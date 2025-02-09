package com.robertknezevic.travelbuddy.ui.screen

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robertknezevic.travelbuddy.viewmodel.AuthViewModel
import androidx.navigation.NavController


@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var loginResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
    ) {
        Text(
            text = "TravelBuddy",
            fontSize = 24.sp
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = {
            authViewModel.login(email, password) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                loginResult = message
            }
        }) {
            Text("Login")
        }
        loginResult?.let {
            Text(it)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "If you don't have an account, register here!")
        Button(onClick = {
            navController.navigate("register")
        }) {
            Text("Register")
        }
    }
}
