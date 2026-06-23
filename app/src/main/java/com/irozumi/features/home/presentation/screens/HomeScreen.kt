package com.irozumi.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.home.domain.model.ArtworkPost
import com.irozumi.features.home.presentation.components.ArtworkCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPostDetail: (postId: String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    val brandBlue = Color(0xFF2F80ED)
    val textDark = Color(0xFF3D405B)
    val backgroundColor = Color(0xFFF4F6F9)

    // Datos simulados (Temporal, luego los leerás de tu HomeState)
    val standardFeed = remember {
        listOf(
            ArtworkPost(1, "Atardecer en Oaxaca", "LERONEZO JUNES M..", null, "Acuarela", "Acuarela sobre papel algodón. 30x40cm", 100, 100, 24)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                Text("IroZumi", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Black, fontSize = 22.sp, color = brandBlue)
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    label = { Text("Mi Perfil de Artista") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.background(Color.White)) {
                    TopAppBar(
                        title = { Text("IroZumi", fontWeight = FontWeight.Black, fontSize = 24.sp, color = brandBlue) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú", tint = textDark)
                            }
                        },
                        actions = {
                            IconButton(onClick = { /* Buscar */ }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar", tint = textDark)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )

                    // Solución Material 3: PrimaryTabRow maneja automáticamente el indicador interno sin romper la firma
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = brandBlue
                    ) {
                        Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                BadgedBox(badge = { Badge(containerColor = Color.Red) { Text("15+") } }) {
                                    Icon(Icons.Default.Home, contentDescription = "Home", tint = if (selectedTab == 0) brandBlue else Color.Gray)
                                }
                            }
                        }
                        Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = "Retos", tint = if (selectedTab == 1) brandBlue else Color.Gray)
                            }
                        }
                        Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = "Gym", tint = if (selectedTab == 2) brandBlue else Color.Gray)
                            }
                        }
                        Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                BadgedBox(badge = { Badge(containerColor = Color.Red) { Text("8") } }) {
                                    Icon(Icons.Default.Mail, contentDescription = "Mensajes", tint = if (selectedTab == 3) brandBlue else Color.Gray)
                                }
                            }
                        }
                        Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }) {
                            Box(modifier = Modifier.padding(12.dp)) {
                                BadgedBox(badge = { Badge(containerColor = Color.Red) { Text("1") } }) {
                                    Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = if (selectedTab == 4) brandBlue else Color.Gray)
                                }
                            }
                        }
                    }
                }
            },
            containerColor = backgroundColor
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> FeedTabContent(posts = standardFeed, brandBlue = brandBlue, textDark = textDark, onPostClick = onNavigateToPostDetail)
                    1 -> TemporarySectionView("Retos de la Comunidad", "Desafía tus habilidades con temáticas semanales.")
                    2 -> TemporarySectionView("Gimnasio de Arte (Gym)", "Rutinas diarias guiadas para soltar la mano.")
                    3 -> TemporarySectionView("Mensajes Directos", "Conversaciones privadas con compradores interesados.")
                    4 -> TemporarySectionView("Notificaciones", "Entérate quién ha guardado tus obras.")
                }
            }
        }
    }
}

@Composable
fun FeedTabContent(
    posts: List<ArtworkPost>,
    brandBlue: Color,
    textDark: Color,
    onPostClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFE0E0E0), CircleShape))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Explora nuevas técnicas y publica tu proceso...", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

        items(posts) { post ->
            ArtworkCard(
                post = post,
                brandBlue = brandBlue,
                textDark = textDark,
                onCommentsClick = { onPostClick(post.id.toString()) }
            )
        }
    }
}

@Composable
fun TemporarySectionView(title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF3D405B))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = body, color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}