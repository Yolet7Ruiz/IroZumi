package com.irozumi.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// Imports de pantallas
import com.irozumi.features.auth.presentation.welcome.screens.WelcomeScreen
import com.irozumi.features.auth.presentation.login.screens.LoginScreen
import com.irozumi.features.auth.presentation.register.screens.RegisterScreen
import com.irozumi.features.home.presentation.screens.HomeScreen // IMPORTACIÓN DE TU NUEVA SCREEN

// Imports de ViewModels
import com.irozumi.features.auth.presentation.login.viewmodels.LoginViewModel
import com.irozumi.features.auth.presentation.register.viewmodels.RegisterViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        // Welcome
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // Login
        composable("login") {
            val loginVm: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginVm,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // Registro
        composable("register") {
            val registerVm: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerVm,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // RUTA HOME INTEGRADA CON TUS NUEVOS COMPONENTES
        composable("home") {
            HomeScreen(
                onNavigateToPostDetail = { postId ->
                    navController.navigate("post_detail/$postId")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        // ... (Rutas comodín para que compile si das clic en los callbacks de perfil o detalle)
        composable("post_detail/{postId}") { /* Pantalla detalle de la obra */ }
        composable("profile") { /* Pantalla del perfil del artista */ }
    }
}