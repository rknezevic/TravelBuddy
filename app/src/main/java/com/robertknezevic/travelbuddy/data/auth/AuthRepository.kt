package com.robertknezevic.travelbuddy.data.auth

class AuthRepository(private val authService: AuthService) {

    fun getCurrentUser() = authService.getCurrentUser()

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        authService.login(email, password, onComplete)
    }

    fun register(
        name : String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        authService.register(name, lastName, email, password,confirmPassword, onComplete)
    }

    fun logout() {
        authService.logout()
    }
}
