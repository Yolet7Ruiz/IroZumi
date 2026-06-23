package com.irozumi.features.auth.presentation.welcome.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val isFinalPage: Boolean = false
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage("Muestra tu arte", "Comparte tus pinturas tradicionales con una comunidad que valora el arte real, sin filtros ni algoritmos tóxicos."),
        OnboardingPage("Comunidad sana", "Aquí los principiantes reciben apoyo y motivación. Las críticas constructivas suman, las destructivas no tienen lugar."),
        OnboardingPage("Aprende de los mejores", "Conecta con pintores dispuestos a enseñar sus técnicas y estilos. Clases personalizadas al alcance de tu mano."),
        OnboardingPage("Vive de tu arte", "Vende obras originales, acepta encargos personalizados con significado emocional, y conecta con compradores que valoran lo hecho a mano."),
        OnboardingPage("IroZumi", "Tu pulso, tu estilo, tu espacio.", isFinalPage = true)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val isLastPage = pagerState.currentPage == pages.size - 1

    val backgroundColor = Color(0xFFFEFAF6)
    val brandBlue = Color(0xFF2F80ED) // Azul unificado de la sesión
    val textDark = Color(0xFF3D405B)

    // Animación suave del degradado según la página
    val targetColor = when (pagerState.currentPage) {
        0 -> Color(0xFF2F80ED) // Azul arte
        1 -> Color(0xFF00B4D8) // Cyan comunidad
        2 -> Color(0xFF7209B7) // Morado aprendizaje
        3 -> Color(0xFFF72585) // Rosa/Coral negocio
        else -> brandBlue       // Azul IroZumi definitivo
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 500),
        label = "OrbeColorAnimation"
    )

    val topGradient = Brush.verticalGradient(
        colors = listOf(
            animatedColor.copy(alpha = 0.25f),
            backgroundColor
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Parte superior: Degradado interactivo con el Logo Oficial integrado de forma fija
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(topGradient),
            contentAlignment = Alignment.Center
        ) {
            // Contenedor circular blanco premium para el Logo de la aplicación
            Surface(
                modifier = Modifier.size(130.dp),
                shape = CircleShape,
                color = Color.White,
                tonalElevation = 2.dp,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Colocamos el emoji de la paleta simulando el isotipo oficial
                    Text(
                        text = "🎨",
                        fontSize = 64.sp
                    )
                }
            }
        }

        // Parte inferior contenedora de las tarjetas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.4f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Deslizador de textos
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { position ->
                    val page = pages[position]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = page.title,
                            fontSize = if (page.isFinalPage) 36.sp else 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = textDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = page.description,
                            fontSize = if (page.isFinalPage) 18.sp else 15.sp,
                            fontWeight = if (page.isFinalPage) FontWeight.Medium else FontWeight.Normal,
                            color = if (page.isFinalPage) Color(0xFF6B6B7B) else Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Indicadores de puntitos inferiores
                if (!isLastPage) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pages.size - 1) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp)
                                    .background(
                                        color = if (isSelected) brandBlue else Color.LightGray,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }

                // Sección inferior de botones interactivos
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isLastPage) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    scope.launch { pagerState.scrollToPage(pages.size - 1) }
                                }
                            ) {
                                Text("SALTAR", color = brandBlue, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.width(140.dp)
                            ) {
                                Text("SIGUIENTE", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = onNavigateToLogin,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                            ) {
                                Text(
                                    text = "Comenzar aventura",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            TextButton(onClick = onNavigateToRegister) {
                                Text("¿Eres nuevo? Crea una cuenta aquí", color = textDark, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}