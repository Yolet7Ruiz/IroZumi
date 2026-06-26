package com.irozumi.features.challenges.data.datasource

import com.irozumi.features.challenges.domain.model.ChallengeDynamic
import com.irozumi.features.challenges.domain.model.ChallengeStage
import com.irozumi.features.challenges.domain.model.ChallengeWinner
import com.irozumi.features.challenges.domain.model.GalleryParticipant

object ChallengesSeeData {

    fun getMockDynamics(): List<ChallengeDynamic> {
        return listOf(
            ChallengeDynamic(
                id = 1,
                title = "Jueves de reto",
                iconEmoji = "🏆",
                theme = "Tema: 'Luz y sombra'",
                description = "Al finalizar se actualiza • 18 registros. Dinámica de contrastes para niveles avanzados y principiantes.",
                referenceImageUrl = null,
                stage = ChallengeStage.UPCOMING,
                timingStatusText = "Próximo reto",
                participantCount = 18
            ),
            ChallengeDynamic(
                id = 2,
                title = "Martes de boceto",
                iconEmoji = "✍️",
                theme = "Tema: 'Manos en movimiento'",
                description = "Dibuja extremidades simulando dinamismo. Ideal como práctica base diaria.",
                referenceImageUrl = "https://example.com/ref_manos.png",
                stage = ChallengeStage.VOTING,
                timingStatusText = "Fase de Votación • 3 días restantes",
                participantCount = 24
            ),
            ChallengeDynamic(
                id = 3,
                title = "Sábado de estilo libre",
                iconEmoji = "⚡",
                theme = "Sin tema fijo, creatividad pura",
                description = "Sube tu mejor ilustración libre. Tienes tiempo límite antes de pasar a la fase de votación de la comunidad.",
                referenceImageUrl = "https://example.com/ref_libre.png",
                stage = ChallengeStage.ACTIVE_UPLOAD,
                timingStatusText = "Quedan 2 días • 18 participantes",
                participantCount = 18,
                isUserParticipating = true
            )
        )
    }

    fun getMockWinners(): List<ChallengeWinner> {
        return listOf(
            ChallengeWinner(1, 1, "@MarinaD", "Categoría Anime", 120, "🥇", null),
            ChallengeWinner(2, 2, "@LuisArt", "Categoría Realismo", 100, "🥈", null),
            ChallengeWinner(3, 3, "@AnaVega", "Categoría Dibujos animados", 95, "🥉", null)
        )
    }

    fun getMockParticipants(): List<GalleryParticipant> {
        return listOf(
            GalleryParticipant(1, "@MarinaD", "Anime", ""),
            GalleryParticipant(2, "@Nek0_Art", "Anime", ""),
            GalleryParticipant(3, "@GokuDraw", "Anime", ""),
            GalleryParticipant(4, "@the_real_art", "Anime", "")
        )
    }
}