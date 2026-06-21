package com.example.irozumi.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.irozumi.features.home.presentation.components.ArtworkCard
import com.example.irozumi.features.home.presentation.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IroZumi", fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, color = Color(0xFF3D405B)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFEFAF6))
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFEFAF6))
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Buscador Dinámico
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.searchQueryChanged(it) }, // Lógica reactiva al escribir
                    placeholder = { Text("Buscar obras, artistas o estilos") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Carrusel de Categorías (Filtros Reactivos)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.categories) { category ->
                        val isSelected = state.selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onCategorySelected(category) },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE07A5F),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Banner "Explora nuevas técnicas"
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "➕ Explora nuevas técnicas y publica tu proceso", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            // Feed de Obras Filtradas y Completamente Interactivas
            items(state.artworks, key = { it.id }) { artwork ->
                ArtworkCard(
                    post = artwork,
                    onLikeClick = { viewModel.toggleLike(artwork.id) } // Suma/Resta likes instantáneamente
                )
            }
        }
    }
}

// Extensión temporal para mapeo de signaturas
private fun HomeViewModel.searchQueryChanged(q: String) = this.onSearchQueryChanged(q)