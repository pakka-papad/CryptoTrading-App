package com.example.cryptotrading

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptotrading.adapters.PriceDetailsAdapter
import com.example.cryptotrading.models.DayStats
import com.example.cryptotrading.models.PriceData
import com.google.android.material.datepicker.MaterialDatePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var cryptoName: String
    private lateinit var assetIdBase: HashMap<String,String>
    private lateinit var timeStart: String
    private lateinit var timeEnd: String
    private lateinit var util: Util
    private lateinit var statsList: ArrayList<DayStats>
    private lateinit var adapter: PriceDetailsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var currency: String
    private var default: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statsList = ArrayList()
        adapter = PriceDetailsAdapter()
        util = Util()
        val intent = intent
        cryptoName = intent.getStringExtra("cryptoName").toString()
        currency = intent.getStringExtra("currency").toString()
        Log.d("Main", cryptoName)
        recyclerView = findViewById<RecyclerView>(R.id.stats_recyclerView)
        progressBar = findViewById(R.id.progress_bar_2)
        val cryptoNameView = findViewById<TextView>(R.id.crypto_name)
        cryptoNameView.text = cryptoName


        assetIdBase = HashMap()
        assetIdBase["Bitcoin"] = "BTC"
        assetIdBase["Ethereum"] = "ETH"
        assetIdBase["Tether"] = "USDT"
        assetIdBase["Dogecoin"] = "DOGE"
        assetIdBase["Litecoin"] = "LTC"
        assetIdBase["Monero"] = "XMR"
        assetIdBase["Cardano"] = "ADA"
        assetIdBase["Uniswap"] = "UNI"
        assetIdBase["Chainlink"] = "LINK"
        assetIdBase["Binance Coin"] = "BNB"
        assetIdBase["Ripple"] = "XRP"
        assetIdBase["Dash"] = "DASH"
        assetIdBase["Stellar Lumens"] = "XLM"
        assetIdBase["Bytecoin"] = "BCN"
        assetIdBase["Electroneum"] = "ETN"

        val calenderObject = Calendar.getInstance()
        timeEnd = util.getDateStringed(calenderObject.get(Calendar.YEAR),
            calenderObject.get(Calendar.MONTH),
            calenderObject.get(Calendar.DATE))
        timeEnd += "T23:59:59"
        calenderObject.add(Calendar.DATE,-6)
        timeStart = util.getDateStringed(calenderObject.get(Calendar.YEAR),
            calenderObject.get(Calendar.MONTH),
            calenderObject.get(Calendar.DATE))
        timeStart += "T00:00:00"
        
        loadData()

        findViewById<Button>(R.id.select_date_range).setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun loadData(){
        val asset_id_base = assetIdBase[cryptoName]!!
        val asset_id_quote = currency
        val options = HashMap<String,String>()
        options["period_id"] = "1DAY"
        options["time_start"] = timeStart
        options["time_end"] = timeEnd

        val retrofit2 = Retrofit.Builder()
            .baseUrl("https://rest.coinapi.io/v1/exchangerate/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit2.create(Api::class.java)
        val call = api.getRangeData(asset_id_base,asset_id_quote,options)
        call.enqueue(object: Callback<ArrayList<PriceData>>{

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ArrayList<PriceData>>,
                response: Response<ArrayList<PriceData>>
            ) {
                val responseObject = response.body()
                val list = util.getDayStats(responseObject,currency)
                statsList.clear()
                statsList.addAll(list)
                try{
                    if(default){
                        val todayHighest = responseObject?.get(responseObject.size-1)!!.rate_high
                        val high = findViewById<TextView>(R.id.current_price)
                        high.text = "Today's Highest: $todayHighest $currency"
                        setupRecyclerView()
                        default = false
                    }
                    else{
                        progressBar.visibility = View.GONE
                        adapter.updateRecyclerView(statsList)
                        recyclerView.visibility = View.VISIBLE
                    }
                }
                catch (e: Exception){
                    Log.e("MainActivity loadData()", e.toString())
                    Toast.makeText(this@MainActivity,"No data available",Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<PriceData>>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Data Could Not Be Retrieved",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showDateRangePicker(){
        val rangePickerBuilder = MaterialDatePicker.Builder.dateRangePicker()
        val picker = rangePickerBuilder.build()
        picker.show(supportFragmentManager,picker.toString())
        picker.addOnPositiveButtonClickListener {
            var start = util.getDateStringed(it.first)
            start += "T00:00:00"
            var end = util.getDateStringed(it.second)
            end += "T23:59:59"
            timeStart = start
            timeEnd = end
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            loadData()
        }
    }

    private fun setupRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        adapter.updateRecyclerView(statsList)
    }
}