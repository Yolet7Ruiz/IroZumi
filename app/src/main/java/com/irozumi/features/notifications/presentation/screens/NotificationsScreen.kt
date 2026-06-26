package com.irozumi.features.notifications.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.notifications.presentation.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onNavigateToSource: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 💡 Aquí agregas tus elementos usando los parámetros corregidos de tu diseño
            item {
                NotificationCard(
                    username = "@BandaRomantica24",
                    actionText = "le gustó tu arte.",
                    timeAndStatus = "Hace 23h • Nuevo",
                    onClick = {
                        // Te redirige a la sección correspondiente al pulsar la barrita
                        onNavigateToSource("feed")
                    },
                    onMuteClick = { /* Vincula tu función del viewModel aquí */ },
                    onDeleteClick = { /* Vincula tu función del viewModel aquí */ }
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    username: String,
    actionText: String,
    timeAndStatus: String,
    onClick: () -> Unit,
    onMuteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp), // Bordes súper redondeados como en la imagen
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Foto de perfil circular con mini-icono de corazón naranja abajo a la derecha
            Box(
                modifier = Modifier.size(54.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.TopStart)
                        .background(Color(0xFFE0E0E0), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color(0xFFFF6B00), CircleShape)
                        .align(Alignment.BottomEnd)
                        .background(Color.White, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Textos en un solo bloque fluido (Nombre en negrita + texto continuo)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF222222))) {
                            append(username)
                        }
                        append(" ")
                        withStyle(style = SpanStyle(color = Color(0xFF555555))) {
                            append(actionText)
                        }
                    },
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeAndStatus,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Tres puntitos con menú desplegable para Silenciar y Eliminar
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = Color.Gray
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Silenciar notificación") },
                        onClick = {
                            onMuteClick()
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Eliminar", color = Color.Red) },
                        onClick = {
                            onDeleteClick()
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}