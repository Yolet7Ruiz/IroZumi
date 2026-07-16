package com.irozumi.features.gym.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.gym.data.datasource.GymRemoteDataSource
import com.irozumi.features.gym.data.datasource.GymSeedData
import com.irozumi.features.gym.domain.model.AntiBlockTip
import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.model.UserStreak
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.irozumi.core.security.TokenManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class GymViewModel(application: Application) : AndroidViewModel(application) {

    private val dataSource = GymRemoteDataSource()

    private val _exercises = MutableStateFlow<List<GymExercise>>(emptyList())
    val exercises: StateFlow<List<GymExercise>> = _exercises.asStateFlow()

    private val _streak = MutableStateFlow<UserStreak?>(
        UserStreak(currentStreakDays = 0, totalPointsEarned = 0, totalExercisesCompleted = 0, lastCompletedDateString = "")
    )
    val streak: StateFlow<UserStreak?> = _streak.asStateFlow()

    private val _currentTip = MutableStateFlow<AntiBlockTip?>(null)
    val currentTip: StateFlow<AntiBlockTip?> = _currentTip.asStateFlow()

    private val _communityTips = mutableStateListOf<AntiBlockTip>().apply {
        addAll(GymSeedData.initialCommunityTips)
    }
    val communityTipsList: List<AntiBlockTip> get() = _communityTips

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        checkAdminRole()
        loadExercises()
        loadTips()
        getRandomTip()
        if (!_isAdmin.value) loadStreak()
    }

    private fun checkAdminRole() {
        // Verificar si el usuario actual es admin
        _isAdmin.value = TokenManager.currentRole == "admin"
        android.util.Log.e("IroZumi", "Rol detectado: ${if (_isAdmin.value) "ADMIN" else "USUARIO"}")
    }

    fun loadExercises() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val list = dataSource.getExercises()
                _exercises.value = list
                android.util.Log.e("IroZumi", "Ejercicios cargados: ${list.size}")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error cargando ejercicios: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun loadTips() {
        viewModelScope.launch {
            try {
                val tips = dataSource.getTips()
                _communityTips.clear()
                _communityTips.addAll(tips)
                android.util.Log.e("IroZumi", "Tips cargados: ${tips.size}")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error tips: ${e.message}")
            }
        }
    }

    fun loadStreak() {
        viewModelScope.launch {
            try {
                val remote = dataSource.getMyStreak()
                _streak.value = UserStreak(
                    currentStreakDays = remote.currentStreakDays,
                    totalPointsEarned = remote.totalPoints,
                    totalExercisesCompleted = remote.totalPractices,
                    lastCompletedDateString = remote.lastPracticeDate
                )
                android.util.Log.e("IroZumi", "Racha cargada: ${remote.currentStreakDays}")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error racha: ${e.message}")
            }
        }
    }
    fun createExercise(
        title: String,
        description: String?,
        category: String,
        difficulty: String,
        durationMinutes: Int,
        pointsReward: Int,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var base64: String? = null
                if (imageUri != null) {
                    val context = getApplication<Application>()
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                    val resized = android.graphics.Bitmap.createScaledBitmap(bitmap, 512, 512, true)
                    val outputStream = java.io.ByteArrayOutputStream()
                    resized.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                    base64 = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
                }
                dataSource.createExercise(title, description, category, difficulty, durationMinutes, pointsReward, base64)
                android.util.Log.e("IroZumi", "Ejercicio creado exitosamente")
                loadExercises() // Recargar lista
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error creando ejercicio: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun updateExercise(
        exerciseId: String,
        title: String,
        description: String?,
        category: String,
        difficulty: String,
        durationMinutes: Int,
        pointsReward: Int,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                var base64: String? = null
                if (imageUri != null) {
                    val context = getApplication<Application>()
                    val inputStream = context.contentResolver.openInputStream(imageUri)
                    val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                    val resized = android.graphics.Bitmap.createScaledBitmap(bitmap, 512, 512, true)
                    val outputStream = java.io.ByteArrayOutputStream()
                    resized.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                    base64 = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)
                }
                dataSource.updateExercise(exerciseId, title, description, category, difficulty, durationMinutes, pointsReward, base64)
                android.util.Log.e("IroZumi", "Ejercicio actualizado")
                loadExercises()
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error actualizando: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun submitPractice(exerciseId: String, photoUri: Uri) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(photoUri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                val resized = android.graphics.Bitmap.createScaledBitmap(bitmap, 512, 512, true)
                val outputStream = java.io.ByteArrayOutputStream()
                resized.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                val base64 = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.NO_WRAP)

                dataSource.submitPractice(exerciseId, base64, null)
                android.util.Log.e("IroZumi", "Práctica subida correctamente")

                // Actualizar racha local
                val current = _streak.value
                if (current != null) {
                    _streak.value = current.copy(
                        currentStreakDays = current.currentStreakDays + 1,
                        totalPointsEarned = current.totalPointsEarned + (exercises.value.find { it.id == exerciseId }?.pointsReward ?: 15),
                        totalExercisesCompleted = current.totalExercisesCompleted + 1,
                        lastCompletedDateString = photoUri.toString()
                    )
                }
                loadExercises()
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error subiendo práctica: ${e.message}")
            }
        }
    }

    fun resetStreak() {
        val current = _streak.value
        if (current != null) {
            _streak.value = current.copy(currentStreakDays = 0)
        }
    }

    fun getRandomTip() {
        if (_communityTips.isNotEmpty()) {
            _currentTip.value = _communityTips.random()
        }
    }

    fun publishCommunityTip(title: String, description: String, category: String) {
        if (title.isNotBlank() && description.isNotBlank()) {
            viewModelScope.launch {
                try {
                    dataSource.createTip(title, description, category)
                    loadTips()
                    android.util.Log.e("IroZumi", "Tip publicado")
                } catch (e: Exception) {
                    android.util.Log.e("IroZumi", "Error publicando tip: ${e.message}")
                }
            }
        }
    }

    fun deleteExercise(exerciseId: String) {
        viewModelScope.launch {
            try {
                dataSource.deleteExercise(exerciseId)
                android.util.Log.e("IroZumi", "Ejercicio eliminado")
                loadExercises()
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error eliminando: ${e.message}")
            }
        }
    }
}