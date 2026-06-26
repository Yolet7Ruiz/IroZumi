package com.irozumi.features.notifications.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationCard(
    username: String,       // Ej: "@BandaRomantica24"
    actionText: String,     // Ej: "le gustó tu arte"
    timeAndStatus: String,  // Ej: "Hace 23h • Nuevo"
    onClick: () -> Unit,
    onMuteClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Redirige al presionar la barrita
        shape = RoundedCornerShape(28.dp), // Bordes bien redondeados como en la referencia
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Foto de perfil y mini-icono de acción superpuesto abajo a la derecha
            Box(
                modifier = Modifier.size(54.dp)
            ) {
                // Perfil circular (puedes cambiar este Box por AsyncImage de Coil después)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.TopStart)
                        .background(Color(0xFFE0E0E0), CircleShape)
                )

                // Pequeño círculo naranja del corazón
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(Color(0xFFFF6B00), CircleShape)
                        .align(Alignment.BottomEnd)
                        .background(Color.White, CircleShape) // Simula el borde blanco estético
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

            // Información textual alineada horizontalmente
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF222222)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = actionText,
                        fontSize = 14.sp,
                        color = Color(0xFF555555)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeAndStatus,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Opciones del menú desplegable (Tres puntitos)
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