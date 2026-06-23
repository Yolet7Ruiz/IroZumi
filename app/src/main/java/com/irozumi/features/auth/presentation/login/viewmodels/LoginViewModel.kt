package com.irozumi.features.auth.presentation.login.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.auth.data.repository.AuthRepositoryImpl
import com.irozumi.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChanged(newValue: String) {
        _state.update { it.copy(email = newValue) }
    }

    fun onPasswordChanged(newValue: String) {
        _state.update { it.copy(password = newValue) }
    }

    fun onLoginSubmitted() {
        val currentEmail = _state.value.email
        val currentPassword = _state.value.password

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            repository.login(currentEmail, currentPassword)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { exception ->
                    _state.update { it.copy(isLoading = false, errorMessage = exception.message ?: "Error desconocido") }
                }
        }
    }
}