package com.irozumi.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.irozumi.features.auth.presentation.welcome.screens.WelcomeScreen
import com.irozumi.features.auth.presentation.login.screens.LoginScreen
import com.irozumi.features.auth.presentation.login.viewmodels.LoginViewModel
import com.irozumi.features.auth.presentation.register.screens.RegisterScreen
import com.irozumi.features.auth.presentation.register.viewmodels.RegisterViewModel
import com.irozumi.features.home.presentation.screens.HomeScreen

@Suppress("SpellCheckingInspection")
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome" // 💡 CORREGIDO: Ahora la app inicia correctamente en la Bienvenida
    ) {
        // 1. Pantalla de Bienvenida (Entrada de la App)
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        // 2. Pantalla de Inicio de Sesión
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    // Viaja al home y limpia el stack para que no pueda regresar al login sin desloguearse
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        // 3. Pantalla de Registro de Usuarios
        composable("register") {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = {
                    // Viaja al home y limpia el stack de manera segura
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        // 4. Pantalla Principal de la Aplicación (Donde están tus pestañas y retos)
        composable("home") {
            HomeScreen(
                onNavigateToCart = { /* Navegar al carrito */ },
                onNavigateToProfile = { /* Navegar al perfil */ }
            )
        }
    }
}