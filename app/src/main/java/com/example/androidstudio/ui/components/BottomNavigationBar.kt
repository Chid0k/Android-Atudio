package com.example.androidstudio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onKnowledgeClick: () -> Unit,
    onProfileClick: () -> Unit,
    selectedItem: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
        color = Color.White,
        tonalElevation = 16.dp,
        shadowElevation = 20.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .height(72.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(
                icon = Icons.Rounded.Home,
                label = "Trang chủ",
                isSelected = selectedItem == 0,
                onClick = onHomeClick
            )
            NavigationItem(
                icon = Icons.Rounded.History,
                label = "Lịch sử",
                isSelected = selectedItem == 1,
                onClick = onHistoryClick
            )
            
            // Central Spacer for FAB if needed, but keeping it balanced
            Spacer(modifier = Modifier.width(48.dp))

            NavigationItem(
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                label = "Góc học tập",
                isSelected = selectedItem == 2,
                onClick = onKnowledgeClick
            )
            NavigationItem(
                icon = Icons.Rounded.Person,
                label = "Hồ sơ",
                isSelected = selectedItem == 3,
                onClick = onProfileClick
            )
        }
    }
}

@Composable
fun RowScope.NavigationItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF0D3B34)
    val inactiveColor = Color.Gray

    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color(0xFFF1F8E9) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) primaryColor else inactiveColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            if (isSelected) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = primaryColor,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
