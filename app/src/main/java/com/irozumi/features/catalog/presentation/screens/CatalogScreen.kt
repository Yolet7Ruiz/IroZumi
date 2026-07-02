package com.irozumi.features.catalog.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.irozumi.features.catalog.presentation.viewmodel.CatalogViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    navController: NavHostController
) {
    val brandBlue = Color(0xFF2F80ED)
    val textDark = Color(0xFF3D405B)
    val backgroundColor = Color(0xFFF4F6F9)

    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var mostrarFormularioVenta by remember { mutableStateOf(false) }

    // Campos del formulario de subida
    var tituloObra by remember { mutableStateOf("") }
    var precioObra by remember { mutableStateOf("") }
    var categoriaObra by remember { mutableStateOf("Acrílico") }
    var imagenSeleccionadaUri by remember { mutableStateOf<Uri?>(null) }
    var fotoTmpUri by remember { mutableStateOf<Uri?>(null) }

    fun generarUriTemporal(): Uri {
        val directorio = File(context.cacheDir, "catalog_fotos").apply { mkdirs() }
        val archivo = File.createTempFile("venta_${System.currentTimeMillis()}", ".jpg", directorio)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imagenSeleccionadaUri = uri }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito -> if (exito) imagenSeleccionadaUri = fotoTmpUri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Obras", fontWeight = FontWeight.Black, color = textDark) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = textDark
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFormularioVenta = true }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Vender Obra", tint = brandBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            when (val estado = state) {
                is CatalogUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = brandBlue)
                }
                is CatalogUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // Barra horizontal de categorías (Filtros del catálogo)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val categorias = listOf("Todos", "Acrílico", "Anime", "Realismo", "Acuarela")
                            categorias.forEach { cat ->
                                FilterChip(
                                    selected = estado.selectedCategory == cat,
                                    onClick = { viewModel.cambiarCategoria(cat) },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = textDark,
                                        selectedLabelColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }

                        val productosFiltrados = if (estado.selectedCategory == "Todos") {
                            estado.products
                        } else {
                            estado.products.filter { it.category == estado.selectedCategory }
                        }

                        // Cuadrícula idéntica a tu diseño de referencia
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(productosFiltrados) { obra ->
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(140.dp)
                                                .background(Color(0xFFEFEFEF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Brush,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(40.dp)
                                            )

                                            // Badge de Puntuación (Estrellas)
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopStart)
                                                    .padding(8.dp)
                                                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF2994A), modifier = Modifier.size(12.dp))
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(obra.rating.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textDark)
                                                }
                                            }

                                            // Corazón de Favoritos
                                            IconButton(
                                                onClick = { viewModel.toggleFavorito(obra.id) },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                                    .size(30.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (obra.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = null,
                                                    tint = if (obra.isFavorite) Color.Red else Color.Gray,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        // Detalles inferiores de la tarjeta comercial
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(text = obra.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark, maxLines = 1)
                                            Text(text = obra.artistName, fontSize = 11.sp, color = Color.Gray)

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "$${String.format("%.0f", obra.price)}",
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 16.sp,
                                                    color = textDark
                                                )

                                                // 💬 BOTÓN (+): Abre directamente tu pantalla de mensajes compartida
                                                IconButton(
                                                    onClick = {
                                                        navController.navigate("messages_screen")
                                                    },
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .background(Color.Black, CircleShape)
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = "Contactar creador", tint = Color.White, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is CatalogUiState.Error -> {
                    Text(estado.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
            }

            // Diálogo Modal para Promocionar una Nueva Obra
            if (mostrarFormularioVenta) {
                AlertDialog(
                    onDismissRequest = { mostrarFormularioVenta = false },
                    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
                    title = { Text("Promocionar obra para venta", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = tituloObra, onValueChange = { tituloObra = it }, label = { Text("Título de la pieza") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(
                                value = precioObra,
                                onValueChange = { precioObra = it },
                                label = { Text("Precio estimado ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text("Fotografía de la pieza:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { val uri = generarUriTemporal(); fotoTmpUri = uri; launcherCamara.launch(uri) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue.copy(alpha = 0.1f), contentColor = brandBlue)
                                ) { Text("Cámara", fontSize = 12.sp) }
                                Button(
                                    onClick = { launcherGaleria.launch("image/*") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue.copy(alpha = 0.1f), contentColor = brandBlue)
                                ) { Text("Galería", fontSize = 12.sp) }
                            }
                            if (imagenSeleccionadaUri != null) {
                                Text("✓ Archivo multimedia adjuntado con éxito", color = Color(0xFF27AE60), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val precioParseado = precioObra.toDoubleOrNull() ?: 0.0
                                if (tituloObra.isNotEmpty() && precioParseado > 0.0) {
                                    viewModel.subirObraParaVenta(tituloObra, precioParseado, categoriaObra, imagenSeleccionadaUri)
                                    mostrarFormularioVenta = false
                                    tituloObra = ""; precioObra = ""; imagenSeleccionadaUri = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                        ) { Text("Publicar", color = Color.White) }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarFormularioVenta = false; tituloObra = ""; precioObra = ""; imagenSeleccionadaUri = null }) {
                            Text("Cancelar", color = Color.Red)
                        }
                    }
                )
            }
        }
    }
}