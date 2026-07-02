package com.irozumi.features.catalog.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.catalog.domain.model.ArtworkProduct
import com.irozumi.features.catalog.presentation.screens.CatalogUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        cargarProductosIniciales()
    }

    private fun cargarProductosIniciales() {
        // Datos de ejemplo idénticos a los de tu imagen de referencia
        val listaDemo = listOf(
            ArtworkProduct("1", "Atardecer Sereno", "@BandaRomantica24", 800.0, 4.9, null, "Acrílico"),
            ArtworkProduct("2", "Explosión de Color", "@ArteVivo", 950.0, 4.7, null, "Anime"),
            ArtworkProduct("3", "Mirada Urbana", "@PincelMX", 680.0, 4.5, null, "Realismo"),
            ArtworkProduct("4", "Bosque en Acuarela", "@AcuarelaArt", 1200.0, 5.0, null, "Realismo")
        )
        _uiState.value = CatalogUiState.Success(products = listaDemo, selectedCategory = "Todos")
    }

    fun cambiarCategoria(nuevaCategoria: String) {
        val estadoActual = _uiState.value
        if (estadoActual is CatalogUiState.Success) {
            _uiState.update { estadoActual.copy(selectedCategory = nuevaCategoria) }
        }
    }

    // Publicación de una obra para la venta desde el formulario de la app
    fun subirObraParaVenta(titulo: String, precio: Double, categoria: String, imagen: Uri?) {
        viewModelScope.launch {
            val estadoActual = _uiState.value
            if (estadoActual is CatalogUiState.Success) {
                val nuevaObra = ArtworkProduct(
                    id = System.currentTimeMillis().toString(),
                    title = titulo,
                    artistName = "@MiUsuarioArtista",
                    price = precio,
                    rating = 5.0,
                    imageUri = imagen,
                    category = categoria
                )
                _uiState.update {
                    estadoActual.copy(products = listOf(nuevaObra) + estadoActual.products)
                }
            }
        }
    }

    fun toggleFavorito(productoId: String) {
        val estadoActual = _uiState.value
        if (estadoActual is CatalogUiState.Success) {
            val listaActualizada = estadoActual.products.map {
                if (it.id == productoId) it.copy(isFavorite = !it.isFavorite) else it
            }
            _uiState.update { estadoActual.copy(products = listaActualizada) }
        }
    }
}