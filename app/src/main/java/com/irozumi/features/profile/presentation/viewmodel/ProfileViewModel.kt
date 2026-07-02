package com.irozumi.features.profile.presentation.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        // Inicializamos con posts de prueba
        uiState = uiState.copy(
            posts = listOf(
                ProfilePost(1, "Atardecer Acuarela", "Práctica de luces cálidas húmedas.", "Acuarela", 1200, false, 45),
                ProfilePost(2, "Estudio de Anatomía", "Boceto rápido digital.", "Digital", 450, false, 12)
            )
        )
    }

    fun setProfileMode(isMyProfile: Boolean) {
        uiState = uiState.copy(
            isMyProfile = isMyProfile,
            name = if (isMyProfile) "Yolet Ruiz" else "Carlos Mendoza",
            username = if (isMyProfile) "@yolet_art" else "@carlos_concept",
            bio = if (isMyProfile) "Artista conceptual | Acuarelas." else "Ilustrador digital freelance."
        )
    }

    fun toggleFollow() {
        uiState = uiState.copy(isFollowing = !uiState.isFollowing)
    }

    fun toggleLike(postId: Int) {
        uiState = uiState.copy(
            posts = uiState.posts.map { post ->
                if (post.id == postId) {
                    val newLiked = !post.isLiked
                    post.copy(
                        isLiked = newLiked,
                        likesCount = if (newLiked) post.likesCount + 1 else post.likesCount - 1
                    )
                } else post
            }
        )
    }

    fun toggleEditMode() {
        uiState = uiState.copy(isEditing = !uiState.isEditing)
    }

    fun updateProfileData(newName: String, newBio: String, newInsta: String, newX: String) {
        uiState = uiState.copy(
            name = newName,
            bio = newBio,
            instagram = newInsta,
            twitter = newX,
            isEditing = false
        )
    }

    fun updateProfilePicture(uri: Uri?) {
        uiState = uiState.copy(profilePictureUri = uri)
    }

    fun updateCoverPicture(uri: Uri?) {
        uiState = uiState.copy(coverPictureUri = uri)
    }
}