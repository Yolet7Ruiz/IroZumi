package com.irozumi.features.gym.data.repository

import com.irozumi.features.gym.domain.model.AntiBlockTip
import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.domain.repository.GymRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GymRepositoryImpl : GymRepository {

    private val tipsList = mutableListOf(
        AntiBlockTip(1, "Estira cada 45 min", "Levántate y mueve las articulaciones.", "Salud"),
        AntiBlockTip(2, "Región de enfoque", "Mira a un punto lejano por 20 segundos.", "Mental")
    )

    override fun getDailyExercise(): Flow<GymExercise> = flow {
        // CORREGIDO: Mantenemos el ID como un String ("1") para cumplir con tu modelo real
        emit(
            GymExercise(
                id = "1",
                name = "Estiramiento de Muñeca",
                description = "Mueve tus muñecas en círculos para aliviar la tensión.",
                isCompleted = false
            )
        )
    }

    override fun getAntiBlockTips(): Flow<List<AntiBlockTip>> = flow {
        emit(tipsList)
    }

    // Cumple con la firma exacta (exerciseId: Int) de la interfaz
    override suspend fun completeExercise(exerciseId: Int) {
        // Aquí puedes usar exerciseId como Int o pasarlo a String según tu Base de Datos futura
    }

    override suspend fun addAntiBlockTip(tip: AntiBlockTip) {
        tipsList.add(tip)
    }
}