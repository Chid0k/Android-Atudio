package com.example.androidstudio.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToKnowledge: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToResult: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    val primaryColor = Color(0xFF0D3B34)
    val backgroundColor = Color(0xFFF8FAF9)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = onNavigateToHome,
                onHistoryClick = { },
                onKnowledgeClick = onNavigateToKnowledge,
                onProfileClick = onNavigateToProfile,
                selectedItem = 1
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToInterviewSetup,
                containerColor = Color(0xFFCCFF90),
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.offset(y = 50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Lịch sử luyện tập",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor
                )
                Text(
                    text = "Nâng cao kỹ năng phỏng vấn mỗi ngày",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Search Bar
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Tìm kiếm lịch sử...", fontSize = 14.sp, color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(24.dp),
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = primaryColor) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = primaryColor
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Tab Switcher (Modern style like KnowledgeScreen)
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0E0E0).copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                ModernTabItem(
                    text = "Danh sách",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                ModernTabItem(
                    text = "Thống kê",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTab == 0) {
                HistoryContent(onNavigateToResult)
            } else {
                StatisticsContent()
            }
        }
    }
}

@Composable
fun HistoryContent(onNavigateToResult: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FeaturedHistoryCard(
                title = "Buổi tập gần nhất",
                desc = "Senior UX Designer - Điểm: 8.5",
                icon = Icons.Rounded.History,
                onClick = { onNavigateToResult("1") }
            )
        }
        
        item {
            Text(
                "Tất cả lịch sử",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0D3B34),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(historyItems) { item ->
            EnhancedHistoryItem(item, onClick = { onNavigateToResult(item.id) })
        }
    }
}

@Composable
fun FeaturedHistoryCard(title: String, desc: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0D3B34), Color(0xFF1B5E20))
                    )
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth(0.7f)) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "LATEST",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(desc, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(80.dp).align(Alignment.TopEnd).offset(x = 10.dp, y = (-5).dp)
            )
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun EnhancedHistoryItem(item: HistoryItem, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.iconBg.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.iconColor, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(item.time, color = Color.Gray, fontSize = 13.sp)
            }
            Surface(
                color = Color(0xFFF1F8E9),
                shape = CircleShape
            ) {
                Text(
                    text = item.score,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun StatisticsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0D3B34)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Assessment, contentDescription = null, tint = Color(0xFFC8E6C9), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Tổng quan nỗ lực",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Bạn đã tiến bộ hơn 15% so với tuần trước!",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        
        Text("Chỉ số quan trọng", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0D3B34))
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(Modifier.weight(1f), "Tổng thời gian", "12.5 giờ", Icons.Rounded.Schedule, Color(0xFFE3F2FD))
            StatCard(Modifier.weight(1f), "Số buổi tập", "24 buổi", Icons.Rounded.CalendarToday, Color(0xFFFFF3E0))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(Modifier.weight(1f), "Điểm trung bình", "7.8", Icons.Rounded.Star, Color(0xFFF1F8E9))
            StatCard(Modifier.weight(1f), "Kỹ năng tốt nhất", "Giao tiếp", Icons.Rounded.RecordVoiceOver, Color(0xFFF3E5F5))
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String, icon: ImageVector, bgColor: Color) {
    Card(
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(bgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0D3B34), modifier = Modifier.size(20.dp))
            }
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D3B34))
                Text(title, fontSize = 12.sp, color = Color.Gray)
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
    HistoryItem("1", "Senior UX Designer", "Hôm nay, 15:30", "8.5", Icons.Default.BusinessCenter, Color(0xFFE8F5E9), Color(0xFF2E7D32)),
    HistoryItem("2", "Product Manager", "Hôm qua, 10:00", "7.0", Icons.Default.Person, Color(0xFFE3F2FD), Color(0xFF1976D2)),
    HistoryItem("3", "Frontend Developer", "20 Th10, 09:00", "7.5", Icons.Default.Code, Color(0xFFFFF3E0), Color(0xFFE65100)),
    HistoryItem("4", "Data Analyst", "18 Th10, 14:00", "7.8", Icons.Default.Search, Color(0xFFFFEBEE), Color(0xFFC62828))
)
