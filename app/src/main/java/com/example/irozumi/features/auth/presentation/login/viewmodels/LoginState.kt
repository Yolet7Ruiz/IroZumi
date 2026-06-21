package com.example.irozumi.features.auth.presentation.login.viewmodels

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null // Asegúrate que sea errorMessage y no error
)