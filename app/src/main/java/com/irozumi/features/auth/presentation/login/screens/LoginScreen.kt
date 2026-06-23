package com.irozumi.features.auth.presentation.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.auth.presentation.login.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel, // Agregado
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    // Estado local indispensable para alternar el ojito
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        // Cabecera decorativa
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Brush.verticalGradient(colors = listOf(Color(0xFF2F80ED), Color(0x002F80ED))))
                .padding(16.dp)
        ) {
            Text("ArteVivo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp)
                .offset(y = (-30).dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Iniciar sesión", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Accede a tu cuenta de forma rápida y segura", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Contraseña con el Ojito Reactivo
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            // Usamos texto/emoji limpio para evitar problemas de assets/vectoriales rotos
                            Text(text = if (passwordVisible) "👁️" else "🙈", fontSize = 18.sp)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (state.isLoading) {
                    CircularProgressIndicator(color = Color(0xFF2F80ED))
                } else {
                    Button(
                        onClick = { viewModel.onLoginSubmitted() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F80ED)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onNavigateToRegister) {
                        Text("¿No tienes una cuenta? Regístrate", color = Color(0xFF2F80ED))
                    }
                }
            }
        }
    }
}