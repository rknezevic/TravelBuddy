package com.robertknezevic.travelbuddy.data.auth

import androidx.lifecycle.ViewModel
import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

val firebaseAuth : FirebaseAuth by lazy{
    FirebaseAuth.getInstance()
}
val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

class AuthViewModel : ViewModel(){

    fun getCurrentUser() = firebaseAuth.currentUser

    fun login(email: String, password: String, navController: NavController, context: Context) {
        if (email.isNotBlank() && password.isNotBlank()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Welcome", Toast.LENGTH_SHORT).show()
                        navController.navigate("home")
                    } else {
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
        }
    }

    fun register(name : String, lastName: String, email: String, password: String, context: Context, navController: NavController
    ) {
        if (lastName.isNotBlank() && name.isNotBlank() &&  email.isNotBlank() && password.isNotBlank()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if(userId != null) {
                            val userMap = hashMapOf(
                                "name" to name,
                                "lastName" to lastName,
                                "email" to email,
                                "id" to userId
                            )
                            firestore.collection("users").document(userId)
                                .set(userMap)


                            Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show()
                            val currentUser = getCurrentUser()
                            navController.navigate("login")


                        }
                    }else{
                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
        }
    }
    fun logout() {
        firebaseAuth.signOut()
    }
}