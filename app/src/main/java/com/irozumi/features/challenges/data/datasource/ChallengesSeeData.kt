package com.irozumi.features.challenges.data.datasource

import com.irozumi.features.challenges.domain.model.*
import java.util.Calendar
import java.util.Date

object ChallengesSeedData {

    val initialChallenges = listOf(
        Challenge(
            id = 1,
            title = "Jueves de reto",
            concept = "Luz y sombra",
            description = "Aprende a dominar los contrastes fuertes usando solo lápiz 2B o grafito puro.",
            status = ChallengeStatus.PROXIMO,
            endDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }.time,
            participantsCount = 18
        ),
        Challenge(
            id = 2,
            title = "Martes de boceto",
            concept = "Manos en movimiento",
            description = "Captura la anatomía de las manos en posturas dinámicas de menos de 2 minutos.",
            status = ChallengeStatus.CERRADO,
            endDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time,
            participantsCount = 54
        ),
        Challenge(
            id = 3,
            title = "Sábado de estilo libre",
            concept = "Creatividad pura",
            description = "Sin tema fijo. Sube tu mejor trazo de la semana para que la comunidad vote.",
            status = ChallengeStatus.ACTIVO,
            endDate = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }.time,
            participantsCount = 110,
            isUserParticipating = true
        )
    )

    val lastWeekWinners = listOf(
        ChallengeWinner("u1", "@Macrudi", "", 120, "Anime", 1, android.R.drawable.star_on),
        ChallengeWinner("u2", "@LeoArt", "", 110, "Realismo", 2, android.R.drawable.star_on),
        ChallengeWinner("u3", "@Artferxu", "", 105, "Dibujo técnico", 3, android.R.drawable.star_on)
    )

    // Datos semilla para tu nueva sección de Materiales Buenos y Baratos
    val initialMaterialTips = listOf(
        MaterialTip(
            id = 1,
            materialName = "Bitácora de dibujo Canson XL Mixed Media",
            category = "Papel",
            approximatePrice = "Económico",
            reviewDescription = "Soporta acuarela ligera, marcadores y grafito sin traspasar la hoja. Es perfecta para estudiantes por su bajo costo y grosor.",
            authorUsername = "@ArteMaster99",
            ratingStars = 5,
            likesCount = 42
        ),
        MaterialTip(
            id = 2,
            materialName = "Lápices de colores Prismacolor Junior",
            category = "Color",
            approximatePrice = "Muy Accesible",
            reviewDescription = "Tienen una mina bastante suave que permite hacer degradados muy decentes sin gastar lo que cuestan los Premier.",
            authorUsername = "@MarinaD",
            ratingStars = 4,
            likesCount = 29
        )
    )
}