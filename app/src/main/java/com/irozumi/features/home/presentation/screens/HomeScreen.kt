    package com.irozumi.features.home.presentation.screens

    import android.Manifest
    import android.content.Intent
    import android.net.Uri
    import androidx.activity.compose.rememberLauncherForActivityResult
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.compose.animation.AnimatedVisibility
    import androidx.compose.animation.fadeIn
    import androidx.compose.animation.fadeOut
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.background
    import androidx.compose.foundation.border
    import androidx.compose.foundation.clickable
    import androidx.compose.foundation.horizontalScroll
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.grid.GridCells
    import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
    import androidx.compose.foundation.lazy.grid.items
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.automirrored.filled.ExitToApp
    import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
    import androidx.compose.material.icons.automirrored.filled.Send
    import androidx.compose.material.icons.filled.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.layout.ContentScale
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.res.painterResource
    import androidx.compose.ui.res.stringResource
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.lifecycle.viewmodel.compose.viewModel
    import androidx.navigation.NavHostController
    import coil.compose.AsyncImage
    import com.irozumi.R
    import com.irozumi.features.home.domain.model.ArtworkPost
    import com.irozumi.features.home.presentation.components.ArtworkCard
    import com.irozumi.features.home.presentation.viewmodel.HomeViewModel
    import com.irozumi.features.gym.presentation.screens.GymScreen
    import com.irozumi.features.gym.di.GymViewModelFactory
    import com.irozumi.features.gym.presentation.viewmodel.GymViewModel
    import com.irozumi.features.challenges.presentation.screens.ChallengesScreen
    import com.irozumi.features.challenges.presentation.viewmodel.ChallengesViewModel
    import com.irozumi.features.notifications.presentation.screens.NotificationsScreen
    import com.irozumi.features.notifications.presentation.viewmodel.NotificationsViewModel
    import com.irozumi.features.messages.presentation.screens.MessagesScreen
    import com.irozumi.features.messages.presentation.screens.UserListScreen
    import com.irozumi.features.messages.presentation.viewmodel.MessagesViewModel
    import com.irozumi.features.messages.presentation.screens.MessagesUiState
    import kotlinx.coroutines.launch
    import android.graphics.Bitmap
    import android.provider.MediaStore
    import java.io.ByteArrayOutputStream
    import androidx.compose.ui.window.Dialog
    import androidx.compose.ui.window.DialogProperties
    import com.irozumi.core.security.TokenManager
    import com.irozumi.features.profile.presentation.viewmodel.ProfileViewModel
    import android.app.Application
    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("SpellCheckingInspection")
    @Composable
    fun HomeScreen(
        navController: NavHostController,
        onNavigateToCart: () -> Unit,
        onNavigateToProfile: () -> Unit,
        homeViewModel: HomeViewModel = viewModel()
    ) {
        val state by homeViewModel.state.collectAsState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        var selectedTab by remember { mutableIntStateOf(0) }
        var showUploadBottomSheet by remember { mutableStateOf(false) }
        var showFormDialog by remember { mutableStateOf(false) }
        var newPostDescription by remember { mutableStateOf("") }
        var newPostCategory by remember { mutableStateOf("Anime") }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var postBeingEdited by remember { mutableStateOf<ArtworkPost?>(null) }
        var editDescriptionText by remember { mutableStateOf("") }
        var fullScreenImage by remember { mutableStateOf<String?>(null) }

        val brandBlue = Color(0xFF2F80ED)
        val textDark = Color(0xFF3D405B)
        val backgroundColor = Color(0xFFF4F6F9)
        val categories = state.categories
        val currentUserName = TokenManager.currentUserName.ifBlank { "Usuario" }
        val profileViewModel: ProfileViewModel = viewModel()
        val profileState = profileViewModel.uiState

// Cargar perfil para obtener la imagen
        LaunchedEffect(Unit) {
            profileViewModel.loadProfile(TokenManager.currentUserId)
        }

        val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) { selectedImageUri = uri; showFormDialog = true }
        }
        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
                val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "IroZumi_${System.currentTimeMillis()}", null)
                selectedImageUri = Uri.parse(path)
                showFormDialog = true
            }
        }
        val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) { cameraLauncher.launch(null) }
        }

        val messagesViewModel: MessagesViewModel = viewModel()
        val messagesState = messagesViewModel.uiState
        val isInsideIndividualChat = selectedTab == 1 &&
                messagesState is MessagesUiState.Success && messagesState.selectedUserId != null

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = Color.White, modifier = Modifier.width(300.dp)) {
                    Column(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { scope.launch { drawerState.close() }; onNavigateToProfile() }
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            when {
                                profileState.profilePictureUrl.isNotEmpty() -> {
                                    AsyncImage(model = profileState.profilePictureUrl, contentDescription = "Foto de perfil",
                                        contentScale = ContentScale.Crop, modifier = Modifier.size(54.dp).clip(CircleShape).border(1.5.dp, brandBlue, CircleShape))
                                }
                                else -> {
                                    Image(painter = painterResource(id = R.drawable.mi_logo), contentDescription = "Foto de perfil",
                                        contentScale = ContentScale.Crop, modifier = Modifier.size(54.dp).clip(CircleShape).border(1.5.dp, brandBlue, CircleShape))
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(currentUserName, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2B2D42))
                                Text("Mi cuenta", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                        }
                        HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                        Spacer(modifier = Modifier.weight(1f))
                        HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(6.dp))
                        NavigationDrawerItem(icon = { Icon(Icons.Default.Settings, null, tint = Color.Gray) }, label = { Text("Configuración y privacidad", color = Color.DarkGray, fontSize = 14.sp) }, selected = false, onClick = { }, modifier = Modifier.padding(horizontal = 12.dp), colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent))
                        NavigationDrawerItem(icon = { Icon(Icons.Default.Info, null, tint = Color.Gray) }, label = { Text("Ayuda y soporte técnico", color = Color.DarkGray, fontSize = 14.sp) }, selected = false, onClick = { }, modifier = Modifier.padding(horizontal = 12.dp), colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent))
                        NavigationDrawerItem(icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color.Gray) }, label = { Text("Salir de la cuenta", color = Color.DarkGray, fontSize = 14.sp) }, selected = false, onClick = {
                            scope.launch { drawerState.close() }
                            profileViewModel.resetState()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }, modifier = Modifier.padding(horizontal = 12.dp), colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent))
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    if (!isInsideIndividualChat) {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Black, fontSize = 24.sp, color = brandBlue) },
                            navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) { Icon(Icons.Default.Menu, null, tint = textDark) } },
                            actions = {
                                IconButton(onClick = { homeViewModel.onSearchToggle(true) }) { Icon(Icons.Default.Search, null, tint = textDark) }
                                IconButton(onClick = onNavigateToCart) { Icon(Icons.Default.ShoppingCart, null, tint = textDark) }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                        )
                    }
                },
                bottomBar = {
                    if (!isInsideIndividualChat) {
                        NavigationBar(containerColor = Color.White, tonalElevation = 8.dp, windowInsets = WindowInsets(0, 0, 0, 0), modifier = Modifier.height(54.dp)) {
                            NavigationBarItem(icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(22.dp)) }, label = { Text(stringResource(R.string.tab_home), fontSize = 10.sp) }, selected = selectedTab == 0 && !state.isSearching, onClick = { selectedTab = 0; homeViewModel.onSearchToggle(false) })
                            NavigationBarItem(icon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(22.dp)) }, label = { Text(stringResource(R.string.tab_messages), fontSize = 10.sp) }, selected = selectedTab == 1, onClick = { selectedTab = 1 })
                            NavigationBarItem(icon = { Icon(Icons.Default.Star, null, modifier = Modifier.size(22.dp)) }, label = { Text(stringResource(R.string.tab_dynamics), fontSize = 10.sp) }, selected = selectedTab == 2, onClick = { selectedTab = 2 })
                            NavigationBarItem(icon = { Icon(Icons.Default.FitnessCenter, null, modifier = Modifier.size(22.dp)) }, label = { Text(stringResource(R.string.tab_gym), fontSize = 10.sp) }, selected = selectedTab == 3, onClick = { selectedTab = 3 })
                            NavigationBarItem(icon = { Icon(Icons.Default.Notifications, null, modifier = Modifier.size(22.dp)) }, label = { Text(stringResource(R.string.tab_notifications), fontSize = 10.sp) }, selected = selectedTab == 4, onClick = { selectedTab = 4 })
                        }
                    }
                },
                containerColor = backgroundColor
            ) { innerPadding ->
                val customModifier = if (isInsideIndividualChat) Modifier.fillMaxSize() else Modifier.fillMaxSize().padding(innerPadding)
                Box(modifier = customModifier) {
                    when (selectedTab) {
                        0 -> {
                            Column {
                                Column(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
                                    Text("Artistas Destacados", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textDark, modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
                                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                        val topArtists by homeViewModel.topArtists.collectAsState()
                                        topArtists.forEach { artist ->
                                            android.util.Log.e("IroZumi", "Artista: ${artist.id} - ${artist.username}")
                                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("profile/${artist.id}") }) {
                                                Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(brandBlue.copy(alpha = 0.08f)).border(1.5.dp, brandBlue, CircleShape)) {
                                                    if (!artist.avatarUrl.isNullOrEmpty()) {
                                                        AsyncImage(model = artist.avatarUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize().clip(CircleShape))
                                                    } else {
                                                        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
                                                            Text(artist.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(artist.username, fontSize = 11.sp, color = textDark, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))

                                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    categories.forEach { category ->
                                        FilterChip(selected = state.selectedCategory == category, onClick = { homeViewModel.onCategorySelected(category) }, label = { Text(category) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = brandBlue, selectedLabelColor = Color.White), shape = RoundedCornerShape(20.dp))
                                    }
                                }

                                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    item {
                                        Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).clickable { showUploadBottomSheet = true }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(40.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, null, tint = brandBlue) }
                                                Spacer(modifier = Modifier.width(12.dp)); Text(stringResource(R.string.explore_placeholder), color = Color.Gray, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                    if (state.isLoading) { item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = brandBlue) } } }
                                    state.errorMessage?.let { error -> item { Text(error, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(16.dp)) } }
                                    items(state.artworks) { post ->
                                        ArtworkCard(post = post, brandBlue = brandBlue, textDark = textDark,
                                            onLikeToggle = { homeViewModel.onLikeToggled(post.id) },
                                            onCommentsClick = { homeViewModel.onActivePostForComments(post) },
                                            onShareClick = {
                                                val shareIntent = Intent().apply { action = Intent.ACTION_SEND; putExtra(Intent.EXTRA_TEXT, "🎨 Mira esta obra en IroZumi: ${post.title}"); type = "text/plain" }
                                                context.startActivity(Intent.createChooser(shareIntent, null))
                                            },
                                            onDeleteClick = { homeViewModel.onDeletePost(post.id) },
                                            onEditClick = { currentDescription -> postBeingEdited = post; editDescriptionText = currentDescription },
                                            onImageClick = { fullScreenImage = post.imageUrl }
                                        )
                                    }
                                }
                            }
                        }
                        1 -> {
                            if (messagesState is MessagesUiState.Success && messagesState.selectedUserId != null) MessagesScreen(viewModel = messagesViewModel, onNavigateBack = { messagesViewModel.selectUser(null) })
                            else UserListScreen(viewModel = messagesViewModel, onUserSelected = { userId -> messagesViewModel.selectUser(userId) })
                        }
                        2 -> { val vm: ChallengesViewModel = viewModel(); ChallengesScreen(viewModel = vm, navController = navController) }
                        3 -> {
                            val vm: GymViewModel = viewModel(factory = GymViewModelFactory(application = LocalContext.current.applicationContext as Application))
                            GymScreen(viewModel = vm)
                        }                        4 -> {
                            val vm: NotificationsViewModel = viewModel()
                            NotificationsScreen(viewModel = vm, onNavigateToSource = { tipo -> when (tipo) { "feed" -> { selectedTab = 0; homeViewModel.onSearchToggle(false) } "messages" -> { selectedTab = 1 } "dynamics" -> { selectedTab = 2 } "gym" -> { selectedTab = 3 } } })
                        }
                    }

                    AnimatedVisibility(visible = state.isSearching, enter = fadeIn(), exit = fadeOut()) {
                        Column(modifier = Modifier.fillMaxSize().background(backgroundColor).clickable(enabled = false) {}) {
                            Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(value = state.searchQuery, onValueChange = { homeViewModel.onSearchQueryChanged(it) }, placeholder = { Text(stringResource(R.string.search_placeholder), color = Color.Gray) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) }, trailingIcon = { IconButton(onClick = { homeViewModel.onSearchToggle(false) }) { Icon(Icons.Default.Clear, "Cerrar", tint = Color.Gray) } }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = brandBlue, unfocusedBorderColor = Color(0xFFE0E0E0), focusedContainerColor = Color(0xFFF4F6F9), unfocusedContainerColor = Color(0xFFF4F6F9)), singleLine = true)
                            }
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                if (state.searchQuery.isEmpty()) {
                                    Text(stringResource(R.string.trending_categories), fontWeight = FontWeight.Bold, color = textDark, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                                    LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        items(listOf("Arte Anime", "Acuarelas", "Gore y Oscuro", "Realismo 3D", "Concepto Digital", "Bocetos")) { tag ->
                                            Box(modifier = Modifier.fillMaxWidth().height(70.dp).clip(RoundedCornerShape(14.dp)).background(brandBlue.copy(alpha = 0.08f)).clickable { homeViewModel.onSearchQueryChanged(tag.split(" ")[0]) }.padding(14.dp), contentAlignment = Alignment.CenterStart) { Text(tag, fontWeight = FontWeight.SemiBold, color = brandBlue, fontSize = 14.sp) }
                                        }
                                    }
                                } else {
                                    Text(stringResource(R.string.search_results, state.searchQuery), fontWeight = FontWeight.Bold, color = textDark, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        items(state.artworks) { post ->
                                            ArtworkCard(post = post, brandBlue = brandBlue, textDark = textDark, onLikeToggle = { homeViewModel.onLikeToggled(post.id) }, onCommentsClick = { homeViewModel.onActivePostForComments(post) }, onShareClick = {}, onDeleteClick = { homeViewModel.onDeletePost(post.id) }, onEditClick = { currentDescription -> postBeingEdited = post; editDescriptionText = currentDescription }, onImageClick = { fullScreenImage = post.imageUrl })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (showUploadBottomSheet) {
                        ModalBottomSheet(onDismissRequest = { showUploadBottomSheet = false }) {
                            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.upload_title), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showUploadBottomSheet = false; permissionLauncher.launch(Manifest.permission.CAMERA) }) { Box(modifier = Modifier.size(56.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.PhotoCamera, null, tint = brandBlue) }; Spacer(modifier = Modifier.height(8.dp)); Text(stringResource(R.string.camera), fontSize = 13.sp) }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showUploadBottomSheet = false; galleryLauncher.launch("image/*") }) { Box(modifier = Modifier.size(56.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Collections, null, tint = brandBlue) }; Spacer(modifier = Modifier.height(8.dp)); Text(stringResource(R.string.gallery), fontSize = 13.sp) }
                                }
                            }
                        }
                    }
                    // 🆕 FORMULARIO DE NUEVA PUBLICACIÓN
                    if (showFormDialog) {
                        AlertDialog(
                            onDismissRequest = { /* NO se cierra al tocar fuera */ },
                            title = { Text("Nueva publicación", fontWeight = FontWeight.Bold) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = newPostDescription,
                                        onValueChange = { newPostDescription = it },
                                        label = { Text("Descripción") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text("Categoría", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        categories.filter { it != "Todos" }.forEach { cat ->
                                            FilterChip(selected = newPostCategory == cat, onClick = { newPostCategory = cat }, label = { Text(cat) })
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    if (newPostDescription.isNotBlank() && selectedImageUri != null) {
                                        val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                                        val bytes = inputStream?.readBytes()
                                        val base64 = android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                                        homeViewModel.onCreatePost("Nueva publicación", newPostDescription, newPostCategory, base64)
                                        newPostDescription = ""
                                        selectedImageUri = null
                                        showFormDialog = false
                                    }
                                }, colors = ButtonDefaults.buttonColors(containerColor = brandBlue)) {
                                    Text("Compartir", color = Color.White)
                                }
                            },
                            dismissButton = { TextButton(onClick = { showFormDialog = false }) { Text("Cancelar") } }
                        )
                    }

                    // 🆕 PANEL DE COMENTARIOS MEJORADO
                    state.activePostForComments?.let { post ->
                        var commentText by remember { mutableStateOf("") }
                        LaunchedEffect(post.id) { homeViewModel.loadComments(post.id) }

                        ModalBottomSheet(
                            onDismissRequest = { homeViewModel.onActivePostForComments(null) },
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp).imePadding()) {
                                Text("Comentarios", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = textDark, modifier = Modifier.padding(bottom = 12.dp))

                                state.commentError?.let { error ->
                                    Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                                }

                                if (state.isCommentsLoading) { LinearProgressIndicator(color = brandBlue, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) }

                                LazyColumn(modifier = Modifier.weight(1f, fill = false).heightIn(max = 300.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(state.comments) { comment ->
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Box(modifier = Modifier.size(36.dp).background(brandBlue.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) { Text(comment.authorName.take(1).uppercase(), color = brandBlue, fontWeight = FontWeight.Bold) }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(comment.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textDark)
                                                Text(comment.text, fontSize = 14.sp, color = Color.Black, lineHeight = 18.sp)
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(value = commentText, onValueChange = { commentText = it }, placeholder = { Text("Escribe un comentario...") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.Black, unfocusedTextColor = Color.Black))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(onClick = { if (commentText.isNotBlank()) { homeViewModel.postComment(post.id, commentText); commentText = "" } }, enabled = !state.isCommentsLoading) { Icon(Icons.AutoMirrored.Filled.Send, "Enviar", tint = brandBlue) }
                                }
                            }
                        }
                    }

                    fullScreenImage?.let { imageUrl ->
                        Dialog(onDismissRequest = { fullScreenImage = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
                            Box(modifier = Modifier.fillMaxSize().background(Color.Black).clickable { fullScreenImage = null }, contentAlignment = Alignment.Center) {
                                AsyncImage(model = imageUrl, contentDescription = null, modifier = Modifier.fillMaxWidth(), contentScale = ContentScale.Fit)
                            }
                        }
                    }
                }
            }
        }
    }
