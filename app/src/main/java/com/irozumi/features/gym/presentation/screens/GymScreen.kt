package com.irozumi.features.gym.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image // 💡 NUEVO IMPORT para renderizar la guía visual
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // 💡 NUEVO IMPORT para cargar el recurso drawable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymScreen(
    viewModel: GymViewModel
) {
    val exercises by viewModel.exercises.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val currentTip by viewModel.currentTip.collectAsState()

    var showAddTipDialog by remember { mutableStateOf(false) }
    var selectedExerciseIdForPhoto by remember { mutableStateOf<Int?>(null) }

    val brandBlue = Color(0xFF2F80ED)
    val accentOrange = Color(0xFFF2994A)
    val textDark = Color(0xFF3D405B)
    val backgroundColor = Color(0xFFF4F6F9)

    val drawingPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && selectedExerciseIdForPhoto != null) {
            viewModel.completeExerciseWithPhoto(selectedExerciseIdForPhoto!!, uri)
            selectedExerciseIdForPhoto = null
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. DASHBOARD DE RECOMPENSAS Y RACHAS
        streak?.let { userStreak ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Racha",
                                    tint = if (userStreak.currentStreakDays > 0) accentOrange else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${userStreak.currentStreakDays} Dias de Racha",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = textDark
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(brandBlue.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = brandBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${userStreak.totalPointsEarned} Pts", color = brandBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (userStreak.currentStreakDays > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFF3CD))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFF856404), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "¡Alerta! Si pasas 3 dias sin subir la foto de tus trazos diarios, tu racha volvera a cero.",
                                        color = Color(0xFF856404),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Text(
                            text = if (userStreak.currentStreakDays == 0) "Sube la foto de tu dibujo hoy para iniciar tu racha." else "Sigue subiendo tus comprobantes diarios para mantener el fuego encendido.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = { if (userStreak.currentStreakDays > 0) 0.7f else 0.0f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = brandBlue,
                                trackColor = brandBlue.copy(alpha = 0.1f),
                            )
                            if (userStreak.currentStreakDays > 0) {
                                TextButton(onClick = { viewModel.resetStreak() }) {
                                    Text("Forzar Perder Racha", color = Color.Red, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. BANNER DE CONSEJOS (SIN IMAGEN)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E202C))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Inspiracion Anti-Bloqueo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        IconButton(onClick = { viewModel.getRandomTip() }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Refresh, contentDescription = "Cambiar tip", tint = Color.LightGray)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentTip?.title ?: "Cargando sabiduria...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentTip?.description ?: "Espera un momento...",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    if (currentTip?.author != "Sistema") {
                        Text(text = "Por: ${currentTip?.author}", color = brandBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showAddTipDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compartir mi Tip Anti-Bloqueo", fontSize = 13.sp)
                    }
                }
            }
        }

        // TÍTULO SECCIÓN EJERCICIOS
        item {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = textDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gimnasio de Trazos Diarios", fontWeight = FontWeight.Black, fontSize = 18.sp, color = textDark)
            }
        }

        // LISTA DE EJERCICIOS
        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                brandBlue = brandBlue,
                textDark = textDark,
                onUploadPhotoClick = {
                    selectedExerciseIdForPhoto = exercise.id
                    drawingPhotoLauncher.launch("image/*")
                }
            )
        }

        // SECCIÓN COMUNIDAD
        item { Text("Consejos de la Comunidad", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark, modifier = Modifier.padding(top = 12.dp)) }

        items(viewModel.communityTipsList) { tip ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = brandBlue)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = tip.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                        Text(text = tip.description, fontSize = 12.sp, color = Color.Gray)
                        Text(text = "Por: ${tip.author} • ${tip.category}", fontSize = 10.sp, color = brandBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DIÁLOGO PARA CREAR NUEVOS TIPS
    if (showAddTipDialog) {
        var tipTitle by remember { mutableStateOf("") }
        var tipDesc by remember { mutableStateOf("") }
        var tipCat by remember { mutableStateOf("Creatividad") }

        AlertDialog(
            onDismissRequest = { showAddTipDialog = false },
            title = { Text("Nuevo Tip Anti-Bloqueo", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = tipTitle, onValueChange = { tipTitle = it }, label = { Text("Titulo corto") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tipDesc, onValueChange = { tipDesc = it }, label = { Text("Describe tu consejo...") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Creatividad", "Fisico", "Digital").forEach { cat ->
                            FilterChip(selected = tipCat == cat, onClick = { tipCat = cat }, label = { Text(cat) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.publishCommunityTip(tipTitle, tipDesc, tipCat); showAddTipDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                ) { Text("Publicar", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showAddTipDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun ExerciseCard(
    exercise: GymExercise,
    brandBlue: Color,
    textDark: Color,
    onUploadPhotoClick: () -> Unit
) {
    val cardBackground by animateColorAsState(
        targetValue = if (exercise.isCompleted) Color(0xFFE8F5E9) else Color.White,
        label = "bgColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (exercise.isCompleted) Color(0xFF81C784) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        // 💡 CORREGIDO: Cambiamos a Column para poder inyectar la imagen abajo de los textos de forma limpia y ordenada
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(brandBlue.copy(alpha = 0.1f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(exercise.category, color = brandBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color.LightGray.copy(alpha = 0.3f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(exercise.difficulty, color = Color.DarkGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = exercise.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = exercise.description, color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (exercise.isCompleted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Completado", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                        Text("¡Verificado!", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onUploadPhotoClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Subir dibujo", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 💡 CORREGIDO: Muestra la guía visual de referencia solo si existe un ID de imagen asignado en el SeedData
            exercise.referenceImageRes?.let { imageRes ->
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Imagen de referencia del ejercicio",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp) // Altura ideal para previsualizar el trazo sin ocupar toda la pantalla
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Tiempo estimado: ${exercise.durationMinutes} min", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textDark)
                Text(text = "+${exercise.pointsReward} Pts", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = brandBlue)
            }
        }
    }
}