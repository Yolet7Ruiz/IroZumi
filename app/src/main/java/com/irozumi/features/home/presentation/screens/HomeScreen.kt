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
import com.irozumi.R
import com.irozumi.features.home.domain.model.ArtworkPost
import com.irozumi.features.home.presentation.components.ArtworkCard
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

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("SpellCheckingInspection")
@Composable
fun HomeScreen(
    navController: NavHostController,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf("Todos") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var showUploadBottomSheet by remember { mutableStateOf(false) }
    var showFormDialog by remember { mutableStateOf(false) }
    var activePostForComments by remember { mutableStateOf<ArtworkPost?>(null) }
    var newPostDescription by remember { mutableStateOf("") }
    var newPostCategory by remember { mutableStateOf("Anime") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var postBeingEdited by remember { mutableStateOf<ArtworkPost?>(null) }
    var editDescriptionText by remember { mutableStateOf("") }

    val brandBlue = Color(0xFF2F80ED)
    val textDark = Color(0xFF3D405B)
    val backgroundColor = Color(0xFFF4F6F9)
    val categories = listOf("Todos", "Anime", "Acuarela", "Gore", "Realismo", "Digital")

    val currentUserName = "Yolet Ruiz"

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) { selectedImageUri = uri; showFormDialog = true }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) showFormDialog = true
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) { cameraLauncher.launch(null) }
    }

    val feedState = remember {
        mutableStateListOf(
            ArtworkPost(1, "Oaxaca Sunset", "Leronezo Junes", "", "Acuarela", "Cotton paper artwork 30x40cm", 100, 100, 24),
            ArtworkPost(2, "First Anime Sketch", "Chupete", "", "Anime", "Pencil 2B portrait sketch practice", 45, 12, 100)
        )
    }

    val messagesViewModel: MessagesViewModel = viewModel()
    val messagesState = messagesViewModel.uiState

    val isInsideIndividualChat = selectedTab == 1 &&
            messagesState is MessagesUiState.Success &&
            messagesState.selectedUserId != null

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch { drawerState.close() }
                                onNavigateToProfile()
                            }
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mi_logo),
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, brandBlue, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUserName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2B2D42)
                            )
                            Text(
                                text = "Mi cuenta",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(color = backgroundColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(6.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray) },
                        label = { Text("Configuración y privacidad", color = Color.DarkGray, fontSize = 14.sp) },
                        selected = false,
                        onClick = { },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray) },
                        label = { Text("Ayuda y soporte técnico", color = Color.DarkGray, fontSize = 14.sp) },
                        selected = false,
                        onClick = { },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Gray) },
                        label = { Text("Salir de la cuenta", color = Color.DarkGray, fontSize = 14.sp) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("welcome") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                    )

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
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = null, tint = textDark)
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearching = true }) { Icon(Icons.Default.Search, contentDescription = null, tint = textDark) }
                            IconButton(onClick = onNavigateToCart) { Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = textDark) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                }
            },
            bottomBar = {
                if (!isInsideIndividualChat) {
                    NavigationBar(
                        containerColor = Color.White,
                        tonalElevation = 8.dp,
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier.height(54.dp)
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(22.dp)) },
                            label = { Text(stringResource(R.string.tab_home), fontSize = 10.sp) },
                            selected = selectedTab == 0 && !isSearching,
                            onClick = { selectedTab = 0; isSearching = false }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(22.dp)) },
                            label = { Text(stringResource(R.string.tab_messages), fontSize = 10.sp) },
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(22.dp)) },
                            label = { Text(stringResource(R.string.tab_dynamics), fontSize = 10.sp) },
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(22.dp)) },
                            label = { Text(stringResource(R.string.tab_gym), fontSize = 10.sp) },
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(22.dp)) },
                            label = { Text(stringResource(R.string.tab_notifications), fontSize = 10.sp) },
                            selected = selectedTab == 4,
                            onClick = { selectedTab = 4 }
                        )
                    }
                }
            },
            containerColor = backgroundColor
        ) { innerPadding ->

            val customModifier = if (isInsideIndividualChat) {
                Modifier.fillMaxSize()
            } else {
                Modifier.fillMaxSize().padding(innerPadding)
            }

            Box(modifier = customModifier) {
                when (selectedTab) {
                    0 -> {
                        Column {
                            // 🎨 SECCIÓN: ARTISTAS DESTACADOS
                            Column(modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)) {
                                Text(
                                    text = "Artistas Destacados",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = textDark,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(horizontal = 14.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    val creadoresEjemplo = listOf("Leronezo", "Chupete", "Gael_Art", "KeniaR", "Mora_99")
                                    creadoresEjemplo.forEach { creador ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable {
                                                navController.navigate("profile/$creador")
                                            }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .background(brandBlue.copy(alpha = 0.08f), CircleShape)
                                                    .border(1.5.dp, brandBlue, CircleShape)
                                                    .padding(3.dp)
                                            ) {
                                                Box(modifier = Modifier.fillMaxSize().background(Color.LightGray, CircleShape))
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = creador, fontSize = 11.sp, color = textDark, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))

                            // Categorías de filtro
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { category ->
                                    val isSelected = selectedCategory == category
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedCategory = category },
                                        label = { Text(category) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = brandBlue, selectedLabelColor = Color.White),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                }
                            }

                            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).clickable { showUploadBottomSheet = true },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(40.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Add, contentDescription = null, tint = brandBlue)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(stringResource(R.string.explore_placeholder), color = Color.Gray, fontSize = 14.sp)
                                        }
                                    }
                                }

                                val filteredPosts = feedState.filter { selectedCategory == "Todos" || it.category == selectedCategory }
                                items(filteredPosts) { post ->
                                    Box(modifier = Modifier.clickable {
                                        navController.navigate("profile/${post.author}")
                                    }) {
                                        ArtworkCard(
                                            post = post, brandBlue = brandBlue, textDark = textDark,
                                            onLikeToggle = {
                                                val index = feedState.indexOf(post)
                                                if (index != -1) {
                                                    val currentLikes = feedState[index].likesCount
                                                    feedState[index] = feedState[index].copy(likesCount = currentLikes + 1)
                                                }
                                            },
                                            onCommentsClick = { activePostForComments = post },
                                            onShareClick = {
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "Check out this art by ${post.author}: '${post.title}'")
                                                    type = "text/plain"
                                                }
                                                context.startActivity(Intent.createChooser(sendIntent, "Share via:"))
                                            },
                                            onDeleteClick = { feedState.remove(post) },
                                            onEditClick = { currentDescription ->
                                                postBeingEdited = post
                                                editDescriptionText = currentDescription
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        if (messagesState is MessagesUiState.Success && messagesState.selectedUserId != null) {
                            MessagesScreen(
                                viewModel = messagesViewModel,
                                onNavigateBack = { messagesViewModel.selectUser(null) }
                            )
                        } else {
                            UserListScreen(
                                viewModel = messagesViewModel,
                                onUserSelected = { userId -> messagesViewModel.selectUser(userId) }
                            )
                        }
                    }
                    2 -> {
                        val challengesViewModel: ChallengesViewModel = viewModel()
                        ChallengesScreen(viewModel = challengesViewModel, navController = navController)
                    }
                    3 -> {
                        val gymViewModel: GymViewModel = viewModel(factory = GymViewModelFactory())
                        GymScreen(viewModel = gymViewModel)
                    }
                    4 -> {
                        val notificationsViewModel: NotificationsViewModel = viewModel()
                        NotificationsScreen(
                            viewModel = notificationsViewModel,
                            onNavigateToSource = { tipoOrigen ->
                                when (tipoOrigen) {
                                    "feed" -> { selectedTab = 0; isSearching = false }
                                    "messages" -> { selectedTab = 1 }
                                    "dynamics" -> { selectedTab = 2 }
                                    "gym" -> { selectedTab = 3 }
                                }
                            }
                        )
                    }
                }

                AnimatedVisibility(visible = isSearching, enter = fadeIn(), exit = fadeOut()) {
                    Column(modifier = Modifier.fillMaxSize().background(backgroundColor).clickable(enabled = false) {}) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text(stringResource(R.string.search_placeholder), color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(28.dp),
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isSearching = false
                                        searchQuery = ""
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Cerrar buscador", tint = Color.Gray)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = brandBlue,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color(0xFFF4F6F9),
                                    unfocusedContainerColor = Color(0xFFF4F6F9)
                                ),
                                singleLine = true
                            )
                        }

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            if (searchQuery.isEmpty()) {
                                Text(stringResource(R.string.trending_categories), fontWeight = FontWeight.Bold, color = textDark, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
                                LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(listOf("Arte Anime", "Acuarelas", "Gore y Oscuro", "Realismo 3D", "Concepto Digital", "Bocetos")) { tag ->
                                        Box(modifier = Modifier.fillMaxWidth().height(70.dp).clip(RoundedCornerShape(14.dp)).background(brandBlue.copy(alpha = 0.08f)).clickable { searchQuery = tag.split(" ")[0] }.padding(14.dp), contentAlignment = Alignment.CenterStart) {
                                            Text(tag, fontWeight = FontWeight.SemiBold, color = brandBlue, fontSize = 14.sp)
                                        }
                                    }
                                }
                            } else {
                                Text(stringResource(R.string.search_results, searchQuery), fontWeight = FontWeight.Bold, color = textDark, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))
                                val filteredSearch = feedState.filter {
                                    it.title.contains(searchQuery, ignoreCase = true) || it.author.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
                                }
                                if (filteredSearch.isEmpty()) {
                                    Text(stringResource(R.string.search_no_match), color = Color.Gray, fontSize = 14.sp)
                                } else {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        items(filteredSearch) { post ->
                                            Box(modifier = Modifier.clickable { navController.navigate("profile/${post.author}") }) {
                                                ArtworkCard(
                                                    post = post, brandBlue = brandBlue, textDark = textDark,
                                                    onLikeToggle = {}, onCommentsClick = { activePostForComments = post }, onShareClick = {},
                                                    onDeleteClick = { feedState.remove(post) },
                                                    onEditClick = { currentDescription ->
                                                        postBeingEdited = post; editDescriptionText = currentDescription
                                                    }
                                                )
                                            }
                                        }
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
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showUploadBottomSheet = false; permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                                    Box(modifier = Modifier.size(56.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = brandBlue) }
                                    Spacer(modifier = Modifier.height(8.dp)); Text(stringResource(R.string.camera), fontSize = 13.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { showUploadBottomSheet = false; galleryLauncher.launch("image/*") }) {
                                    Box(modifier = Modifier.size(56.dp).background(brandBlue.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Collections, contentDescription = null, tint = brandBlue) }
                                    Spacer(modifier = Modifier.height(8.dp)); Text(stringResource(R.string.gallery), fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                if (showFormDialog) {
                    AlertDialog(
                        onDismissRequest = { showFormDialog = false },
                        title = { Text(stringResource(R.string.dialog_post_details), fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(value = newPostDescription, onValueChange = { newPostDescription = it }, label = { Text(stringResource(R.string.dialog_comment_label)) }, modifier = Modifier.fillMaxWidth())
                                Text(stringResource(R.string.dialog_category_label), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    categories.filter { it != "Todos" }.forEach { cat ->
                                        FilterChip(selected = newPostCategory == cat, onClick = { newPostCategory = cat }, label = { Text(cat) })
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (newPostDescription.isNotBlank()) {
                                        feedState.add(0, ArtworkPost(feedState.size + 1, "New upload", "You (Artist)", selectedImageUri?.toString() ?: "", newPostCategory, newPostDescription, 0, 0, 0))
                                        newPostDescription = ""
                                        showFormDialog = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                            ) { Text(stringResource(R.string.btn_share), color = Color.White) }
                        },
                        dismissButton = { TextButton(onClick = { showFormDialog = false }) { Text(stringResource(R.string.btn_cancel)) } }
                    )
                }

                postBeingEdited?.let { post ->
                    AlertDialog(
                        onDismissRequest = { postBeingEdited = null },
                        title = { Text(stringResource(R.string.dialog_edit_title), fontWeight = FontWeight.Bold) },
                        text = {
                            Column {
                                OutlinedTextField(value = editDescriptionText, onValueChange = { editDescriptionText = it }, label = { Text(stringResource(R.string.dialog_update_desc)) }, modifier = Modifier.fillMaxWidth())
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val index = feedState.indexOf(post)
                                    if (index != -1 && editDescriptionText.isNotBlank()) {
                                        feedState[index] = feedState[index].copy(description = editDescriptionText)
                                    }
                                    postBeingEdited = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                            ) { Text("Actualizar") }
                        },
                        dismissButton = { TextButton(onClick = { postBeingEdited = null }) { Text("Cancelar") } }
                    )
                }

                activePostForComments?.let { post ->
                    val comentariosLocales = remember(post) {
                        mutableStateListOf(
                            Pair("UsuarioEjemplo", "¡Qué increíble obra de arte! Me encanta el estilo.")
                        )
                    }
                    var nuevoComentarioText by remember { mutableStateOf("") }

                    ModalBottomSheet(
                        onDismissRequest = { activePostForComments = null }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .padding(bottom = 24.dp)
                        ) {
                            Text(
                                text = "Comentarios - ${post.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = textDark,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .heightIn(max = 250.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(comentariosLocales) { (usuario, texto) ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF0F2F5), RoundedCornerShape(12.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(text = usuario, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = brandBlue)
                                        Text(text = texto, fontSize = 13.sp, color = textDark)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = nuevoComentarioText,
                                    onValueChange = { nuevoComentarioText = it },
                                    placeholder = { Text("Escribe un comentario...") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                IconButton(
                                    onClick = {
                                        if (nuevoComentarioText.isNotBlank()) {
                                            comentariosLocales.add(Pair(currentUserName, nuevoComentarioText))
                                            nuevoComentarioText = ""
                                        }
                                    },
                                    modifier = Modifier.background(brandBlue, CircleShape)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}