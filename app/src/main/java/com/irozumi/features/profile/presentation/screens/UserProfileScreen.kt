package com.irozumi.features.profile.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.irozumi.R
import com.irozumi.features.profile.presentation.viewmodel.ProfileViewModel
import com.irozumi.core.security.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: String = TokenManager.currentUserId,
    isMyProfile: Boolean,
    onNavigateToChat: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val state = profileViewModel.uiState
    val context = LocalContext.current
    val brandBlue = Color(0xFF2F80ED)
    val textDark = Color(0xFF2B2D42)

    // Lanzadores de Galería para Fotos
    val profilePicLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            profileViewModel.updateProfilePicture(uri)
        }
    val coverPicLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            profileViewModel.updateCoverPicture(uri)
        }

    // Carga inicial
    LaunchedEffect(userId) {
        profileViewModel.setProfileMode(isMyProfile)
        profileViewModel.loadProfile(userId)
    }

    // Diálogo para Editar Perfil y Redes Sociales
    if (state.isEditing) {
        var tempName by remember { mutableStateOf(state.name) }
        var tempBio by remember { mutableStateOf(state.bio) }
        var tempInsta by remember { mutableStateOf(state.instagram) }
        var tempX by remember { mutableStateOf(state.twitter) }

        AlertDialog(
            onDismissRequest = { /* NO se cierra al tocar fuera */ },
            title = { Text("Editar Información", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Nombre") })
                    OutlinedTextField(
                        value = tempBio,
                        onValueChange = { tempBio = it },
                        label = { Text("Biografía") })
                    OutlinedTextField(
                        value = tempInsta,
                        onValueChange = { tempInsta = it },
                        label = { Text("Instagram (usuario)") })
                    OutlinedTextField(
                        value = tempX,
                        onValueChange = { tempX = it },
                        label = { Text("X / Twitter (usuario)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    profileViewModel.updateProfileData(
                        tempName,
                        tempBio,
                        tempInsta,
                        tempX
                    )
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { profileViewModel.toggleEditMode() }) { Text("Cancelar") }
            }
        )
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = brandBlue)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F9))) {
            // --- 1. PORTADA Y FOTO PERFIL REACCIONABLES ---
            item {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    // Portada
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(brandBlue.copy(alpha = 0.15f))
                            .clickable(enabled = state.isMyProfile) { coverPicLauncher.launch("image/*") }
                    ) {
                        when {
                            state.coverPictureUri != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(state.coverPictureUri),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            state.coverPictureUrl.isNotEmpty() -> {
                                AsyncImage(
                                    model = state.coverPictureUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            else -> {
                                Icon(
                                    Icons.Default.PhotoCamera,
                                    contentDescription = null,
                                    tint = brandBlue,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }

                    // Avatar
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(90.dp)
                            .background(Color.White, CircleShape)
                            .border(2.dp, brandBlue, CircleShape)
                            .clickable(enabled = state.isMyProfile) { profilePicLauncher.launch("image/*") }
                    ) {
                        when {
                            state.profilePictureUri != null -> {
                                Image(
                                    painter = rememberAsyncImagePainter(state.profilePictureUri),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            }

                            state.profilePictureUrl.isNotEmpty() -> {
                                AsyncImage(
                                    model = state.profilePictureUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            }

                            else -> {
                                Image(
                                    painter = painterResource(id = R.drawable.mi_logo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            }
                        }
                    }
                }

                // Textos informativos
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.name,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Text(text = state.username, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = state.bio,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    // --- MÉTODOS DE CONTACTO / REDES SOCIALES ---
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.instagram.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://instagram.com/${state.instagram}")
                                    )
                                    context.startActivity(intent)
                                }) {
                                Icon(
                                    Icons.Default.Link,
                                    contentDescription = null,
                                    tint = brandBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    " ${state.instagram}",
                                    fontSize = 12.sp,
                                    color = brandBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (state.twitter.isNotEmpty()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://x.com/${state.twitter}")
                                    )
                                    context.startActivity(intent)
                                }) {
                                Icon(
                                    Icons.Default.Link,
                                    contentDescription = null,
                                    tint = brandBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    " ${state.twitter}",
                                    fontSize = 12.sp,
                                    color = brandBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // --- 2. ACCIONES COMPLEMENTARIAS DINÁMICAS ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.isMyProfile) {
                        Button(
                            onClick = onNavigateToCatalog,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            border = BorderStroke(1.5.dp, Color.LightGray),
                            modifier = Modifier.weight(1.5f).height(44.dp)
                        ) {
                            Icon(
                                Icons.Default.Storefront,
                                contentDescription = null,
                                tint = textDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Subir a tienda", color = textDark, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { profileViewModel.toggleEditMode() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            border = BorderStroke(1.5.dp, textDark),
                            modifier = Modifier.weight(1.2f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = brandBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Editar perfil", color = textDark, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Botón dinámico Seguir / Siguiendo
                        Button(
                            onClick = { profileViewModel.toggleFollow(userId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (state.isFollowing) Color.Gray else brandBlue),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(
                                if (state.isFollowing) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (state.isFollowing) "Siguiendo" else "Seguir",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Botón para iniciar Chat directo
                        Button(
                            onClick = onNavigateToChat,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = textDark),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mensaje", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // --- 3. SECCIÓN LOGROS ---
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Logros de Dinámicas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf(
                                Pair("🛡️", false),
                                Pair("🏆", true),
                                Pair("🛡️", false),
                                Pair("🏅", false)
                            ).forEach { (emoji, isGolden) ->
                                Box(
                                    modifier = Modifier.size(42.dp).background(
                                        color = if (isGolden) Color(0xFFFFEAD2) else Color(
                                            0xFFE3F2FD
                                        ),
                                        shape = CircleShape
                                    ).border(
                                        width = 1.5.dp,
                                        color = if (isGolden) Color(0xFFF2994A) else brandBlue,
                                        shape = CircleShape
                                    ), contentAlignment = Alignment.Center
                                ) {
                                    Text(text = emoji, fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
            }

            // --- 4. TÍTULO SECCIÓN DE CONTENIDO ---
            item {
                Text(
                    text = "Publicaciones recientes",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = textDark,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            // --- 5. LISTA DE PUBLICACIONES TOTALMENTE INTERACTIVAS ---
            items(state.posts) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(CircleShape)
                                    .background(Color.LightGray)
                                    .border(1.dp, brandBlue, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.mi_logo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = state.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textDark
                                )
                                Text(
                                    text = "${state.username} • ${post.category}",
                                    fontSize = 11.sp,
                                    color = brandBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.1f)
                                .background(Color(0xFFE0E0E0))
                                .clickable { profileViewModel.onImageClick(post.imageUrl) }
                        ) {
                            if (post.imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = post.imageUrl,
                                    contentDescription = post.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.mi_logo),
                                    contentDescription = post.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        // --- ACCIONES SOCIALES (LIKE, SHARE, COMENTARIOS) ---
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { profileViewModel.toggleLike(post.id) }) {
                                Icon(
                                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (post.isLiked) Color.Red else textDark
                                )
                            }
                            Text(
                                text = post.likesCount.toString(),
                                fontSize = 13.sp,
                                color = textDark,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            IconButton(onClick = {
                                profileViewModel.onActivePostForComments(post)
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Comment,
                                    contentDescription = "Comment",
                                    tint = textDark
                                )
                            }
                            Text(
                                text = post.commentsCount.toString(),
                                fontSize = 13.sp,
                                color = textDark,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "¡Mira la obra '${post.title}' de ${state.username} en Irozumi!"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Compartir vía"
                                    )
                                )
                            }) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = brandBlue
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = post.title,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = textDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = post.description,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        // Panel de comentarios
        state.activePostForComments?.let { post ->
            var commentText by remember { mutableStateOf("") }

            ModalBottomSheet(
                onDismissRequest = { profileViewModel.onActivePostForComments(null) }
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        "Comentarios",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    state.commentError?.let { error ->
                        Text(
                            error,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (state.isCommentsLoading) {
                        LinearProgressIndicator(
                            color = brandBlue,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false).heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.comments) { comment ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier.size(36.dp)
                                        .background(brandBlue.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        comment.authorName.take(1).uppercase(),
                                        color = brandBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        comment.authorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = textDark
                                    )
                                    Text(
                                        comment.text,
                                        fontSize = 14.sp,
                                        color = Color.Black,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = { Text("Escribe un comentario...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (commentText.isNotBlank()) {
                                profileViewModel.postComment(post.id, commentText)
                                commentText = ""
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = brandBlue)
                        }
                    }
                }
            }
        }

        // Imagen a pantalla completa
        state.fullScreenImage?.let { imageUrl ->
            Dialog(
                onDismissRequest = { profileViewModel.onImageClick(null) },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .clickable { profileViewModel.onImageClick(null) },
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
    }
}
