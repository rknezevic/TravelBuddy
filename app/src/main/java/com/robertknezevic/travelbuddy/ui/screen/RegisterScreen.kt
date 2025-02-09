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
import androidx.navigation.NavController
import com.robertknezevic.travelbuddy.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController : NavController, authViewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    var registerResult by remember { mutableStateOf<String?>(null) }

    Column {
        Text(text = "Please register first so you could use this app!")
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") }
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
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = {
            authViewModel.register(name, lastName, email, password, confirmPassword) { success, message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                registerResult = message
                if(success){
                    navController.navigate("login")
                }

            }
        }) {
            Text("Register")
        }
        registerResult?.let {
            Text(it)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Already have an account? Login here!")
        Button(onClick = {
            navController.navigate("login")
        }) {

        }
    }
}
