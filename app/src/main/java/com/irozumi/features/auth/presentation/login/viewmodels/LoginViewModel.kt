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
        _state.update { it.copy(email = newValue, errorMessage = null) }
    }

    fun onPasswordChanged(newValue: String) {
        _state.update { it.copy(password = newValue, errorMessage = null) }
    }

    fun onLoginSubmitted() {
        val currentEmail = _state.value.email
        val currentPassword = _state.value.password

        android.util.Log.e("IroZumi", "🔐 Login: $currentEmail")

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val result = repository.login(currentEmail, currentPassword)
                android.util.Log.e("IroZumi", "✅ Result: $result")
                result.onSuccess {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                result.onFailure { exception ->
                    android.util.Log.e("IroZumi", "❌ ${exception.message}")
                    _state.update { it.copy(isLoading = false, errorMessage = exception.message ?: "Error") }
                }
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "💥 ${e.message}")
            }
        }
    }
}