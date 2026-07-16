package com.irozumi.features.gym.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.irozumi.features.gym.domain.model.GymExercise
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.ByteArrayOutputStream
import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GymScreen(
    viewModel: GymViewModel
) {
    val exercises by viewModel.exercises.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val currentTip by viewModel.currentTip.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    var showAddTipDialog by remember { mutableStateOf(false) }
    var showNewExerciseDialog by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<GymExercise?>(null) }
    var selectedExerciseIdForPhoto by remember { mutableStateOf<String?>(null) }
    var fullScreenImage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var exerciseToDelete by remember { mutableStateOf<String?>(null) }

    val brandBlue = Color(0xFF2F80ED)
    val accentOrange = Color(0xFFF2994A)
    val textDark = Color(0xFF3D405B)
    val backgroundColor = Color(0xFFF4F6F9)
    val context = LocalContext.current

    val drawingPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && selectedExerciseIdForPhoto != null) {
            viewModel.submitPractice(selectedExerciseIdForPhoto!!, uri)
            selectedExerciseIdForPhoto = null
        }
    }

    var showPracticeDialog by remember { mutableStateOf(false) }
    var practiceImageUri by remember { mutableStateOf<Uri?>(null) }
    var practiceNotes by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "Practice_${System.currentTimeMillis()}",
                null
            )
            practiceImageUri = Uri.parse(path)
            showPracticeDialog = true
        }
    }

    val practiceGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            practiceImageUri = uri
            showPracticeDialog = true
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) cameraLauncher.launch(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        // 1. DASHBOARD DE RECOMPENSAS Y RACHAS
        if (!isAdmin) {
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
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = brandBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${userStreak.totalPointsEarned} Pts",
                                        color = brandBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (userStreak.currentStreakDays in 1..2) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFFF3CD))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFF856404),
                                        modifier = Modifier.size(18.dp)
                                    )
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
                            text = when {
                                userStreak.currentStreakDays == 0 -> "🔥 Sube tu primer trazo hoy para empezar tu racha."
                                userStreak.currentStreakDays < 3 -> "🔥 Vas empezando. ¡No te rindas!"
                                userStreak.currentStreakDays < 7 -> "🔥 ¡${userStreak.currentStreakDays} días! Sigue así."
                                else -> "🔥 ¡Eres imparable! ${userStreak.currentStreakDays} días de racha."
                            },
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
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Inspiracion Anti-Bloqueo",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        IconButton(
                            onClick = { viewModel.getRandomTip() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Cambiar tip",
                                tint = Color.LightGray
                            )
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
                        Text(
                            text = "Por: ${currentTip?.author}",
                            color = brandBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showAddTipDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compartir mi Tip Anti-Bloqueo", fontSize = 13.sp)
                    }
                }
            }
        }

        // TÍTULO SECCIÓN EJERCICIOS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = textDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Gimnasio de Trazos Diarios",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = textDark
                    )
                }
                if (viewModel.isAdmin.collectAsState().value) {
                    FilledIconButton(
                        onClick = { showNewExerciseDialog = true },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = brandBlue),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Nuevo ejercicio",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // LISTA DE EJERCICIOS
        items(exercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                brandBlue = brandBlue,
                textDark = textDark,
                isAdmin = isAdmin,
                onUploadPhotoClick = {
                    selectedExerciseIdForPhoto = exercise.id
                    // Mostrar opciones: galería o cámara
                    showPracticeDialog = true
                },
                onEditClick = {
                    editingExercise = exercise
                    showNewExerciseDialog = true
                },
                onDeleteClick = {
                    showDeleteDialog = true
                    exerciseToDelete = exercise.id
                },
                onImageClick = { fullScreenImage = exercise.imageUrl }
            )
        }

        // SECCIÓN COMUNIDAD
        item {
            Text(
                "Consejos de la Comunidad",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textDark,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        items(viewModel.communityTipsList) { tip ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(36.dp)
                            .background(brandBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = brandBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = tip.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = textDark
                        )
                        Text(text = tip.description, fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "Por: ${tip.author} • ${tip.category}",
                            fontSize = 10.sp,
                            color = brandBlue,
                            fontWeight = FontWeight.Bold
                        )
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
        var tipCatExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { /* No se cierra al tocar fuera */ },
            properties = DialogProperties(dismissOnClickOutside = false),
            title = { Text("Nuevo Tip Anti-Bloqueo", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tipTitle,
                        onValueChange = { tipTitle = it },
                        label = { Text("Titulo corto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tipDesc,
                        onValueChange = { tipDesc = it },
                        label = { Text("Describe tu consejo...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = tipCatExpanded,
                        onExpandedChange = { tipCatExpanded = it }) {
                        OutlinedTextField(
                            value = tipCat,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipCatExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = tipCatExpanded,
                            onDismissRequest = { tipCatExpanded = false }) {
                            listOf("Creatividad", "Fisico", "Digital").forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = { tipCat = cat; tipCatExpanded = false })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.publishCommunityTip(
                            tipTitle,
                            tipDesc,
                            tipCat
                        ); showAddTipDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                ) { Text("Publicar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddTipDialog = false
                }) { Text("Cancelar") }
            }
        )
    }
    // DIÁLOGO PARA CREAR NUEVO EJERCICIO (SOLO ADMIN)
    if (showNewExerciseDialog) {
        val isEditing = editingExercise != null
        var exTitle by remember(editingExercise) { mutableStateOf(editingExercise?.title ?: "") }
        var exDesc by remember(editingExercise) {
            mutableStateOf(
                editingExercise?.description ?: ""
            )
        }
        var exCategory by remember(editingExercise) {
            mutableStateOf(
                editingExercise?.category ?: "Líneas"
            )
        }
        var exDifficulty by remember(editingExercise) {
            mutableStateOf(
                editingExercise?.difficulty ?: "Principiante"
            )
        }
        var exDuration by remember(editingExercise) {
            mutableStateOf(
                editingExercise?.durationMinutes?.toString() ?: "15"
            )
        }
        var exPoints by remember(editingExercise) {
            mutableStateOf(
                editingExercise?.pointsReward?.toString() ?: "10"
            )
        }
        var exImageUri by remember(editingExercise) { mutableStateOf<Uri?>(null) }

        val exerciseImageLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri -> exImageUri = uri }

        AlertDialog(
            onDismissRequest = { /* No se cierra al tocar fuera */ },
            properties = DialogProperties(dismissOnClickOutside = false),
            title = {
                Text(
                    if (isEditing) "Editar Ejercicio" else "Nuevo Ejercicio",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()), // 🛠️ RESPONSIVE: Scroll para pantallas chicas
                    verticalArrangement = Arrangement.spacedBy(12.dp) // Espaciado controlado
                ) {
                    OutlinedTextField(
                        value = exTitle,
                        onValueChange = { exTitle = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = exDesc,
                        onValueChange = { exDesc = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // CATEGORÍA DROPDOWN
                    var catExpanded by remember { mutableStateOf(false) }
                    val catOptions = listOf(
                        "Líneas",
                        "Círculos",
                        "Sombras",
                        "Perspectiva",
                        "Anatomía",
                        "Rostros",
                        "Manos"
                    )

                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = it }) {
                        OutlinedTextField(
                            value = exCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }) {
                            catOptions.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = { exCategory = cat; catExpanded = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    var difExpanded by remember { mutableStateOf(false) }
                    val difOptions = listOf("Principiante", "Intermedio", "Avanzado")

                    ExposedDropdownMenuBox(
                        expanded = difExpanded,
                        onExpandedChange = { difExpanded = it }) {
                        OutlinedTextField(
                            value = exDifficulty,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Dificultad") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = difExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = difExpanded,
                            onDismissRequest = { difExpanded = false }) {
                            difOptions.forEach { dif ->
                                DropdownMenuItem(
                                    text = { Text(dif) },
                                    onClick = { exDifficulty = dif; difExpanded = false })
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = exDuration,
                            onValueChange = { newVal ->
                                if (newVal.all { it.isDigit() } || newVal.isEmpty()) exDuration =
                                    newVal
                            },
                            label = { Text("Minutos") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = exPoints,
                            onValueChange = { newVal ->
                                if (newVal.all { it.isDigit() } || newVal.isEmpty()) exPoints =
                                    newVal
                            },
                            label = { Text("Puntos") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Button(
                        onClick = { exerciseImageLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)) // Color morado como en tu captura
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (exImageUri != null) "Imagen Seleccionada" else "Subir Referencia",
                            fontSize = 13.sp
                        )
                    }

                    // 🖼️ PREVIEW AJUSTADA
                    exImageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (exTitle.isNotBlank()) {
                            if (isEditing && editingExercise != null) {
                                viewModel.updateExercise(
                                    exerciseId = editingExercise!!.id,
                                    title = exTitle,
                                    description = exDesc.ifBlank { null },
                                    category = exCategory,
                                    difficulty = exDifficulty,
                                    exDuration.toIntOrNull()?.coerceAtLeast(1) ?: 15,
                                    exPoints.toIntOrNull()?.coerceAtLeast(0) ?: 10,
                                    imageUri = exImageUri
                                )
                            } else {
                                viewModel.createExercise(
                                    exTitle,
                                    exDesc.ifBlank { null },
                                    exCategory,
                                    exDifficulty,
                                    exDuration.toIntOrNull() ?: 15,
                                    exPoints.toIntOrNull() ?: 10,
                                    exImageUri
                                )
                            }
                            showNewExerciseDialog = false
                            editingExercise = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Publicar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNewExerciseDialog = false
                    editingExercise = null
                }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // IMAGEN COMPLETA
    fullScreenImage?.let { imageUrl ->
        Dialog(
            onDismissRequest = { fullScreenImage = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { fullScreenImage = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    // DIÁLOGO CONFIRMAR ELIMINAR
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { /* No se cierra al tocar fuera */ },
            properties = DialogProperties(dismissOnClickOutside = false),
            title = { Text("Eliminar ejercicio") },
            text = { Text("¿Estás seguro? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        exerciseToDelete?.let { viewModel.deleteExercise(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) { Text("Cancelar") }
            }
        )
    }
    // 📸 DIÁLOGO SUBIR PRÁCTICA
    if (showPracticeDialog && selectedExerciseIdForPhoto != null) {
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { /* No se cierra */ },
            properties = DialogProperties(dismissOnClickOutside = false),
            title = { Text("Subir práctica", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (practiceImageUri != null) {
                        AsyncImage(
                            model = practiceImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().height(150.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { practiceGalleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Collections, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Galería")
                        }
                        Button(
                            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PhotoCamera, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cámara")
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (practiceImageUri != null && selectedExerciseIdForPhoto != null) {
                        viewModel.submitPractice(selectedExerciseIdForPhoto!!, practiceImageUri!!)
                        showPracticeDialog = false
                        practiceImageUri = null
                        selectedExerciseIdForPhoto = null
                    }
                }) { Text("Subir") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPracticeDialog = false; practiceImageUri = null
                }) { Text("Cancelar") }
            }
        )
    }
}


@Composable
fun ExerciseCard(
    exercise: GymExercise,
    brandBlue: Color,
    textDark: Color,
    isAdmin: Boolean = false,
    onUploadPhotoClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onImageClick: () -> Unit = {}
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
                    Text(text = exercise.description ?: "", color = Color.Gray, fontSize = 13.sp, lineHeight = 18.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))
                if (exercise.isCompleted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Completado", tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                        Text("¡Verificado!", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    if (isAdmin) {
                        Row {
                            IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Edit, "Editar", tint = brandBlue, modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red, modifier = Modifier.size(20.dp))
                            }
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
            }

            exercise.imageUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de referencia",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f))
                        .clickable { onImageClick() },
                    contentScale = ContentScale.Crop
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
