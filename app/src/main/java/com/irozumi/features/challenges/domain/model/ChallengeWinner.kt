package com.irozumi.features.challenges.domain.model

data class ChallengeWinner(
    val userId: String,
    val username: String,       // Ej: "@MarinaD"
    val userAvatarUrl: String,
    val votesCount: Int,        // Cantidad de me gusta/votos recibidos
    val category: String,       // Ej: "Acuarela", "Realismo"
    val rankPosition: Int,      // 1 = Primer Lugar, 2 = Segundo, 3 = Tercer Lugar
    val badgeIconRes: Int       // Recurso de la insignia otorgada (Oro, Plata, Bronce)
)