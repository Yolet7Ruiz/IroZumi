package com.irozumi.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.irozumi.features.auth.presentation.detail.screen.PostDetailScreen

// IMPORTANTE: Verifica si tu carpeta es "screen" o "screens"
import com.irozumi.features.home.presentation.detail.viewmodel.PostDetailViewModel
import com.irozumi.features.gym.presentation.screens.GymScreen
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "gym" // Cambiamos a gym para que puedas probarlo
    ) {
        composable(route = "login") { /* LoginScreen(...) */ }
        composable(route = "register") { /* RegisterScreen(...) */ }

        // Sección Gym
        composable(route = "gym") {
            val gymViewModel: GymViewModel = viewModel()
            GymScreen(viewModel = gymViewModel)
        }

        // Pantalla de Detalle de Obra
        composable(
            route = "post_detail/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) {
            val postDetailViewModel: PostDetailViewModel = viewModel()
            PostDetailScreen(
                onBack = { navController.popBackStack() },
                viewModel = postDetailViewModel
            )
        }
    }
}