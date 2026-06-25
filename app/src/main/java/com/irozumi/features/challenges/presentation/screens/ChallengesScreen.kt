package com.irozumi.features.challenges.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.challenges.presentation.viewmodel.ChallengesViewModel

// 💡 MODELOS LOCALES: Definidos aquí mismo para que no tiren 'Unresolved reference' jamás
data class LocalChallengeItem(
    val id: Int,
    val title: String,
    val theme: String,
    val status: String,
    val infoText: String,
    val statusLabel: String,
    val showNotificationBtn: Boolean = false,
    val showUploadBtn: Boolean = false
)

data class LocalWinnerItem(
    val id: Int,
    val category: String,
    val username: String,
    val likes: Int
)

data class LocalParticipantItem(
    val id: Int,
    val username: String,
    val category: String,
    val imageUrl: String
)

@Suppress("SpellCheckingInspection")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(viewModel: ChallengesViewModel) {
    var selectedCategory by remember { mutableStateOf("Anime") }

    val orangeColor = Color(0xFFFF6F00)
    val tealColor = Color(0xFF00A896)
    val redColor = Color(0xFFE63946)
    val lightOrangeBg = Color(0xFFFFF2E6)
    val backgroundGray = Color(0xFFF8F9FA)
    val textDark = Color(0xFF1A1A1A)
    val textMuted = Color(0xFF7F8C8D)

    // 💡 DATOS ESTRUCTURADOS: Hardcodeados localmente para clonar tus imágenes sin depender de estados rotos del ViewModel
    val localChallenges = remember {
        listOf(
            LocalChallengeItem(1, "Jueves de reto", "Tema: 'Luz y sombra'", "próximo", "Al finalizar se actualiza • 18 registros", "Activar", showNotificationBtn = true),
            LocalChallengeItem(2, "Martes de boceto", "Tema: 'Manos en movimiento'", "Cerrado", "Inicia 2 días - 18 participantes", "No activo", showNotificationBtn = true),
            LocalChallengeItem(3, "Sábado de estilo libre", "Sin tema fijo, creatividad pura.", "Activo", "Todos los sábados", "Participando", showUploadBtn = true)
        )
    }

    val localWinners = remember {
        listOf(
            LocalWinnerItem(1, "Anime", "@MarinaD", 121),
            LocalWinnerItem(2, "Realismo", "@LuisArt", 110),
            LocalWinnerItem(3, "Tradicional", "@AnArt99", 150)
        )
    }

    val localCategories = listOf("Acuarela", "Anime", "Sangre", "Realismo")

    val localParticipants = remember {
        listOf(
            LocalParticipantItem(1, "@MarinaD", "Anime", ""),
            LocalParticipantItem(2, "@Nek0_Art", "Anime", ""),
            LocalParticipantItem(3, "@GokuDraw", "Anime", ""),
            LocalParticipantItem(4, "@ thereal_art", "Anime", "")
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(backgroundGray)) {

        // TOP BAR
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { /* Volver */ },
                shape = RoundedCornerShape(20.dp),
                color = backgroundGray,
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(14.dp), tint = textDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Atrás", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = textDark)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Retos semanales", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textDark)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // LISTA DE RETOS
            items(localChallenges) { challenge ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val indicatorColor = when(challenge.status) {
                                    "próximo" -> orangeColor
                                    "Cerrado" -> redColor
                                    else -> tealColor
                                }
                                Box(modifier = Modifier.width(3.dp).height(22.dp).background(indicatorColor, RoundedCornerShape(2.dp)))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(challenge.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = textDark)
                            }

                            val (badgeBg, badgeText) = when(challenge.status) {
                                "próximo" -> Pair(Color(0xFFFFE6D5), orangeColor)
                                "Cerrado" -> Pair(Color(0xFFFFE6E6), redColor)
                                else -> Pair(Color(0xFFE0F4F1), tealColor)
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(badgeBg).padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(challenge.status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeText)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(challenge.theme, color = textMuted, fontSize = 14.sp)
                        Text(challenge.infoText, color = Color.LightGray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        if (challenge.showNotificationBtn) {
                            Row(
                                modifier = Modifier.fillMaxWidth().background(backgroundGray, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Activar Notificación", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textDark)
                                Text("Activar", color = orangeColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
                            }
                        }

                        if (challenge.showUploadBtn) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {},
                                    colors = ButtonDefaults.buttonColors(containerColor = lightOrangeBg),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                                ) {
                                    Text("Participando", color = orangeColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Sube tu dibujo", fontSize = 11.sp, color = orangeColor, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier.size(38.dp).background(lightOrangeBg, CircleShape).clickable { },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, tint = orangeColor, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECCIÓN GANADORES
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆 ", fontSize = 16.sp)
                            Text("Ganadores", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                        }
                        Text("12 de Julio del 2026", color = Color.LightGray, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            localWinners.forEach { winner ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(winner.category, fontSize = 11.sp, color = textMuted, fontWeight = FontWeight.Medium, maxLines = 1)
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Box(modifier = Modifier.size(44.dp).background(backgroundGray, CircleShape))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(winner.username, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textDark)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = redColor, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${winner.likes}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textDark)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECCIÓN PARTICIPANTES
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("👥 ", fontSize = 16.sp)
                            Text("Participantes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                        }
                        Text("Selecciona las categorías", color = textMuted, fontSize = 13.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            localCategories.forEach { cat ->
                                val isSelected = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFFECEFF1) else backgroundGray)
                                        .clickable { selectedCategory = cat }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(cat, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) textDark else textMuted)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Grid modular de 2 columnas sin errores de contexto
                        val chunks = localParticipants.chunked(2)
                        chunks.forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                rowItems.forEach { participant ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(150.dp)
                                            .clip(RoundedCornerShape(18.dp))
                                            .background(Color(0xFFE0E0E0))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(10.dp)
                                                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(16.dp).background(Color.LightGray, CircleShape))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(participant.username, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = textDark)
                                        }
                                    }
                                }
                                if (rowItems.size < 2) { Spacer(modifier = Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            }
        }
    }
}