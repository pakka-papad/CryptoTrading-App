package com.example.cryptotrading

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.cryptotrading.daos.UserDao
import com.example.cryptotrading.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignIn : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task?.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {

            }
        }
        else{
            Log.e("myError", result.resultCode.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val gso =GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setOnClickListener {
            getContent.launch(Intent(googleSignInClient.signInIntent))
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        signInButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        GlobalScope.launch(Dispatchers.IO){
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main){
                updateUI(firebaseUser)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        if(firebaseUser!=null){
            val userDao = UserDao()
            val user = User(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl.toString(),"USD")

            //call addUser function only if the current user is not added in database

            FirebaseFirestore.getInstance().collection("users").document(auth.currentUser!!.uid).get()
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        if(it.result?.exists() == false){
                            userDao.addUser(user)
                        }
                    }
                }

            val homeActivity = Intent(this, Home::class.java)
            startActivity(homeActivity)
            finish()
        }
        else{
            signInButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
}