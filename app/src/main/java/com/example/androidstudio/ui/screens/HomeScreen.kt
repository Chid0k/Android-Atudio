package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToKnowledge: () -> Unit
) {
    Scaffold(
        bottomBar = { 
            BottomNavigationBar(
                onHomeClick = {},
                onHistoryClick = onNavigateToHistory,
                onKnowledgeClick = onNavigateToKnowledge,
                onProfileClick = onNavigateToProfile,
                selectedItem = 0
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                HomeHeader(onNavigateToProfile)
                Spacer(modifier = Modifier.height(24.dp))
                StatCards()
                Spacer(modifier = Modifier.height(24.dp))
                StartInterviewBanner(onNavigateToInterviewSetup)
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Gần đây", onSeeAllClick = onNavigateToHistory)
            }
            
            items(recentInterviews) { interview ->
                InterviewHistoryItem(interview)
            }
        }
    }
}

@Composable
fun HomeHeader(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable { onProfileClick() }
        ) {
            Icon(
                Icons.Default.Person, 
                contentDescription = "Profile",
                modifier = Modifier.align(Alignment.Center)
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Chính Đỗ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Pen tester", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatCards() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timer,
            label = "Đã luyện",
            value = "12h 30p"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Assignment,
            label = "Buổi tập",
            value = "24"
        )
    }
}

@Composable
fun StatCard(modifier: Modifier, icon: ImageVector, label: String, value: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0E3C3E), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StartInterviewBanner(onStartClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0D3B34), Color(0xFF1B5E55))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    "Bắt đầu phỏng vấn mới",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "Tùy chỉnh thiết lập để mở những buổi phỏng vấn thực tế nhất.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCFF90)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Luyện tập ngay", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        TextButton(onClick = onSeeAllClick) {
            Text("Xem tất cả", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun InterviewHistoryItem(interview: InterviewHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = interview.color.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = interview.title.take(1),
                    color = interview.color,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = interview.title, fontWeight = FontWeight.Medium)
            Text(text = interview.date, color = Color.Gray, fontSize = 12.sp)
        }
        
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}

data class InterviewHistory(val title: String, val date: String, val color: Color)

val recentInterviews = listOf(
    InterviewHistory("Product Manager", "Vừa xong • 45 phút • 8.5/10", Color.Blue),
    InterviewHistory("Frontend Dev", "2 ngày trước • 30 phút • 7.0/10", Color.Magenta)
)
