package com.irozumi.features.challenges.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.challenges.data.datasource.ChallengesRemoteDataSource
import com.irozumi.features.challenges.domain.model.Challenge
import com.irozumi.features.challenges.presentation.screens.ChallengesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.irozumi.features.challenges.domain.model.ChallengeStatus
class ChallengesViewModel : ViewModel() {

    private val dataSource = ChallengesRemoteDataSource()

    private val _uiState = MutableStateFlow<ChallengesUiState>(ChallengesUiState.Success())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        loadChallenges()
    }

    private fun loadChallenges() {
        viewModelScope.launch {
            try {
                val remote = dataSource.getActiveChallenges()
                val challenges = remote.map {
                    Challenge(
                        id = it.id,
                        title = it.title,
                        concept = it.theme ?: "",
                        description = it.description ?: "",
                        status = ChallengeStatus.ACTIVO,
                        endDate = java.text.SimpleDateFormat("yyyy-MM-dd").parse(it.endDate) ?: java.util.Date(),
                        participantsCount = 0
                    )
                }
                _uiState.update { (it as? ChallengesUiState.Success)?.copy(systemChallenges = challenges) ?: it }
            } catch (e: Exception) { }
        }
    }

    fun loadSubmissions(challengeId: String) {
        viewModelScope.launch {
            try {
                val remote = dataSource.getSubmissions(challengeId)
                val artworks = remote.map {
                    com.irozumi.features.challenges.domain.model.ParticipantArtwork(
                        id = it.id,
                        username = it.username,
                        title = it.title ?: "",
                        category = it.category ?: "",
                        imageUri = null,
                        votes = it.votes
                    )
                }
                _uiState.update { (it as? ChallengesUiState.Success)?.copy(participantArtworks = artworks) ?: it }
            } catch (e: Exception) { }
        }
    }

    fun submitArtwork(challengeId: String, title: String, category: String, imageBase64: String) {
        viewModelScope.launch {
            try {
                dataSource.submitArtwork(challengeId, title, category, imageBase64)
                loadSubmissions(challengeId)
            } catch (e: Exception) { }
        }
    }

    fun updateVote(submissionId: String) {
        viewModelScope.launch {
            try {
                dataSource.voteSubmission(submissionId)
            } catch (e: Exception) { }
        }
    }

    fun createNewChallenge(title: String, description: String, date: String, time: String, votingDays: String, imageBase64: String) {
        viewModelScope.launch {
            try {
                dataSource.createChallenge(title, description, date, time, imageBase64, votingDays)
                loadChallenges()
            } catch (e: Exception) { }
        }
    }
}

