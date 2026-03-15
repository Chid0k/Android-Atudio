package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Danh sách", "Thống kê")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử luyện tập", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF0E3C3E),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF0E3C3E)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            if (selectedTab == 0) {
                HistoryList(onNavigateToResult)
            } else {
                StatisticsView()
            }
        }
    }
}

@Composable
fun HistoryList(onNavigateToResult: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = true, onClick = {}, label = { Text("Tất cả") })
            FilterChip(selected = false, onClick = {}, label = { Text("Video") })
            FilterChip(selected = false, onClick = {}, label = { Text("Voice") })
            FilterChip(selected = false, onClick = {}, label = { Text("Text") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(historyItems) { item ->
                HistoryItemRow(item, onClick = { onNavigateToResult(item.id) })
            }
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(item.iconBg, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.iconColor, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(item.time, color = Color.Gray, fontSize = 12.sp)
            }

            Surface(
                color = Color.White,
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Text(
                    text = item.score,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
fun StatisticsView() {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TỔNG THỜI GIAN", color = Color(0xFF7B1FA2), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("12.5 giờ", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("SỐ BUỔI TẬP", color = Color(0xFFE65100), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("24 buổi", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }
        }
    }
}

data class HistoryItem(
    val id: String,
    val title: String,
    val time: String,
    val score: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconColor: Color
)

val historyItems = listOf(
    HistoryItem("1", "Senior UX Designer", "15:30", "8.5", Icons.Default.BusinessCenter, Color(0xFFE8F5E9), Color(0xFF2E7D32)),
    HistoryItem("2", "Product Manager", "10:00", "7.0", Icons.Default.Person, Color(0xFFE3F2FD), Color(0xFF1976D2)),
    HistoryItem("3", "Frontend Developer", "09:00", "7.5", Icons.Default.Code, Color(0xFFFFF3E0), Color(0xFFE65100)),
    HistoryItem("4", "Data Analyst", "14:00", "7.8", Icons.Default.Search, Color(0xFFFFEBEE), Color(0xFFC62828))
)
