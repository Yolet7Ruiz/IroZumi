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

        // Registro - AQUÍ ESTABA EL ERROR
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
                    // Solución: pasar el lambda para volver atrás o ir al login
                    navController.popBackStack()
                }
            )
        }

        // ... (resto de rutas: home, gym, post_detail)
    }
}