package com.example.androidstudio.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 12.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            selected = selectedItem == 0,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0D3B34),
                indicatorColor = Color(0xFFF1F8E9)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.BarChart, contentDescription = "Stats") },
            label = { Text("Lịch sử", fontSize = 10.sp) },
            selected = selectedItem == 1,
            onClick = onHistoryClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0D3B34),
                indicatorColor = Color(0xFFF1F8E9)
            )
        )
        Spacer(modifier = Modifier.weight(0.4f))
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Rounded.MenuBook, contentDescription = "Learn") },
            label = { Text("Học", fontSize = 10.sp) },
            selected = selectedItem == 2,
            onClick = onKnowledgeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0D3B34),
                indicatorColor = Color(0xFFF1F8E9)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Person, contentDescription = "Profile") },
            label = { Text("Hồ sơ", fontSize = 10.sp) },
            selected = selectedItem == 3,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0D3B34),
                indicatorColor = Color(0xFFF1F8E9)
            )
        )
    }
}
