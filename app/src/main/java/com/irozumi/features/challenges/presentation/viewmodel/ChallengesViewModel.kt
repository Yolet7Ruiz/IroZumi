package com.irozumi.features.challenges.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.challenges.presentation.screens.ChallengesUiState
import com.irozumi.features.challenges.domain.model.CustomChallenge
import com.irozumi.features.challenges.domain.model.ParticipantArtwork
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChallengesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ChallengesUiState>(
        ChallengesUiState.Success(
            systemChallenges = emptyList(), // Aquí cargarías tus retos base
            communityChallenges = emptyList(),
            participantArtworks = emptyList(),
            pastWinners = emptyList()
        )
    )
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    // 💡 Envío de entrega de un participante al reto seleccionado
    fun submitArtwork(challengeId: String, title: String, category: String, imageUri: Uri?) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ChallengesUiState.Success) {
                val newArtwork = ParticipantArtwork(
                    id = System.currentTimeMillis().toString(),
                    username = "@UsuarioLogueado",
                    title = title,
                    category = category,
                    imageUri = imageUri,
                    votes = 0
                )
                _uiState.update {
                    currentState.copy(
                        participantArtworks = currentState.participantArtworks + newArtwork
                    )
                }
            }
        }
    }

    // 💡 Gestión de votaciones persistente por ID de entrega
    fun updateVote(artworkId: String, isVoted: Boolean) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ChallengesUiState.Success) {
                val updatedArtworks = currentState.participantArtworks.map { artwork ->
                    if (artwork.id == artworkId) {
                        val newVotes = if (isVoted) artwork.votes + 1 else artwork.votes - 1
                        artwork.copy(votes = newVotes)
                    } else {
                        artwork
                    }
                }
                _uiState.update { currentState.copy(participantArtworks = updatedArtworks) }
            }
        }
    }

    // 💡 Publicación de una nueva dinámica creada por un miembro de la comunidad
    fun createNewChallenge(
        title: String,
        description: String,
        date: String,
        time: String,
        votingDays: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ChallengesUiState.Success) {
                val days = votingDays.toIntOrNull() ?: 3
                val newChallenge = CustomChallenge(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    description = description,
                    startDate = date,
                    startTime = time,
                    votingDays = days,
                    referenceImageUri = imageUri
                )
                _uiState.update {
                    currentState.copy(
                        communityChallenges = currentState.communityChallenges + newChallenge
                    )
                }
            }
        }
    }
}