package com.irozumi.core.navigation

sealed class Screen(val route: String) {
    // Grafo de Autenticación
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")

    // Destinos Principales (Bottom Bar)
    object Home : Screen("home")
    object Gym : Screen("gym")
    object Store : Screen("store")
    object Profile : Screen("profile")

    // Sub-pantallas / Flujos Adicionales indispensables
    object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }
    object Chat : Screen("chat/{partnerName}") {
        fun createRoute(partnerName: String) = "chat/$partnerName"
    }
    object Settings : Screen("settings")
}