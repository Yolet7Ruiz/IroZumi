package com.irozumi.core.navigation

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.irozumi.R
import com.irozumi.core.security.TokenManager
import com.irozumi.features.auth.presentation.welcome.screens.WelcomeScreen
import com.irozumi.features.auth.presentation.login.screens.LoginScreen
import com.irozumi.features.auth.presentation.register.screens.RegisterScreen
import com.irozumi.features.home.presentation.screens.HomeScreen
import com.irozumi.features.messages.presentation.screens.MessagesScreen
import com.irozumi.features.messages.presentation.screens.UserListScreen
import com.irozumi.features.messages.presentation.viewmodel.MessagesViewModel
import com.irozumi.features.profile.presentation.screens.UserProfileScreen
import com.irozumi.features.catalog.presentation.screens.CatalogScreen
import kotlinx.coroutines.delay

@Composable
fun NavGraph(navController: NavHostController) {
    val messagesViewModel: MessagesViewModel = viewModel()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            val onboardingCompleted = remember {
                context.getSharedPreferences("irozumi_prefs", Context.MODE_PRIVATE)
                    .getBoolean("onboarding_completed", false)
            }

            LaunchedEffect(Unit) {
                while (!TokenManager.isLoaded) {
                    delay(100)
                }
                delay(500)

                val destination = when {
                    TokenManager.hasValidToken() -> "home"
                    onboardingCompleted -> "login"
                    else -> "welcome"
                }
                navController.navigate(destination) {
                    popUpTo("splash") { inclusive = true }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFEFAF6)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.mi_logo),
                        contentDescription = null,
                        modifier = Modifier.size(140.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 🚀 Llamamos a la función que ahora está afuera
                    LoadingDotsAnimation()
                }
            }
        }

        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                viewModel = viewModel(),
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        @Suppress("SpellCheckingInspection")
        composable("register") {
            RegisterScreen(
                viewModel = viewModel(),
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                onNavigateToCart = { navController.navigate("catalog_screen") },
                onNavigateToProfile = { navController.navigate("profile/my_account") }
            )
        }

        composable("catalog_screen") {
            CatalogScreen(viewModel = viewModel(), navController = navController)
        }
        
        composable("user_list_screen") {
            UserListScreen(
                viewModel = messagesViewModel,
                onUserSelected = { navController.navigate("messages_screen") })
        }

        composable("messages_screen") {
            MessagesScreen(
                viewModel = messagesViewModel,
                onNavigateBack = { navController.popBackStack() })
        }

        composable("messages_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            LaunchedEffect(userId) { messagesViewModel.selectUser(userId) }
            MessagesScreen(
                viewModel = messagesViewModel,
                onNavigateBack = { navController.popBackStack() })
        }

        composable("profile/my_account") {
            UserProfileScreen(
                isMyProfile = true,
                onNavigateToChat = { navController.navigate("user_list_screen") },
                onNavigateToCatalog = { navController.navigate("catalog_screen") }
            )
        }

        composable("profile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfileScreen(
                userId = userId,
                isMyProfile = userId == "my_account" || userId == TokenManager.currentUserId,
                onNavigateToChat = { navController.navigate("messages_screen/$userId") },
                onNavigateToCatalog = { navController.navigate("catalog_screen") }
            )
        }
    }
}

// 💡 ESTA FUNCIÓN TIENE QUE ESTAR AQUÍ AFUERA PARA QUE NO HAYA ERRORES
@Composable
fun LoadingDotsAnimation(color: Color = Color(0xFF2F80ED)) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    @Composable
    fun animateDotAlpha(delay: Int): Float {
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 600
                    0.2f at delay
                    1f at delay + 300
                    0.2f at 600
                },
                repeatMode = RepeatMode.Restart
            ), label = ""
        )
        return alpha
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(color.copy(alpha = animateDotAlpha(0))))
        Box(Modifier.size(10.dp).clip(CircleShape).background(color.copy(alpha = animateDotAlpha(200))))
        Box(Modifier.size(10.dp).clip(CircleShape).background(color.copy(alpha = animateDotAlpha(400))))
    }
}
