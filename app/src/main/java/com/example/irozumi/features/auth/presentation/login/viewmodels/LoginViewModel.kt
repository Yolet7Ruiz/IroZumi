package com.example.irozumi.features.auth.presentation.login.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    // Estas son las funciones que la vista buscará
    fun onEmailChanged(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPasswordChanged(newValue: String) {
        _state.update { it.copy(password = newValue) }
    }

    fun onLoginSubmitted() {
        // Lógica de login aquí
    }
}