package com.irozumi.features.profile.presentation.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.R // Cambiado para que apunte a la raíz de tus recursos

@Composable
fun SocialPostCard(
    authorName: String,
    username: String,
    category: String,
    title: String,
    description: String,
    likesCount: String,
    commentsCount: String,
    modifier: Modifier = Modifier
) {
    val brandBlue = Color(0xFF2F80ED)
    val textDark = Color(0xFF2B2D42)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(1.dp, brandBlue, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mi_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = authorName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textDark)
                    Text(text = "$username • $category", fontSize = 11.sp, color = brandBlue, fontWeight = FontWeight.SemiBold)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .background(Color(0xFFE0E0E0))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mi_logo),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Like", tint = textDark)
                }
                Text(text = likesCount, fontSize = 13.sp, color = textDark, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = textDark)
                }
                Text(text = commentsCount, fontSize = 13.sp, color = textDark, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = brandBlue)
                }
            }

            Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
                Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = textDark)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 17.sp)
            }
        }
    }
}