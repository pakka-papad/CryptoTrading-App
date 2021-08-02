package com.example.cryptotrading.daos

import android.util.Log
import com.example.cryptotrading.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val auth = Firebase.auth

    fun addUser(user: User?){
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                usersCollection.document(user.uid).set(it).addOnSuccessListener { Log.d("UserDao", "User Added") }
                    .addOnFailureListener { Log.e("UserDao", "User not added + ${user.uid + " " + user.displayName + " " + user.imageUrl}") }
            }
        }
    }

    fun setFavourites(newList: ArrayList<String>){
        newList.let{
            GlobalScope.launch(Dispatchers.IO) {
                val user = usersCollection.document(auth.currentUser!!.uid).get().await().toObject(User::class.java)
                user?.favourites?.clear()
                user?.favourites?.addAll(newList)
                user?.let {
                    usersCollection.document(auth.currentUser!!.uid).set(user)
                }
            }
        }
    }

    fun updateCurrency(newCurrency: String){
        newCurrency.let {
            GlobalScope.launch(Dispatchers.IO) {
                val user = usersCollection.document(auth.currentUser!!.uid).get().await().toObject(User::class.java)
                user?.currency = newCurrency
                user?.let {
                    usersCollection.document(auth.currentUser!!.uid).set(user)
                }
            }
        }
    }
}