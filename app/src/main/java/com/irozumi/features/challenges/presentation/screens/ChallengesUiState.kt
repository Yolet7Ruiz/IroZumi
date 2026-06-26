package com.irozumi.features.challenges.presentation.screens

import com.irozumi.features.challenges.domain.model.CustomChallenge
import com.irozumi.features.challenges.domain.model.ParticipantArtwork
import com.irozumi.features.challenges.domain.model.ChallengeWinner

sealed interface ChallengesUiState {
    object Loading : ChallengesUiState

    data class Success(
        val systemChallenges: List<com.irozumi.features.challenges.domain.model.Challenge> = emptyList(),
        val communityChallenges: List<CustomChallenge> = emptyList(),
        val participantArtworks: List<ParticipantArtwork> = emptyList(),
        val pastWinners: List<ChallengeWinner> = emptyList()
    ) : ChallengesUiState

    data class Error(val message: String) : ChallengesUiState
}