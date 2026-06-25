package com.irozumi.features.challenges.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.irozumi.features.challenges.data.datasource.ChallengesSeedData
import com.irozumi.features.challenges.domain.model.Challenge
import com.irozumi.features.challenges.domain.model.ChallengeWinner
import com.irozumi.features.challenges.domain.model.MaterialTip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChallengesViewModel : ViewModel() {

    private val _challengesList = MutableStateFlow<List<Challenge>>(emptyList())
    val challengesList: StateFlow<List<Challenge>> = _challengesList.asStateFlow()

    private val _winnersList = MutableStateFlow<List<ChallengeWinner>>(emptyList())
    val winnersList: StateFlow<List<ChallengeWinner>> = _winnersList.asStateFlow()

    // Manejo reactivo de la lista de materiales económicos compartidos
    private val _materialTips = mutableStateListOf<MaterialTip>().apply {
        addAll(ChallengesSeedData.initialMaterialTips)
    }
    val materialTipsList: List<MaterialTip> get() = _materialTips

    init {
        loadChallengesData()
    }

    private fun loadChallengesData() {
        // Inicialmente jalamos de los datos semilla fijos de tus vistas
        _challengesList.value = ChallengesSeedData.initialChallenges
        _winnersList.value = ChallengesSeedData.lastWeekWinners
    }

    // 💡 Función para cuando el usuario presiona "Sube tu dibujo" en una dinámica ACTIVA
    fun uploadDrawingToChallenge(challengeId: Int, imageUri: Uri) {
        _challengesList.value = _challengesList.value.map { challenge ->
            if (challenge.id == challengeId) {
                challenge.copy(
                    participantsCount = challenge.participantsCount + 1,
                    isUserParticipating = true
                )
            } else {
                challenge
            }
        }
        // Aquí se disparará la notificación de guardado exitoso en el futuro
    }

    // 💡 Nueva función interactiva: Permite a los usuarios añadir un tip de materiales calidad-precio
    fun publishMaterialTip(name: String, category: String, price: String, review: String) {
        if (name.isNotBlank() && review.isNotBlank()) {
            val newTip = MaterialTip(
                id = _materialTips.size + 1,
                materialName = name,
                category = category,
                approximatePrice = price,
                reviewDescription = review,
                authorUsername = "Tú (Artista)",
                ratingStars = 5
            )
            _materialTips.add(0, newTip) // Aparece inmediatamente arriba en el feed
        }
    }
}