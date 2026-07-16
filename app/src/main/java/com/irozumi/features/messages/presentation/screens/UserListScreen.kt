package com.irozumi.features.messages.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.messages.presentation.viewmodel.MessagesViewModel

@Composable
fun UserListScreen(
    viewModel: MessagesViewModel,
    onUserSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.uiState
    val textDark = Color(0xFF2B2D42)
    val backgroundColor = Color(0xFFF4F6F9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        if (state is MessagesUiState.Success) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(state.users) { user ->
                    val userId = user.id
                    val userName = user.name
                    val userRole = user.role
                    val isOnline = user.isOnline

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectUser(userId)
                                onUserSelected(userId)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = CircleShape,
                                color = Color(0xFF2F80ED).copy(alpha = 0.2f)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = userName.take(1),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2F80ED),
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            if (isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color(0xFF2ECC71), CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textDark
                            )
                            if (userRole.isNotBlank()) {
                                Text(
                                    text = userRole,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFE0E0E0),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}