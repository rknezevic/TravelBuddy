package com.robertknezevic.travelbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertknezevic.travelbuddy.data.auth.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun getCurrentUser() = authRepository.getCurrentUser()

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            authRepository.login(email, password, onComplete)
        }
    }

    fun register(
        name: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            authRepository.register(name, lastName, email, password, confirmPassword, onComplete)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
