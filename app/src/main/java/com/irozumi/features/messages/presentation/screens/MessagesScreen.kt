package com.irozumi.features.messages.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.messages.presentation.viewmodel.MessagesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("SpellCheckingInspection")
@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val brandBlue = Color(0xFF2F80ED)
    val textDark = Color(0xFF2B2D42)
    val backgroundColor = Color(0xFFF4F6F9)
    val chatBubbleMine = Color(0xFFD2E4FF)
    val chatBubbleOther = Color.White

    var messageText by remember { mutableStateOf("") }
    val state = viewModel.uiState

    val currentChatPartnerName = remember(state) {
        if (state is MessagesUiState.Success) {
            val user = state.users.find { it.id == state.selectedUserId }
            (user?.name) ?: "Chat"

        } else {
            "Chat"
        }
    }

    // 🛠️ CLAVE 1: Scaffold maneja el redimensionamiento de ventanas de forma limpia y mantiene fija la TopAppBar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentChatPartnerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = textDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        // 🛠️ CLAVE 2: Colocamos la barra de entrada de texto aquí. Al usar Scaffold,
        // automáticamente se calcula la posición del teclado sin mover la TopAppBar.
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .windowInsetsPadding(WindowInsets.ime), // 👈 Reemplaza imePadding() por este insets nativo
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Attach",
                            tint = brandBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Escribe un mensaje...", color = Color.Gray, fontSize = 14.sp) },
                        modifier = Modifier.weight(1f),
                        maxLines = 4,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark,
                            focusedContainerColor = Color(0xFFF4F6F9),
                            unfocusedContainerColor = Color(0xFFF4F6F9),
                            focusedBorderColor = Color(0xFFE0E0E0),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank(),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (messageText.isNotBlank()) brandBlue else Color.LightGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        // --- CUERPO PRINCIPAL DEL CHAT ---
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding), // 👈 Aplica los paddings del Scaffold automáticamente
            contentAlignment = Alignment.BottomCenter
        ) {
            if (state is MessagesUiState.Success) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true
                ) {
                    items(state.currentChatMessages.reversed()) { message ->
                        val text = message.text
                        val isMine = message.isMine

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Card(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMine) 16.dp else 2.dp,
                                    bottomEnd = if (isMine) 2.dp else 16.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isMine) chatBubbleMine else chatBubbleOther
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(
                                        text = text,
                                        fontSize = 15.sp,
                                        color = textDark,
                                        lineHeight = 20.sp
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