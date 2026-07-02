package com.irozumi.features.auth.presentation.register.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.irozumi.features.auth.presentation.register.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }
    var expandedDropdown by remember { mutableStateOf(false) }
    val levels = listOf("Principiante", "Intermedio", "Profesional")

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) onNavigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFAF6))
            .padding(24.dp)
            // Añadido por si el teclado o pantallas chicas requieren scroll
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Crea tu cuenta", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = "Únete a la comunidad de arte", fontSize = 13.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // --- CAMPO: NOMBRE ARTÍSTICO ---
        OutlinedTextField(
            value = state.username,
            onValueChange = { viewModel.onUsernameChanged(it) },
            label = { Text("Nombre artístico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO: CORREO ELECTRÓNICO ---
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEmailChanged(it) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO: CONTRASEÑA ---
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onPasswordChanged(it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color.Gray
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- NUEVO CAMPO: DESPLEGABLE DE NIVEL ARTÍSTICO ---
        ExposedDropdownMenuBox(
            expanded = expandedDropdown,
            onExpandedChange = { expandedDropdown = !expandedDropdown },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = state.artisticLevel,
                onValueChange = {},
                readOnly = true, // Evita que abran el teclado al tocarlo
                label = { Text("Nivel artístico") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                shape = RoundedCornerShape(12.dp),
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false },
                modifier = Modifier.background(Color.White)
            ) {
                levels.forEach { level ->
                    DropdownMenuItem(
                        text = { Text(text = level) },
                        onClick = {
                            viewModel.onArtisticLevelChanged(level)
                            expandedDropdown = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- NUEVA CASILLA: TÉRMINOS Y CONDICIONES ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.acceptedTerms,
                onCheckedChange = { viewModel.onTermsAcceptedChanged(it) },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2F80ED))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Acepto los términos y condiciones de uso.",
                fontSize = 13.sp,
                color = Color.DarkGray
            )
        }

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTÓN DE REGISTRO ---
        if (state.isLoading) {
            CircularProgressIndicator(color = Color(0xFF2F80ED))
        } else {
            Button(
                onClick = {
                    viewModel.onRegisterSubmitted()
                    onNavigateToHome()
                },
                // El botón se deshabilita si no se han aceptado los términos
                enabled = state.acceptedTerms,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F80ED),
                    disabledContainerColor = Color(0xFF2F80ED).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes una cuenta? Inicia sesión", color = Color(0xFF2F80ED))
            }
        }
    }
}