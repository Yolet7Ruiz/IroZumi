package com.irozumi.features.auth.presentation.register.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegisterViewModel : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onUsernameChanged(newValue: String) {
        _state.update { it.copy(username = newValue) }
    }

    fun onEmailChanged(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPasswordChanged(newValue: String) {
        _state.update { it.copy(password = newValue) }
    }

    fun onArtisticLevelChanged(newValue: String) {
        _state.update { it.copy(artisticLevel = newValue) }
    }

    fun onTermsAcceptedChanged(newValue: Boolean) {
        _state.update { it.copy(acceptedTerms = newValue) }
    }

    fun onRegisterSubmitted() {
        // Tu lógica de registro aquí usando state.value.artisticLevel y state.value.acceptedTerms
    }
}