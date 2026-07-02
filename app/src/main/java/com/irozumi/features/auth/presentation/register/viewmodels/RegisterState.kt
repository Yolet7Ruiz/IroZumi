package com.irozumi.features.auth.presentation.register.viewmodels

data class RegisterState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val artisticLevel: String = "Principiante", // Valor inicial por defecto
    val acceptedTerms: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)