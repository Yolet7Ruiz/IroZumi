package com.irozumi.features.home.presentation.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.irozumi.R
import com.irozumi.features.home.domain.model.ArtworkPost
import com.irozumi.features.home.presentation.components.ArtworkCard
import com.irozumi.features.gym.presentation.screens.GymScreen
import com.irozumi.features.gym.di.GymViewModelFactory
import com.irozumi.features.gym.presentation.viewmodel.GymViewModel
import com.irozumi.features.challenges.presentation.screens.ChallengesScreen // 💡 IMPORTACIÓN DE DINÁMICAS
import com.irozumi.features.challenges.presentation.viewmodel.ChallengesViewModel // 💡 IMPORTACIÓN DE DINÁMICAS
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(modifier = Modifier.fillMaxHeight().padding(bottom = 16.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(stringResource(R.string.app_name), modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Black, fontSize = 22.sp, color = brandBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        label = { Text(stringResource(R.string.drawer_profile)) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigateToProfile()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
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
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_home), fontSize = 11.sp) },
                        selected = selectedTab == 0 && !isSearching,
                        onClick = { selectedTab = 0; isSearching = false }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Mail, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_messages), fontSize = 11.sp) },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_dynamics), fontSize = 11.sp) },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_gym), fontSize = 11.sp) },
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_notifications), fontSize = 11.sp) },
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 }
                    )
                }
            },
            containerColor = backgroundColor
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

                when (selectedTab) {
                    0 -> {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 10.dp),
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
                                    ArtworkCard(
                                        post = post, brandBlue = brandBlue, textDark = textDark,
                                        onLikeToggle = {
                                            val index = feedState.indexOfFirst { it.id == post.id }
                                            if (index != -1) feedState[index] = feedState[index].copy(likesCount = feedState[index].likesCount + 1)
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
                    2 -> {
                        // 💡 CORREGIDO: Inyección e inicialización impecable de tu pantalla de dinámicas
                        val challengesViewModel: ChallengesViewModel = viewModel()
                        ChallengesScreen(viewModel = challengesViewModel)
                    }
                    3 -> {
                        val gymViewModel: GymViewModel = viewModel(factory = GymViewModelFactory())
                        GymScreen(viewModel = gymViewModel)
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.under_development), color = Color.Gray)
                        }
                    }
                }

                // BUSCADOR INMERSIVO
                AnimatedVisibility(visible = isSearching, enter = fadeIn(), exit = fadeOut()) {
                    Column(modifier = Modifier.fillMaxSize().background(backgroundColor).clickable(enabled = false) {}) {
                        Row(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { isSearching = false; searchQuery = "" }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = textDark)
                            }
                            OutlinedTextField(
                                value = searchQuery, onValueChange = { searchQuery = it },
                                placeholder = { Text(stringResource(R.string.search_placeholder), color = Color.Gray) },
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                                shape = RoundedCornerShape(28.dp),
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Gray) }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = brandBlue, unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = Color(0xFFF8F9FA), unfocusedContainerColor = Color(0xFFF8F9FA)
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

                // BOTTOM SHEETS & DIALOGS
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
                                        newPostDescription = ""; showFormDialog = false
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
                                    val index = feedState.indexOfFirst { it.id == post.id }
                                    if (index != -1) feedState[index] = feedState[index].copy(description = editDescriptionText)
                                    postBeingEdited = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                            ) { Text(stringResource(R.string.btn_save), color = Color.White) }
                        },
                        dismissButton = { TextButton(onClick = { postBeingEdited = null }) { Text(stringResource(R.string.btn_cancel)) } }
                    )
                }

                activePostForComments?.let { post ->
                    var commentText by remember { mutableStateOf("") }
                    val mockComments = remember(post.id) { mutableStateListOf("Amazing style!", "Great piece composition") }

                    ModalBottomSheet(onDismissRequest = { activePostForComments = null }, modifier = Modifier.fillMaxHeight(0.85f), containerColor = Color(0xFF1E202C)) {
                        Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.ime)) {
                            Column(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
                                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                                    Text(text = stringResource(R.string.comments_title, mockComments.size), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        items(mockComments) { comment ->
                                            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                                                Box(modifier = Modifier.size(36.dp).background(Color(0xFFE0E0E0), CircleShape))
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text("@user_iroZumi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(comment, fontSize = 14.sp, color = Color.LightGray)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color(0xFF1E202C)).padding(horizontal = 16.dp, vertical = 12.dp).navigationBarsPadding()) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = commentText, onValueChange = { commentText = it },
                                        placeholder = { Text(stringResource(R.string.comment_reply_placeholder), fontSize = 14.sp, color = Color.Gray) },
                                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = brandBlue, unfocusedBorderColor = Color.DarkGray,
                                            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                            focusedContainerColor = Color(0xFF151722), unfocusedContainerColor = Color(0xFF151722)
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    IconButton(
                                        onClick = {
                                            if (commentText.isNotBlank()) {
                                                mockComments.add(commentText)
                                                val index = feedState.indexOfFirst { it.id == post.id }
                                                if (index != -1) feedState[index] = feedState[index].copy(comments = feedState[index].comments + 1)
                                                commentText = ""
                                            }
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(containerColor = brandBlue), modifier = Modifier.size(44.dp)
                                    ) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}