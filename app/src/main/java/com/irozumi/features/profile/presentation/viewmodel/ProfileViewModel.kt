package com.irozumi.features.profile.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.irozumi.features.profile.data.datasource.ProfileRemoteDataSource
import kotlinx.coroutines.launch
import com.irozumi.core.security.TokenManager
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dataSource = ProfileRemoteDataSource()
    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun loadProfile(userId: String) {
        android.util.Log.e("IroZumi", "Cargando perfil: $userId")
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val profile = dataSource.getProfile(userId)
                android.util.Log.e(
                    "IroZumi",
                    "Perfil recibido - name: ${profile.displayName}, bio: ${profile.bio}, insta: ${profile.instagram}, twitter: ${profile.twitter}"
                )
                val posts = dataSource.getUserPosts(userId, userId).map {
                    ProfilePost(
                        it.id, it.title, it.description ?: "", it.style ?: "",
                        it.likesCount, it.isLiked, it.commentsCount, it.imageUrl ?: ""
                    )
                }
                uiState = uiState.copy(
                    isLoading = false,
                    name = profile.displayName,
                    username = "@${profile.username}",
                    bio = profile.bio ?: "",
                    profilePictureUrl = profile.profilePictureUrl ?: "",
                    coverPictureUrl = profile.coverPictureUrl ?: "",
                    instagram = profile.instagram ?: "",
                    twitter = profile.twitter ?: "",
                    posts = posts
                )
                android.util.Log.e("IroZumi", "Perfil cargado")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error perfil: ${e.message}")
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun setProfileMode(isMyProfile: Boolean) {
        uiState = uiState.copy(isMyProfile = isMyProfile)
    }

    fun toggleFollow(userId: String) {
        val previous = uiState.isFollowing
        uiState = uiState.copy(isFollowing = !previous)

        viewModelScope.launch {
            try {
                val isFollowing = dataSource.toggleFollow(userId)
                uiState = uiState.copy(isFollowing = isFollowing)
                android.util.Log.e("IroZumi", "Follow actualizado: $isFollowing")
            } catch (e: Exception) {
                uiState = uiState.copy(isFollowing = previous)
                android.util.Log.e("IroZumi", "Error follow: ${e.message}")
            }
        }
    }

    fun toggleLike(postId: String) {
        // 1. Cambio optimista: UI inmediata
        val previousPosts = uiState.posts
        uiState = uiState.copy(
            posts = uiState.posts.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLiked
                    post.copy(isLiked = newLiked, likesCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1)
                } else post
            }
        )

        // 2. Llamar al backend
        viewModelScope.launch {
            try {
                val updatedPost = dataSource.toggleLike(postId)
                android.util.Log.e("IroZumi", "Like actualizado - postId: $postId, likes: ${updatedPost.likesCount}, isLiked: ${updatedPost.likesCount > 0}")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error like: ${e.message}")
                // 3. Si falla, revertir al estado anterior
                uiState = uiState.copy(posts = previousPosts)
            }
        }
    }

    fun toggleEditMode() {
        uiState = uiState.copy(isEditing = !uiState.isEditing)
    }

    fun updateProfileData(newName: String, newBio: String, newInsta: String, newX: String) {
        android.util.Log.e(
            "IroZumi",
            "LLAMANDO updateProfileData - name: $newName, bio: $newBio, insta: $newInsta, twitter: $newX"
        )
        viewModelScope.launch {
            try {
                dataSource.updateProfile(
                    TokenManager.currentUserId,
                    newName,
                    newBio,
                    newInsta,
                    newX
                )
                android.util.Log.e(
                    "IroZumi",
                    "updateProfileData EXITOSO - actualizando estado local"
                )
                uiState = uiState.copy(
                    name = newName,
                    bio = newBio,
                    instagram = newInsta,
                    twitter = newX,
                    isEditing = false
                )
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "ERROR updateProfileData: ${e.message}")
            }
        }
    }

    fun updateProfilePicture(uri: Uri?) {
        uiState = uiState.copy(profilePictureUri = uri)
        uri?.let { uploadToCloudinary(it) { url -> updateProfileOnServer(profilePictureUrl = url) } }
    }

    fun updateCoverPicture(uri: Uri?) {
        uiState = uiState.copy(coverPictureUri = uri)
        uri?.let { uploadToCloudinary(it) { url -> updateProfileOnServer(coverPictureUrl = url) } }
    }

    fun onActivePostForComments(post: ProfilePost?) {
        uiState = uiState.copy(activePostForComments = post, comments = emptyList(), commentError = null)
        post?.let { loadComments(it.id) }
    }

    fun onImageClick(imageUrl: String?) {
        uiState = uiState.copy(fullScreenImage = imageUrl)
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isCommentsLoading = true, commentError = null)
            try {
                val comments = dataSource.getComments(postId).map {
                    CommentData(id = it.id, authorName = it.author.username, text = it.content)
                }
                uiState = uiState.copy(comments = comments, isCommentsLoading = false)
                android.util.Log.e("IroZumi", "Comentarios cargados: ${comments.size}")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error comentarios: ${e.message}")
                uiState = uiState.copy(isCommentsLoading = false, commentError = "No se pudieron cargar los comentarios")
            }
        }
    }

    fun postComment(postId: String, text: String) {
        viewModelScope.launch {
            try {
                dataSource.postComment(postId, text)
                loadComments(postId) // Recargar comentarios
                android.util.Log.e("IroZumi", "Comentario enviado")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error enviando comentario: ${e.message}")
                uiState = uiState.copy(commentError = "No se pudo enviar el comentario")
            }
        }
    }

    fun resetState() {
        uiState = ProfileUiState()
    }

    private fun uploadToCloudinary(uri: Uri, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                val resized = android.graphics.Bitmap.createScaledBitmap(bitmap, 512, 512, true)
                val outputStream = java.io.ByteArrayOutputStream()
                resized.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream)
                val base64 = android.util.Base64.encodeToString(
                    outputStream.toByteArray(),
                    android.util.Base64.NO_WRAP
                )

                // Subir al backend (el backend lo sube a Cloudinary)
                android.util.Log.e("IroZumi", "Subiendo imagen a Cloudinary...")
                val url = dataSource.uploadProfileImage(base64)
                android.util.Log.e("IroZumi", "Imagen subida: $url")
                onSuccess(url)
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error subiendo imagen: ${e.message}")
            }
        }
    }

    private fun updateProfileOnServer(
        profilePictureUrl: String? = null,
        coverPictureUrl: String? = null
    ) {
        viewModelScope.launch {
            try {
                android.util.Log.e(
                    "IroZumi",
                    "Actualizando perfil con imágenes - profileUrl: $profilePictureUrl, coverUrl: $coverPictureUrl"
                )
                dataSource.updateProfile(
                    TokenManager.currentUserId,
                    uiState.name, uiState.bio, uiState.instagram, uiState.twitter,
                    profilePictureUrl,
                    coverPictureUrl
                )
                android.util.Log.e("IroZumi", "Perfil actualizado con imágenes")
            } catch (e: Exception) {
                android.util.Log.e("IroZumi", "Error updateProfileOnServer: ${e.message}")
            }
        }
    }
}