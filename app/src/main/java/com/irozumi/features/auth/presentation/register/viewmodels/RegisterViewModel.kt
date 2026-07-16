package com.irozumi.features.auth.presentation.register.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.auth.data.repository.AuthRepositoryImpl
import com.irozumi.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onUsernameChanged(newValue: String) {
        _state.update { it.copy(username = newValue, errorMessage = null) }
    }

    fun onEmailChanged(newValue: String) {
        _state.update { it.copy(email = newValue, errorMessage = null) }
    }

    fun onPasswordChanged(newValue: String) {
        _state.update { it.copy(password = newValue, errorMessage = null) }
    }

    fun onArtisticLevelChanged(newValue: String) {
        _state.update { it.copy(artisticLevel = newValue) }
    }

    fun onTermsAcceptedChanged(newValue: Boolean) {
        _state.update { it.copy(acceptedTerms = newValue) }
    }

    fun onRegisterSubmitted() {
        if (!_state.value.acceptedTerms) {
            _state.update { it.copy(errorMessage = "Debes aceptar los términos y condiciones") }
            return
        }

        val username = _state.value.username
        val email = _state.value.email
        val password = _state.value.password

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            repository.register(username, email, password, _state.value.artisticLevel)
                .onSuccess { _state.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al registrarse") } }
        }
    }
}