package com.irozumi.features.challenges.domain.model

data class ParticipanteVotacion(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val fotoUrl: String = "",
    val votosIniciales: Int,
    val tieneMiVoto: Boolean = false
)