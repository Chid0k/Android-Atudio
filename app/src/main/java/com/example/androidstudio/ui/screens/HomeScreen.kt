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
import com.example.androidstudio.network.RetrofitClient
import com.example.androidstudio.network.UserProfile
import com.example.androidstudio.network.SessionHistoryManager
import com.example.androidstudio.network.InterviewSessionResponse
import com.example.androidstudio.ui.components.BottomNavigationBar
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userId: Int?,
    onNavigateToProfile: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToKnowledge: () -> Unit,
    onNavigateToResult: (Int) -> Unit
) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var sessions by remember { mutableStateOf(SessionHistoryManager.sessions) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            scope.launch {
                try {
                    userProfile = RetrofitClient.apiService.getUserProfile(userId)
                    val fetchedSessions = RetrofitClient.apiService.getInterviewSessions(userId)
                    SessionHistoryManager.sessions = fetchedSessions
                    sessions = fetchedSessions
                } catch (e: Exception) {
                    // Silent fail or handle error if needed
                }
            }
        }
    }

    // Calculate statistics
    val totalSessions = sessions.size
    val totalDurationMinutes = sessions.sumOf { session ->
        if (session.startTime != null && session.endTime != null) {
            try {
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val start = LocalDateTime.parse(session.startTime, formatter)
                val end = LocalDateTime.parse(session.endTime, formatter)
                Duration.between(start, end).toMinutes().toInt()
            } catch (e: Exception) {
                0
            }
        } else {
            0
        }
    }

    val hours = totalDurationMinutes / 60
    val minutes = totalDurationMinutes % 60
    val durationText = if (hours > 0) "${hours}h ${minutes}p" else "${minutes}p"

    val recentSessions = sessions.take(2)

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
                HomeHeader(userProfile, onNavigateToProfile)
                Spacer(modifier = Modifier.height(24.dp))
                StatCards(durationText, totalSessions.toString())
                Spacer(modifier = Modifier.height(24.dp))
                StartInterviewBanner(onNavigateToInterviewSetup)
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader(title = "Gần đây", onSeeAllClick = onNavigateToHistory)
            }
            
            items(recentSessions) { session ->
                InterviewHistoryItem(
                    session = session,
                    onClick = { onNavigateToResult(session.sessionId) }
                )
            }
        }
    }
}

@Composable
fun HomeHeader(userProfile: UserProfile?, onProfileClick: () -> Unit) {
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
                .background(Color(0xFFE8F5E9))
                .clickable { onProfileClick() }
        ) {
            Icon(
                Icons.Default.Person, 
                contentDescription = "Profile",
                modifier = Modifier.align(Alignment.Center),
                tint = Color(0xFF0D3B34)
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = userProfile?.full_name ?: "Người dùng", 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp,
                color = Color(0xFF2C3E50)
            )
            Text(
                text = userProfile?.major ?: "Chưa cập nhật hồ sơ", 
                color = Color.Gray, 
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun StatCards(duration: String, sessionsCount: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timer,
            label = "Đã luyện",
            value = duration
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Assignment,
            label = "Buổi tập",
            value = sessionsCount
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
fun InterviewHistoryItem(session: InterviewSessionResponse, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.Blue.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = session.title.take(1),
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(text = session.title, fontWeight = FontWeight.Medium)
            val dateText = try {
                val formatter = DateTimeFormatter.ISO_DATE_TIME
                val ldt = LocalDateTime.parse(session.startTime ?: "", formatter)
                ldt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            } catch (e: Exception) {
                session.startTime ?: "N/A"
            }
            Text(text = dateText, color = Color.Gray, fontSize = 12.sp)
        }
        
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
