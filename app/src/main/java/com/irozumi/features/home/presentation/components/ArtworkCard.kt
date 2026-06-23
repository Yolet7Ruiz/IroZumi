package com.irozumi.features.home.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.irozumi.features.home.domain.model.ArtworkPost

@Composable
fun ArtworkCard(
    post: ArtworkPost,
    brandBlue: Color,
    textDark: Color,
    onCommentsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(44.dp).background(brandBlue.copy(alpha = 0.15f), CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.author, fontWeight = FontWeight.Bold, color = textDark, fontSize = 15.sp)
                    Text(text = post.category, color = brandBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                IconButton(onClick = { /* Opciones */ }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Opciones", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = post.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = textDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.description, fontSize = 13.sp, color = Color.DarkGray, maxLines = 3, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEFEFEF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${if (post.isLikedByUser) "❤️" else "🖤"} ${post.likesCount} me gusta", fontSize = 12.sp, color = Color.Gray)
                Text(text = "💬 ${post.comments} comentarios", fontSize = 12.sp, color = Color.Gray)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFF5F5F5))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Comisiones */ },
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Comisiones", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { /* Pedir similar */ },
                    modifier = Modifier.weight(1f).height(40.dp),
                    border = BorderStroke(1.dp, brandBlue.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Brush, contentDescription = null, modifier = Modifier.size(16.dp), tint = brandBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pedir similar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = brandBlue)
                }
            }
        }
    }
}