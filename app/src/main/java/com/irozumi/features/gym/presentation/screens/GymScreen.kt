package com.irozumi.features.gym.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel

@Composable
fun GymScreen(viewModel: GymViewModel) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Tu Ejercicio Diario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = state.dailyExercise.name, fontWeight = FontWeight.Bold)
                    Text(text = state.dailyExercise.description)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Tips Anti-Bloqueo",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(state.antiBlockTips) { tip ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                ListItem(
                    headlineContent = { Text(tip.title) },
                    supportingContent = { Text(tip.content) }
                )
            }
        }
    }
}