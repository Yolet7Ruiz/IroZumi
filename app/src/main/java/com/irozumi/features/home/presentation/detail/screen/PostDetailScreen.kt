package com.irozumi.features.home.presentation.detail.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// CORREGIDO: Apuntando al paquete correcto de home y su subcarpeta viewmodel
import com.irozumi.features.home.presentation.detail.viewmodel.PostDetailViewModel
import com.irozumi.features.home.presentation.detail.viewmodel.PostDetailState

@Composable
fun PostDetailScreen(
    onBack: () -> Unit,
    viewModel: PostDetailViewModel
) {
    val state: PostDetailState by viewModel.state.collectAsState()
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Barra Superior
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) { Text("←", fontSize = 22.sp) }
            Text("IroZumi", fontWeight = FontWeight.Black)
            IconButton(onClick = {}) { Text("🔖") }
        }

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(state.authorName.ifBlank { "Artista" }, fontWeight = FontWeight.Bold)
                    Button(onClick = { viewModel.toggleFollow() }) {
                        Text(if (state.isFollowing) "Siguiendo" else "Seguir")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(state.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(state.description)
                }
            }

            items(state.comments) { comment ->
                Text(comment.text, modifier = Modifier.padding(16.dp))
            }
        }

        // Input de comentarios
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Comentar...") }
            )
            Button(onClick = {
                viewModel.addComment(commentText)
                commentText = ""
            }) { Text("🚀") }
        }
    }
}