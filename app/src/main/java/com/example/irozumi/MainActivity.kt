package com.example.irozumi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.irozumi.core.ui.theme.IroZumiTheme

// --- IMPORTACIONES DEL GYM ---
import com.example.irozumi.features.gym.presentation.screens.GymScreen
import com.example.irozumi.features.gym.presentation.viewmodel.GymViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IroZumiTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        // 1. Instanciamos el ViewModel del Gym
                        val gymViewModel: GymViewModel = viewModel()

                        // 2. Cargamos la pantalla del Gym
                        GymScreen(viewModel = gymViewModel)

                        /*
                        Nota: He quitado WelcomeScreen para que puedas ver
                        directamente la de Gym al ejecutar.
                        */
                    }
                }
            }
        }
    }
}