package com.irozumi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.irozumi.core.navigation.NavGraph
import com.irozumi.core.ui.theme.IroZumiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IroZumiTheme {
                val navController = rememberNavController()

                // Observamos la ruta actual
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Definimos las rutas de autenticación (Donde NO se muestra la barra inferior)
                val authRoutes = listOf("welcome", "login", "register")
                val showBottomBar = currentRoute != null && currentRoute !in authRoutes

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                // Pestaña Inicio
                                NavigationBarItem(
                                    selected = currentRoute == "home" || currentRoute?.startsWith("post_detail") == true,
                                    label = { Text("Inicio") },
                                    icon = { Text("🏠", fontSize = 20.sp) },
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )

                                // Pestaña Gym
                                NavigationBarItem(
                                    selected = currentRoute == "gym",
                                    label = { Text("Gym") },
                                    icon = { Text("💪", fontSize = 20.sp) },
                                    onClick = {
                                        navController.navigate("gym") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}