package com.irozumi.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.home.domain.model.ArtworkPost

@Composable
fun ArtworkCard(
    post: ArtworkPost,             // Corregido: Tipo explícito correcto sin asignaciones raras
    brandBlue: Color,
    textDark: Color,
    onLikeToggle: () -> Unit,
    onCommentsClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,     // Parámetro para eliminar funcional
    onEditClick: (String) -> Unit  // Parámetro para editar funcional
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(44.dp).background(brandBlue.copy(alpha = 0.15f), CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.author, fontWeight = FontWeight.Bold, color = textDark, fontSize = 15.sp)
                    Text(text = post.category, color = brandBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                // Mostrar el menú desplegable SOLO si la publicación pertenece al usuario actual
                if (post.author.contains("You", ignoreCase = true) || post.author.contains("Tú", ignoreCase = true)) {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreHoriz, contentDescription = "More options", tint = Color.Gray)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Editar Publicación") },
                                onClick = {
                                    showMenu = false
                                    onEditClick(post.description)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = post.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = textDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.description, fontSize = 13.sp, color = Color.DarkGray, maxLines = 3, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(12.dp))

            // Contenedor de la imagen alternativo (Evita usar Coil si no está instalado)
            val hasImage = !post.imageUrl.isNullOrBlank()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (hasImage) brandBlue.copy(alpha = 0.1f) else Color(0xFFEFEFEF)),
                contentAlignment = Alignment.Center
            ) {
                if (hasImage) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(48.dp), tint = brandBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Imagen cargada exitosamente", fontSize = 12.sp, color = brandBlue, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLikeToggle() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = if (post.likesCount > 0) "❤️" else "🖤", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${post.likesCount} likes", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCommentsClick() }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💬 ${post.comments} comentarios", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }

                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}