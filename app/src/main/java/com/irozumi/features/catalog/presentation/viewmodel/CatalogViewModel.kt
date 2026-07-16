package com.irozumi.features.catalog.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.features.catalog.data.datasource.CatalogRemoteDataSource
import com.irozumi.features.catalog.domain.model.ArtworkProduct
import com.irozumi.features.catalog.presentation.screens.CatalogUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CatalogViewModel : ViewModel() {

    private val dataSource = CatalogRemoteDataSource()
    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    init {
        loadCatalog()
    }

    fun loadCatalog(category: String? = null) {
        viewModelScope.launch {
            try {
                val remote = dataSource.getCatalog(category)
                val products = remote.map {
                    ArtworkProduct(
                        id = it.id, title = it.title, artistName = it.artistName,
                        price = it.price, rating = it.rating, imageUrl = it.imageUrl ?: "",
                        category = it.category, artistId = it.artistId
                    )
                }
                _uiState.value = CatalogUiState.Success(
                    products = products,
                    selectedCategory = category ?: "Todos"
                )
            } catch (e: Exception) {
                _uiState.value = CatalogUiState.Error("Error al cargar catálogo")
            }
        }
    }

    fun cambiarCategoria(category: String) {
        loadCatalog(category)
    }

    fun toggleFavorito(productId: String) {
        val state = _uiState.value
        if (state is CatalogUiState.Success) {
            val updated =
                state.products.map { if (it.id == productId) it.copy(isFavorite = !it.isFavorite) else it }
            _uiState.value = state.copy(products = updated)
        }
    }

    fun subirObraParaVenta(titulo: String, precio: Double, categoria: String, imageBase64: String) {
        android.util.Log.e("IroZumi", "Subiendo obra: $titulo")
        viewModelScope.launch {
            try {
                dataSource.createProduct(titulo, precio, categoria, imageBase64)
                android.util.Log.e("IroZumi", "Obra publicada")
                loadCatalog()
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error: ${e.message}")
            }
        }
    }
}