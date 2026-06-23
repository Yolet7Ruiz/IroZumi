package com.irozumi.features.auth.presentation.welcome.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val dy by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "translateY"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEFAF6))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "Ilustración ArtBound",
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer { translationY = dy },
                contentScale = ContentScale.Fit
            )
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 700)
            ) + fadeIn(animationSpec = tween(durationMillis = 700)),
            modifier = Modifier.weight(1f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "ArtBound",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF3D405B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tu pulso, tu estilo, tu espacio.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6B6B7B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    // CORRECCIÓN AQUÍ: Usamos onNavigateToRegister o onNavigateToLogin
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE07A5F))
                    ) {
                        Text(
                            text = "Comenzar aventura",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Opcional: Añadir un botón pequeño para ir al Login si ya tienen cuenta
                    TextButton(onClick = onNavigateToLogin) {
                        Text("Ya tengo una cuenta", color = Color(0xFF3D405B))
                    }
                }
            }
        }
    }
}