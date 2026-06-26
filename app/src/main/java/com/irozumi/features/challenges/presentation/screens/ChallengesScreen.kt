package com.irozumi.features.challenges.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.irozumi.features.challenges.presentation.viewmodel.ChallengesViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    viewModel: ChallengesViewModel,
    navController: NavHostController
) {
    val brandBlue = Color(0xFF2F80ED)
    val brandOrange = Color(0xFFFF6B00)
    val backgroundColor = Color(0xFFF4F6F9)
    val scrollState = rememberScrollState()

    // --- ESTADOS DE DIÁLOGOS INTERACTIVOS ---
    var showCreateChallengeDialog by remember { mutableStateOf(false) }
    var showParticipateDialog by remember { mutableStateOf(false) }
    var showInstructionsDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var selectedChallengeTitle by remember { mutableStateOf("") }
    var selectedChallengeTheme by remember { mutableStateOf("") }

    // Formulario de Participación
    var artworkName by remember { mutableStateOf("") }
    var artworkCategory by remember { mutableStateOf("Anime") }
    var userImageUri by remember { mutableStateOf<Uri?>(null) }

    // Formulario de Nueva Dinámica Creada por el Usuario
    var newChallengeTitle by remember { mutableStateOf("") }
    var newChallengeDesc by remember { mutableStateOf("") }
    var selectedDateText by remember { mutableStateOf("Seleccionar fecha de inicio") }
    var selectedTimeText by remember { mutableStateOf("Seleccionar hora de inicio") }
    var newChallengeVoteDays by remember { mutableStateOf("") }
    var referenceImageUri by remember { mutableStateOf<Uri?>(null) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    val sampleGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        userImageUri = uri
    }
    val referenceGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        referenceImageUri = uri
    }

    // Colectamos el estado real del flujo (según archivo ChallengesUiState)
    val uiStateLifecycle by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Brush, contentDescription = null, tint = brandBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retos semanales", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF333333))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Brush, contentDescription = "Atrás", tint = brandBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                modifier = Modifier.height(56.dp)
            )
        },
        // 💡 SOLUCIÓN A LA MANCHA AZUL: Convertido en un hermoso botón flotante extendido (FAB)
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateChallengeDialog = true },
                containerColor = brandBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp)) },
                text = { Text("Diseñar dinámica", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->

        when (val state = uiStateLifecycle) {
            is ChallengesUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = brandBlue)
                }
            }
            is ChallengesUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is ChallengesUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    // 1. SECCIÓN DE RETOS DISPONIBLES (MOCK ORIGINAL)
                    item {
                        InteractiveChallengeCard(
                            title = "Jueves de reto",
                            theme = "Luz y sombra",
                            details = "Al finalizar se votará • 74 registros",
                            statusText = "Urgente",
                            statusColor = brandOrange,
                            icon = Icons.Default.FlashOn,
                            brandBlue = brandBlue,
                            brandOrange = brandOrange,
                            onParticipateClick = {
                                selectedChallengeTitle = "Jueves de reto"
                                showParticipateDialog = true
                            },
                            onInstructionsClick = {
                                selectedChallengeTitle = "Jueves de reto"
                                selectedChallengeTheme = "Consiste en usar contrastes altos de blanco y negro, enfocando una fuente lumínica lateral."
                                showInstructionsDialog = true
                            }
                        )
                    }

                    item {
                        InteractiveChallengeCard(
                            title = "Martes de boceto",
                            theme = "Manos en movimiento",
                            details = "Inicia 2 días • 11 participantes",
                            statusText = "Activo",
                            statusColor = Color(0xFF27AE60),
                            icon = Icons.Default.Brush,
                            brandBlue = brandBlue,
                            brandOrange = brandOrange,
                            onParticipateClick = {
                                selectedChallengeTitle = "Martes de boceto"
                                showParticipateDialog = true
                            },
                            onInstructionsClick = {
                                selectedChallengeTitle = "Martes de boceto"
                                selectedChallengeTheme = "Realiza trazos rápidos de manos haciendo gestos o sosteniendo objetos sin enfocarte en detalles."
                                showInstructionsDialog = true
                            }
                        )
                    }

                    // 2. GALERÍA DE PARTICIPANTES Y VOTACIÓN
                    item {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            Text("Participantes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF222222))
                            Text("Selecciona el arte para votar por tus favoritos", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.horizontalScroll(scrollState),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Acuarela", "Anime", "Gore", "Realismo").forEachIndexed { index, cat ->
                                FilterChip(
                                    selected = index == 0,
                                    onClick = { },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = brandBlue, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    // Renderizado dinámico de la cuadrícula adaptada según tu `image_9e7c5a.png`
                    item {
                        val artworks = state.participantArtworks
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            artworks.chunked(2).forEach { rowItems ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    rowItems.forEach { artwork ->
                                        Box(modifier = Modifier.weight(1f)) {
                                            VotingCard(
                                                username = artwork.username,
                                                title = artwork.title,
                                                likes = artwork.votes,
                                                brandOrange = brandOrange,
                                                onVoteChanged = { isVoted ->
                                                    viewModel.updateVote(artwork.id, isVoted)
                                                }
                                            )
                                        }
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }

                    // 3. GANADORES E INSIGNIAS
                    item {
                        Text("Ganadores con Insignia (Semana Pasada)", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 14.dp))
                    }

                    item {
                        PodiumWinnerCard(
                            winners = listOf("@OliverArt", "@Cynthia", "@Carlos_art"),
                            votes = listOf("186 v", "143 v", "112 v")
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // DIÁLOGO DE INSTRUCCIONES
    // ==========================================
    if (showInstructionsDialog) {
        AlertDialog(
            onDismissRequest = { showInstructionsDialog = false },
            title = { Text(selectedChallengeTitle, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Guía e Instrucciones de la Dinámica:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(selectedChallengeTheme, fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                            Text("Imagen Guía de Referencia", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInstructionsDialog = false
                        showParticipateDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandOrange)
                ) {
                    Text("¡Participar ahora!", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { showInstructionsDialog = false }) { Text("Cerrar") } }
        )
    }

    // ==========================================
    // DIÁLOGO PARA PARTICIPAR
    // ==========================================
    if (showParticipateDialog) {
        AlertDialog(
            onDismissRequest = { showParticipateDialog = false },
            title = { Text("Enviar mi entrega a $selectedChallengeTitle", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = artworkName, onValueChange = { artworkName = it }, label = { Text("Nombre de tu obra") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = artworkCategory, onValueChange = { artworkCategory = it }, label = { Text("Categoría (Ej: Anime, Acuarela)") }, modifier = Modifier.fillMaxWidth())

                    Box(
                        modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF0F2F5)).clickable { sampleGalleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (userImageUri != null) {
                            Text("✓ Lienzo cargado correctamente", color = brandBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Gray)
                                Text("Presiona para subir tu dibujo", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitArtwork(selectedChallengeTitle, artworkName, artworkCategory, userImageUri)
                        showParticipateDialog = false
                        artworkName = ""
                        userImageUri = null
                    },
                    enabled = artworkName.isNotBlank() && userImageUri != null,
                    colors = ButtonDefaults.buttonColors(containerColor = brandOrange)
                ) { Text("Enviar Entrega", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showParticipateDialog = false }) { Text("Cancelar") } }
        )
    }

    // ==========================================
    // DIÁLOGO DE CREAR DINÁMICA
    // ==========================================
    if (showCreateChallengeDialog) {
        AlertDialog(
            onDismissRequest = { showCreateChallengeDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.size(32.dp).background(brandBlue.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Brush, contentDescription = null, tint = brandBlue, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Diseña tu propia dinámica", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color(0xFF222222))
                        Text("Crea un reto personalizado para la comunidad", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = newChallengeTitle, onValueChange = { newChallengeTitle = it }, label = { Text("Título de la dinámica") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newChallengeDesc, onValueChange = { newChallengeDesc = it }, label = { Text("Instrucciones de lo que deben crear") }, modifier = Modifier.fillMaxWidth(), maxLines = 2)

                    Card(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
                    ) {
                        Text(selectedDateText, modifier = Modifier.padding(14.dp), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    Card(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2F5))
                    ) {
                        Text(selectedTimeText, modifier = Modifier.padding(14.dp), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }

                    OutlinedTextField(value = newChallengeVoteDays, onValueChange = { newChallengeVoteDays = it }, label = { Text("Días disponibles para votar") }, modifier = Modifier.fillMaxWidth())

                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF0F2F5)).clickable { referenceGalleryLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (referenceImageUri != null) {
                            Text("✓ Muestra de referencia añadida", color = brandBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        } else {
                            Text("Subir imagen guía para los participantes", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createNewChallenge(newChallengeTitle, newChallengeDesc, selectedDateText, selectedTimeText, newChallengeVoteDays, referenceImageUri)
                        showCreateChallengeDialog = false
                        newChallengeTitle = ""
                        newChallengeDesc = ""
                        referenceImageUri = null
                    },
                    enabled = newChallengeTitle.isNotBlank() && newChallengeDesc.isNotBlank() && referenceImageUri != null,
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                ) { Text("Publicar Dinámica", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showCreateChallengeDialog = false }) { Text("Cerrar") } }
        )
    }

    // --- SUB-SELECTORES OFICIALES MATERIAL 3 ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance().apply { timeInMillis = millis }
                        selectedDateText = "Inicia el: ${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTimeText = "Hora: ${timePickerState.hour}:${String.format("%02d", timePickerState.minute)}"
                    showTimePicker = false
                }) { Text("Confirmar") }
            },
            title = { Text("Elige la hora") },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

@Composable
fun InteractiveChallengeCard(
    title: String,
    theme: String,
    details: String,
    statusText: String,
    statusColor: Color,
    icon: ImageVector,
    brandBlue: Color,
    brandOrange: Color,
    onParticipateClick: () -> Unit,
    onInstructionsClick: () -> Unit
) {
    var isSubscribed by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = brandBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF333333))
                }

                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(statusColor.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Tema: \"$theme\"", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF444444))
                    Text(text = details, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 2.dp))
                }

                IconButton(onClick = onInstructionsClick) {
                    Icon(Icons.Default.Info, contentDescription = "Instrucciones", tint = brandBlue)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { isSubscribed = !isSubscribed }) {
                    Text(
                        text = if (isSubscribed) "Notificación Activa" else "Activar notificación",
                        color = if (isSubscribed) brandOrange else Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onParticipateClick,
                    colors = ButtonDefaults.buttonColors(containerColor = brandOrange),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Participar", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VotingCard(
    username: String,
    title: String,
    likes: Int,
    brandOrange: Color,
    onVoteChanged: (Boolean) -> Unit
) {
    var hasVoted by remember { mutableStateOf(false) }
    var currentLikes by remember { mutableStateOf(likes) }

    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().background(Color(0xFFEAEAEA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Brush, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(32.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(username, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF333333))
                    Text(title, fontSize = 10.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$currentLikes votos", fontSize = 10.sp, color = brandOrange)
                    }
                }

                IconButton(
                    onClick = {
                        hasVoted = !hasVoted
                        if (hasVoted) currentLikes++ else currentLikes--
                        onVoteChanged(hasVoted)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (hasVoted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Votar",
                        tint = if (hasVoted) Color.Red else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PodiumWinnerCard(
    winners: List<String>,
    votes: List<String>
) {
    val medals = listOf(Color(0xFFFFD700), Color(0xFFC0C0C0), Color(0xFFCD7F32))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            winners.forEachIndexed { index, name ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(modifier = Modifier.size(46.dp).background(Color(0xFFE0E0E0), CircleShape))
                        Box(
                            modifier = Modifier.size(18.dp).background(medals[index], CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF222222))
                    Text(votes[index], fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}