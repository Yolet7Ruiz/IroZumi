package com.irozumi.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.irozumi.features.auth.presentation.welcome.screens.WelcomeScreen
import com.irozumi.features.auth.presentation.login.screens.LoginScreen
import com.irozumi.features.auth.presentation.login.viewmodels.LoginViewModel
import com.irozumi.features.auth.presentation.register.screens.RegisterScreen
import com.irozumi.features.auth.presentation.register.viewmodels.RegisterViewModel
import com.irozumi.features.home.presentation.screens.HomeScreen
import com.irozumi.features.messages.presentation.screens.MessagesScreen
import com.irozumi.features.messages.presentation.screens.UserListScreen
import com.irozumi.features.messages.presentation.viewmodel.MessagesViewModel
import com.irozumi.features.profile.presentation.screens.UserProfileScreen
// IMPORTACIONES DEL NUEVO CATÁLOGO
import com.irozumi.features.catalog.presentation.screens.CatalogScreen
import com.irozumi.features.catalog.presentation.viewmodel.CatalogViewModel

@Suppress("SpellCheckingInspection")
@Composable
fun NavGraph(navController: NavHostController) {
    // Compartimos la misma instancia del ViewModel para mantener el estado sincronizado
    val messagesViewModel: MessagesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("register") {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                navController = navController,
                // 🛒 CONECTADO CON ÉXITO: Al presionar el carrito va al catálogo
                onNavigateToCart = {
                    navController.navigate("catalog_screen")
                },
                onNavigateToProfile = {
                    navController.navigate("profile/my_account")
                }
            )
        }

        // 🏷️ NUEVA RUTA: Pantalla del Catálogo de Ventas
        composable("catalog_screen") {
            val catalogViewModel: CatalogViewModel = viewModel()
            CatalogScreen(
                viewModel = catalogViewModel,
                navController = navController
            )
        }

        // 1. Vista Principal: Lista de Contactos
        composable("user_list_screen") {
            UserListScreen(
                viewModel = messagesViewModel,
                onUserSelected = { userId ->
                    // Al seleccionar un usuario de la lista, navegamos al chat detallado
                    navController.navigate("messages_screen")
                }
            )
        }

        // 2. Vista del Chat Detallado (Funcionalidad de abajo hacia arriba)
        composable("messages_screen") {
            MessagesScreen(
                viewModel = messagesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val isMyProfile = userId == "my_account"

            UserProfileScreen(
                isMyProfile = isMyProfile,
                onNavigateToChat = {
                    navController.navigate("user_list_screen")
                },
                onNavigateToCatalog = {
                    navController.navigate("catalog_screen")
                }
            )
        }
    }
}