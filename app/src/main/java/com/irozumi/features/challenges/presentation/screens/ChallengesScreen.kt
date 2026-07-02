package com.irozumi.features.challenges.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.irozumi.features.challenges.presentation.viewmodel.ChallengesViewModel
import com.irozumi.features.challenges.domain.model.ParticipanteVotacion

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    viewModel: ChallengesViewModel,
    navController: NavHostController
) {
    val brandBlue = Color(0xFF2F80ED)
    val brandOrange = Color(0xFFF2994A)
    val textDark = Color(0xFF3D405B)
    val backgroundColor = Color(0xFFF4F6F9)

    var verParticipantes by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf("Acuarela") }

    var mostrarFormularioDinamica by remember { mutableStateOf(false) }
    var mostrarSelectorMediaParticipar by remember { mutableStateOf(false) }
    var mensajeConfirmacion by remember { mutableStateOf("") }

    var nombreDinamica by remember { mutableStateOf("") }
    var fechasParticipar by remember { mutableStateOf("") }
    var fechasVotar by remember { mutableStateOf("") }

    // Guardar URI de la imagen seleccionada u obtenida de forma real
    var imagenReferenciaUri by remember { mutableStateOf<Uri?>(null) }
    var imagenParticipacionUri by remember { mutableStateOf<Uri?>(null) }

    // ================= LAUNCHERS CON FUNCIONALIDAD REAL (SISTEMA OPERATIVO) =================

    // Lanzador para abrir la Galería
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            if (mostrarFormularioDinamica) {
                imagenReferenciaUri = uri
                mensajeConfirmacion = "¡Imagen de referencia cargada desde Galería!"
            } else {
                imagenParticipacionUri = uri
                mensajeConfirmacion = "¡Tu ilustración fue seleccionada con éxito de la Galería!"
            }
        }
    }

    // Lanzador para abrir la Cámara (Captura Foto Completa/Miniatura simulada)
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            mensajeConfirmacion = if (mostrarFormularioDinamica) {
                "¡Foto tomada con éxito para la referencia del mentor!"
            } else {
                "¡Captura realizada! Tu obra ha sido digitalizada correctamente."
            }
        }
    }

    val listaParticipantes = remember {
        mutableStateListOf(
            ParticipanteVotacion(1, "Carlos Art", "Acuarela", votosIniciales = 42),
            ParticipanteVotacion(2, "Sofia V.", "Acuarela", votosIniciales = 39),
            ParticipanteVotacion(3, "Elena Rostros", "Acuarela", votosIniciales = 55),
            ParticipanteVotacion(4, "GokuFan99", "Anime", votosIniciales = 120),
            ParticipanteVotacion(5, "IrozumiUser", "Gore", votosIniciales = 14)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (verParticipantes) "Votar Participantes" else "Retos Semanales",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = textDark
                    )
                },
                navigationIcon = {
                    if (verParticipantes) {
                        IconButton(onClick = { verParticipantes = false }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = textDark
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFormularioDinamica = true }) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Diseñar dinámica",
                            tint = brandBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    IconButton(onClick = { verParticipantes = !verParticipantes }) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Ver participantes",
                            tint = if (verParticipantes) brandBlue else Color.Gray,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // ================= FORMULARIO DIÁLOGO: DISEÑAR DINÁMICA (+) =================
            if (mostrarFormularioDinamica) {
                AlertDialog(
                    onDismissRequest = { },
                    properties = DialogProperties(
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    ),
                    title = { Text("Nueva Dinámica Semanal", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = nombreDinamica,
                                onValueChange = { nombreDinamica = it },
                                label = { Text("Nombre de la dinámica") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = fechasParticipar,
                                onValueChange = { fechasParticipar = it },
                                label = { Text("Fechas de participar") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = fechasVotar,
                                onValueChange = { fechasVotar = it },
                                label = { Text("Fechas para votar") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (imagenReferenciaUri != null) "✓ Referencia lista" else "Subir imagen de referencia del mentor:",
                                fontSize = 12.sp,
                                color = if (imagenReferenciaUri != null) Color(0xFF27AE60) else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { launcherCamara.launch(null) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue.copy(alpha = 0.1f), contentColor = brandBlue),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cámara", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { launcherGaleria.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue.copy(alpha = 0.1f), contentColor = brandBlue),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Galería", fontSize = 12.sp)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (nombreDinamica.isNotEmpty()) {
                                    mostrarFormularioDinamica = false
                                    mensajeConfirmacion = "¡Dinámica '${nombreDinamica}' creada y publicada exitosamente!"
                                    nombreDinamica = ""; fechasParticipar = ""; fechasVotar = ""; imagenReferenciaUri = null
                                } else {
                                    mensajeConfirmacion = "Por favor ingresa al menos el nombre de la dinámica."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = brandOrange)
                        ) {
                            Text("Publicar Reto")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                mostrarFormularioDinamica = false
                                nombreDinamica = ""; fechasParticipar = ""; fechasVotar = ""; imagenReferenciaUri = null
                            }
                        ) {
                            Text("Cancelar", color = Color.Red)
                        }
                    }
                )
            }

            // ================= BOTTOM SHEET: SUBIR IMAGEN AL PARTICIPAR =================
            if (mostrarSelectorMediaParticipar) {
                ModalBottomSheet(
                    onDismissRequest = { mostrarSelectorMediaParticipar = false },
                    sheetState = rememberModalBottomSheetState(confirmValueChange = { false })
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Elige tu ilustración para la dinámica", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                FilledIconButton(
                                    onClick = {
                                        mostrarSelectorMediaParticipar = false
                                        launcherCamara.launch(null)
                                    },
                                    modifier = Modifier.size(60.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = brandBlue)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = "Cámara", modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Cámara", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                FilledIconButton(
                                    onClick = {
                                        mostrarSelectorMediaParticipar = false
                                        launcherGaleria.launch("image/*")
                                    },
                                    modifier = Modifier.size(60.dp),
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = brandOrange)
                                ) {
                                    Icon(Icons.Default.Collections, contentDescription = "Galería", modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Galería", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { mostrarSelectorMediaParticipar = false },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Alerta de confirmación de procesos exitosos (No se cierra tocando fuera)
            if (mensajeConfirmacion.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = { },
                    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                    confirmButton = {
                        Button(onClick = { mensajeConfirmacion = "" }) { Text("Entendido") }
                    },
                    title = { Text("Estado del Proceso", fontWeight = FontWeight.Bold) },
                    text = { Text(mensajeConfirmacion) }
                )
            }

            if (!verParticipantes) {
                // ================= VISTA 1: RETOS SEMANALES =================
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // RETO TERMINADO (VOTACIONES ABIERTAS)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(Color.Red.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.TimerOff, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Jueves de reto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)
                                    }
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("Tiempo Agotado", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                                        border = null,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Tema: \"Luz y sombra\"", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textDark)
                                Text(text = "Indicaciones: Aplica contrastes extremos usando un único punto de iluminación cenital.", fontSize = 13.sp, color = textDark.copy(alpha = 0.7f))

                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.DarkGray.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                                        Text("Esquema_Luz_Cenital.png", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = backgroundColor)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("¡Resultados en 3 días!", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = brandOrange)
                                        Text("Tiempo de evaluar participantes.", fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Button(
                                        onClick = { mensajeConfirmacion = "Notificaciones activadas. Te avisaremos cuando los mentores declaren los ganadores en 3 días." },
                                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Avisarme", fontSize = 12.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { verParticipantes = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = brandOrange),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Ir a Votar Favoritos", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // RETO ABIERTO (PARTICIPAR)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(brandBlue.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = brandBlue, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Martes de boceto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)
                                    }
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("Abierto", color = Color(0xFF27AE60), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF27AE60).copy(alpha = 0.1f)),
                                        border = null,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Tema: \"Manos en movimiento\"", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textDark)
                                Text(text = "Indicaciones: Secuencia de tres pasos realizando una acción cotidiana enfocada en anatomía dinámica.", fontSize = 13.sp, color = textDark.copy(alpha = 0.7f))

                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Inicia en 2 días", fontSize = 12.sp, color = Color.Gray)

                                    Button(
                                        onClick = { mostrarSelectorMediaParticipar = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = brandOrange),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Participar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // ================= VISTA 2: SECCIÓN DE VOTACIONES =================
                Column(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = brandBlue.copy(alpha = 0.08f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = brandBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "¡Apoya con tu voto! Los primeros 3 lugares ganarán una insignia especial.",
                                fontSize = 13.sp,
                                color = brandBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Acuarela", "Anime", "Gore").forEach { cat ->
                            val esSeleccionado = categoriaSeleccionada == cat
                            FilterChip(
                                selected = esSeleccionado,
                                onClick = { categoriaSeleccionada = cat },
                                label = { Text(cat) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = brandBlue,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }

                    val itemsFiltrados = listaParticipantes.filter { it.categoria == categoriaSeleccionada }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(itemsFiltrados, key = { it.id }) { participante ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(130.dp)
                                            .background(Color.LightGray.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Brush,
                                            contentDescription = null,
                                            tint = Color.Gray.copy(alpha = 0.4f),
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = participante.nombre,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = textDark
                                            )
                                            Text(
                                                text = "${participante.votosIniciales} votos",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                val index = listaParticipantes.indexOf(participante)
                                                if (index != -1) {
                                                    // Actualiza de forma reactiva mutando el estado interno
                                                    listaParticipantes[index] = participante.copy(
                                                        votosIniciales = participante.votosIniciales + 1
                                                    )
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = "Votar",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}