package com.irozumi.features.home.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.irozumi.data.local.AppDatabase
import com.irozumi.features.home.data.dataSource.AuthorResponse
import com.irozumi.features.home.data.dataSource.HomeRemoteDataSource
import com.irozumi.features.home.data.repository.HomeRepositoryImpl
import com.irozumi.features.home.domain.model.ArtworkPost
import com.irozumi.features.home.domain.model.Comment
import com.irozumi.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private val likedPostIds = mutableSetOf<String>()
    }

    private val dataSource = HomeRemoteDataSource()
    private val database = AppDatabase.getInstance(application)
    private val repository: HomeRepository = HomeRepositoryImpl(
        remoteDataSource = dataSource,
        postDao = database.postDao()
    )

    private val _topArtists = MutableStateFlow<List<AuthorResponse>>(emptyList())
    val topArtists: StateFlow<List<AuthorResponse>> = _topArtists.asStateFlow()

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadPosts()
        loadTopArtists()
    }

    private fun loadTopArtists() {
        viewModelScope.launch {
            try {
                _topArtists.value = dataSource.getTopArtists()
            } catch (e: Exception) { }
        }
    }

    fun onCategorySelected(category: String) {
        _state.update { it.copy(selectedCategory = category) }
        loadPosts()
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        loadPosts()
    }

    fun onSearchToggle(isSearching: Boolean) {
        _state.update { it.copy(isSearching = isSearching) }
        if (!isSearching) {
            _state.update { it.copy(searchQuery = "") }
            loadPosts()
        }
    }

    fun onLikeToggled(postId: String) {
        val isNowLiked = !likedPostIds.contains(postId)
        if (isNowLiked) likedPostIds.add(postId) else likedPostIds.remove(postId)

        _state.update { currentState ->
            val updatedList = currentState.artworks.map { post ->
                if (post.id == postId) post.copy(
                    isLikedByUser = isNowLiked,
                    likesCount = if (isNowLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(
                        0
                    )
                ) else post
            }
            currentState.copy(artworks = updatedList)
        }

        viewModelScope.launch {
            repository.toggleLike(postId).onFailure {
                loadPosts()
            }
        }
    }

    fun onDeletePost(postId: String) {
        viewModelScope.launch {
            try {
                dataSource.deletePost(postId)
                loadPosts()
            } catch (e: Exception) {
                _state.update { it.copy(errorMessage = "No se pudo eliminar") }
            }
        }
    }

    fun onActivePostForComments(post: ArtworkPost?) {
        _state.update { it.copy(activePostForComments = post, commentError = null) }
        if (post != null) {
            loadComments(post.id)
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isCommentsLoading = true, commentError = null) }
            repository.getComments(postId).onSuccess { comments ->
                _state.update { it.copy(comments = comments, isCommentsLoading = false) }
            }.onFailure {
                _state.update {
                    it.copy(
                        isCommentsLoading = false,
                        commentError = "No se pudieron cargar los comentarios"
                    )
                }
            }
        }
    }

    fun postComment(postId: String, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(commentError = null) }
            repository.postComment(postId, content).onSuccess {
                // Actualizar contador en la tarjeta
                _state.update { currentState ->
                    val updatedList = currentState.artworks.map { post ->
                        if (post.id == postId) post.copy(comments = post.comments + 1) else post
                    }
                    currentState.copy(artworks = updatedList)
                }
                loadComments(postId)
            }.onFailure {
                _state.update { it.copy(commentError = "Error al enviar el comentario") }
            }
        }
    }

    fun onCreatePost(title: String, description: String, category: String, imageBase64: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.createPost(title, description, category, imageBase64).onSuccess {
                    loadPosts()
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al crear post") }
            }
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val posts = repository.getPosts(_state.value.selectedCategory, _state.value.searchQuery)
                val updatedPosts = posts.map { post ->
                    post.copy(isLikedByUser = likedPostIds.contains(post.id))
                }
                _state.update { it.copy(artworks = updatedPosts, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar publicaciones") }
            }
        }
    }
    }