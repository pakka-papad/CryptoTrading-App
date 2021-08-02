package com.example.cryptotrading

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptotrading.adapters.FavouritesAdapter
import com.example.cryptotrading.adapters.IFavouritesAdapter
import com.example.cryptotrading.daos.UserDao
import com.example.cryptotrading.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class Home : AppCompatActivity(), IFavouritesAdapter {
    private lateinit var userDao: UserDao
    private lateinit var selected: BooleanArray
    private lateinit var favourites: ArrayList<String>
    private lateinit var favourites0: ArrayList<String>
    private lateinit var adapter: FavouritesAdapter
    private lateinit var profilePicture: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currency: String
    private lateinit var currency0: String
    private var selectedCurrency: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        profilePicture = findViewById(R.id.profile_button)
        val progressBar2 = findViewById<ProgressBar>(R.id.progress_bar_2)
        progressBar2.visibility = View.VISIBLE
        userDao = UserDao()
        favourites = ArrayList()
        currency = ""
        currency0 = currency
        db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        auth = Firebase.auth
        usersCollection.document(auth.currentUser!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                try{
                    val user = it.result?.toObject(User::class.java)
                    currency = user!!.currency
                    currency0 = currency
                    favourites.addAll(user.favourites)
                    loadProfilePicture(user)
                }
                catch (e: Exception){
                    Log.e("Home", "Line 66 $e")
                }
                setup()
                progressBar2.visibility = View.GONE
            }
        }.addOnFailureListener {
            Log.e("Home","Line 72 $it")
            //Handle error
        }

        profilePicture.setOnClickListener {
            setupProfileOptions()
        }

    }

    private fun loadProfilePicture(user: User){
        val url = user.imageUrl
        Glide.with(this).load(url.toUri()).into(profilePicture)
    }

    private fun setup(){
        val list = resources.getStringArray(R.array.cryptocurrency_list)
        favourites0 = ArrayList()
        favourites0.addAll(favourites)
        val listSize = list.size
        selected = BooleanArray(listSize)
        for(i in 0 until listSize){
            selected[i] = favourites.contains(list[i])
        }

        val currencyList = resources.getStringArray(R.array.currencyList)
        val currencyListSize = currencyList.size
        for( i in 0 until currencyListSize){
            if(currencyList[i]==currency){
                selectedCurrency = i
                break
            }
        }
        currency0 = currency

        setupRecyclerView()
        val favouritesButton = findViewById<ImageButton>(R.id.add_favourites)
        favouritesButton.setOnClickListener {
            setupMultipleOptionChooser()
        }

        findViewById<ImageButton>(R.id.choose_currency).setOnClickListener {
            setupCurrencyChooser()
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FavouritesAdapter(this)
        recyclerView.adapter = adapter
        adapter.updateRecyclerView(favourites)
    }

    private fun setupMultipleOptionChooser() {
        val list = resources.getStringArray(R.array.cryptocurrency_list)
        val listSize = list.size
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cryptocurrencies")
        builder.setMultiChoiceItems(R.array.cryptocurrency_list, selected, DialogInterface.OnMultiChoiceClickListener{ dialog, which, isChecked ->
            if (isChecked){
                if(!favourites.contains(list[which])){
                    favourites.add(list[which])
                }
            }
            else{
                if(favourites.contains(list[which])){
                    favourites.remove(list[which])
                }
            }
        })
        builder.setCancelable(false)
        builder.setPositiveButton("ADD",DialogInterface.OnClickListener { dialog, which ->
            favourites0.clear()
            favourites0.addAll(favourites)
            userDao.setFavourites(favourites)
            adapter.updateRecyclerView(favourites)
        })
        builder.setNegativeButton("DISMISS", DialogInterface.OnClickListener { dialog, which ->
            favourites.clear()
            favourites.addAll(favourites0)
            for(i in 0 until listSize){
                selected[i] = favourites.contains(list[i])
            }
            dialog.dismiss()
        })

        val chooserDialog = builder.create()
        chooserDialog.show()
    }

    private fun setupProfileOptions(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(auth.currentUser!!.displayName)
        builder.setItems(R.array.profile_options,DialogInterface.OnClickListener{ dialog, which ->
            if(which==0){
                auth.signOut()
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
                finish()
            }
        })
        builder.setNegativeButton("DISMISS", DialogInterface.OnClickListener{ dialog, which ->
            dialog.dismiss()
        })

        val profileDialog = builder.create()
        profileDialog.show()
    }

    private fun setupCurrencyChooser(){
        val currencyList = resources.getStringArray(R.array.currencyList)
        val currencyListSize = currencyList.size
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Currency")
        builder.setSingleChoiceItems(R.array.currencyList,selectedCurrency,DialogInterface.OnClickListener { dialog, which ->
            currency = currencyList[which]
        })
        builder.setCancelable(false)
        builder.setPositiveButton("OK",DialogInterface.OnClickListener{ dialog, which ->
            for(i in 0 until currencyListSize){
                if (currencyList[i]==currency){
                    selectedCurrency = i
                    break
                }
            }
            currency0 = currency
            userDao.updateCurrency(currency)
            dialog.dismiss()
        })
        builder.setNegativeButton("DISSMISS",DialogInterface.OnClickListener{ dialog, which ->
            currency = currency0
            for(i in 0 until currencyListSize){
                if (currencyList[i]==currency){
                    selectedCurrency = i
                    break
                }
            }
            dialog.dismiss()
        })
        val currencyChooserDialog = builder.create()
        currencyChooserDialog.show()
    }

    override fun onItemClicked(cryptoName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("cryptoName", cryptoName)
        intent.putExtra("currency",currency)
        startActivity(intent)
    }
}