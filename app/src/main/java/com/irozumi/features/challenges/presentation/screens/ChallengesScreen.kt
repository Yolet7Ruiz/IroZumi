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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.items

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
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var verParticipantes by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf("Acuarela") }

    var mostrarFormularioDinamica by remember { mutableStateOf(false) }
    var mostrarSelectorMediaParticipar by remember { mutableStateOf(false) }
    var mensajeConfirmacion by remember { mutableStateOf("") }

    var nombreDinamica by remember { mutableStateOf("") }
    var fechasParticipar by remember { mutableStateOf("") }
    var fechasVotar by remember { mutableStateOf("") }

    var imagenReferenciaUri by remember { mutableStateOf<Uri?>(null) }
    var descripcionDinamica by remember { mutableStateOf("") }
    var temaDinamica by remember { mutableStateOf("") }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenReferenciaUri = uri
            mensajeConfirmacion = "¡Imagen cargada desde Galería!"
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            mensajeConfirmacion = "¡Captura realizada con éxito!"
        }
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
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = textDark)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFormularioDinamica = true }) {
                        Icon(Icons.Default.AddCircleOutline, "Crear", tint = brandBlue, modifier = Modifier.size(26.dp))
                    }
                    IconButton(onClick = { verParticipantes = !verParticipantes }) {
                        Icon(Icons.Default.Groups, "Ver", tint = if (verParticipantes) brandBlue else Color.Gray, modifier = Modifier.size(26.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // DIÁLOGO: NUEVA DINÁMICA
            if (mostrarFormularioDinamica) {
                AlertDialog(
                    onDismissRequest = { /* No se cierra al tocar fuera */  },
                    title = { Text("Nueva Dinámica Semanal", fontWeight = FontWeight.Bold) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(value = nombreDinamica, onValueChange = { nombreDinamica = it }, label = { Text("Nombre") })
                            OutlinedTextField(value = descripcionDinamica, onValueChange = { descripcionDinamica = it }, label = { Text("Descripción") })
                            OutlinedTextField(value = temaDinamica, onValueChange = { temaDinamica = it }, label = { Text("Tema") })
                            OutlinedTextField(value = fechasParticipar, onValueChange = { fechasParticipar = it }, label = { Text("Fechas participar") })
                            OutlinedTextField(value = fechasVotar, onValueChange = { fechasVotar = it }, label = { Text("Fechas votar") })

                            // PREVIEW
                            if (imagenReferenciaUri != null) {
                                AsyncImage(
                                    model = imagenReferenciaUri,
                                    contentDescription = "Vista previa",
                                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { launcherCamara.launch(null) }, modifier = Modifier.weight(1f)) { Text("Cámara", fontSize = 11.sp) }
                                Button(onClick = { launcherGaleria.launch("image/*") }, modifier = Modifier.weight(1f)) { Text("Galería", fontSize = 11.sp) }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (nombreDinamica.isBlank()) {
                                mensajeConfirmacion = "El nombre es obligatorio"
                                return@Button
                            }
                            viewModel.createNewChallenge(nombreDinamica, descripcionDinamica, fechasParticipar, fechasVotar, "3", temaDinamica)
                            nombreDinamica = ""; descripcionDinamica = ""; temaDinamica = ""
                            fechasParticipar = ""; fechasVotar = ""; imagenReferenciaUri = null
                            mostrarFormularioDinamica = false
                            mensajeConfirmacion = "¡Reto creado!"
                        }) { Text("Publicar") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            nombreDinamica = ""; descripcionDinamica = ""; temaDinamica = ""
                            fechasParticipar = ""; fechasVotar = ""; imagenReferenciaUri = null
                            mostrarFormularioDinamica = false
                        }) { Text("Cancelar", color = Color.Red) }
                    })

            }


            if (!verParticipantes) {
                val successState = state as? ChallengesUiState.Success
                val challenges = successState?.systemChallenges ?: emptyList()

                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(challenges) { challenge ->
                        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(challenge.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textDark)
                                Text("Tema: \"${challenge.concept}\"", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(challenge.description, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = { verParticipantes = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = brandOrange)) {
                                    Text("Ir a Votar Favoritos")
                                }
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Acuarela", "Anime", "Gore").forEach { cat ->
                            FilterChip(selected = categoriaSeleccionada == cat, onClick = { categoriaSeleccionada = cat }, label = { Text(cat) })
                        }
                    }

                    val successState = state as? ChallengesUiState.Success
                    val itemsFiltrados = successState?.participantArtworks?.filter { it.category == categoriaSeleccionada } ?: emptyList()

                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(itemsFiltrados) { participante ->
                            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(participante.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${participante.votes} votos", fontSize = 12.sp)
                                        }
                                        // AQUÍ ESTABA EL ERROR DE SINTAXIS (CORREGIDO)
                                        IconButton(onClick = { viewModel.updateVote(participante.id) }) {
                                            Icon(Icons.Default.Favorite, "Votar", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            if (mensajeConfirmacion.isNotEmpty()) {
                AlertDialog(onDismissRequest = { mensajeConfirmacion = "" }, confirmButton = { Button(onClick = { mensajeConfirmacion = "" }) { Text("OK") } }, title = { Text("Estado") }, text = { Text(mensajeConfirmacion) })
            }
        }
    }
}
