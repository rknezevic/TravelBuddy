package com.robertknezevic.travelbuddy.data.auth

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthService {
    private val firebaseAuth : FirebaseAuth by lazy{
        FirebaseAuth.getInstance()
    }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }


    fun getCurrentUser() : FirebaseUser? = firebaseAuth.currentUser

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    onComplete(true, "Logged in successfully")
                } else{
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun register(
        name : String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        if(password == confirmPassword) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if(userId != null) {
                            val userMap = hashMapOf(
                                "name" to name,
                                "lastName" to lastName,
                                "email" to email
                            )
                            firestore.collection("users").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    onComplete(true, "Registered successfully")
                                }
                                .addOnFailureListener { e ->
                                    onComplete(false, e.message)
                                }
                        }else
                        {
                            onComplete(false, "User ID is null")
                        }
                    }else{
                        onComplete(false, task.exception?.message)
                    }
                }
        }else{
            onComplete(false, "Passwords do not match")
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}