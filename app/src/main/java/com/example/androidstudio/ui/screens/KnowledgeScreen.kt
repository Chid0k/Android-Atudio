package com.example.androidstudio.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun KnowledgeScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToArticle: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    val primaryColor = Color(0xFF0D3B34)
    val backgroundColor = Color(0xFFF8FAF9)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = onNavigateToHome,
                onHistoryClick = onNavigateToHistory,
                onKnowledgeClick = { },
                onProfileClick = onNavigateToProfile,
                selectedItem = 2
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
                    text = "Góc học tập",
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
                    placeholder = { Text("Tìm kiếm bài viết, mẹo...", fontSize = 14.sp, color = Color.Gray) },
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

            // Custom Tab Switcher
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0E0E0).copy(alpha = 0.5f))
                    .padding(4.dp)
            ) {
                ModernTabItem(
                    text = "Kiến thức",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                ModernTabItem(
                    text = "Phục trang",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTab == 0) {
                KnowledgeContent(onNavigateToArticle)
            } else {
                ClothingContent()
            }
        }
    }
}

@Composable
fun ModernTabItem(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val containerColor by animateColorAsState(
        if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 300), label = ""
    )
    val contentColor by animateColorAsState(
        if (isSelected) Color(0xFF0D3B34) else Color.Gray,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = containerColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 10.dp)) {
            Text(
                text = text,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun KnowledgeContent(onNavigateToArticle: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FeaturedLearningCard(
                title = "Phương pháp STAR",
                desc = "Làm chủ kỹ thuật kể chuyện trong phỏng vấn",
                icon = Icons.Rounded.AutoAwesome,
                onClick = onNavigateToArticle
            )
        }
        
        item {
            Text(
                "Phổ biến nhất",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0D3B34),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(knowledgeArticles) { article ->
            EnhancedKnowledgeItem(article, onClick = onNavigateToArticle)
        }
    }
}

@Composable
fun FeaturedLearningCard(title: String, desc: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
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
                        "HOT COURSE",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text(desc, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
            }
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(100.dp).align(Alignment.TopEnd).offset(x = 20.dp, y = (-10).dp)
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
fun EnhancedKnowledgeItem(article: Article, onClick: () -> Unit) {
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
                    .background(Color(0xFFF1F8E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(article.icon, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(article.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Text(article.description, color = Color.Gray, fontSize = 13.sp, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    Text(" 5 phút đọc", fontSize = 11.sp, color = Color.LightGray)
                }
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun ClothingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        // Tip Box
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0D3B34)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Color(0xFFC8E6C9), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "Trang phục phù hợp giúp bạn tự tin hơn 40% trong buổi phỏng vấn.",
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        
        Text("Gợi ý theo ngành nghề", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0D3B34))
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ModernCategoryCard(Modifier.weight(1f), "Công nghệ", Icons.Rounded.Terminal, Color(0xFFE3F2FD))
            ModernCategoryCard(Modifier.weight(1f), "Kinh doanh", Icons.Rounded.BusinessCenter, Color(0xFFFFF3E0))
        }

        Spacer(modifier = Modifier.height(28.dp))
        
        Text("Tác phong chuyên nghiệp", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0D3B34))
        Spacer(modifier = Modifier.height(16.dp))
        
        ProfessionTipCard(
            title = "Nụ cười & Ánh mắt",
            desc = "Duy trì eye-contact tự nhiên",
            icon = Icons.Rounded.SentimentSatisfiedAlt,
            color = Color(0xFFF1F8E9)
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfessionTipCard(
            title = "Tư thế ngồi",
            desc = "Thẳng lưng, hơi hướng về phía trước",
            icon = Icons.Rounded.AccessibilityNew,
            color = Color(0xFFE8EAF6)
        )
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ModernCategoryCard(modifier: Modifier, title: String, icon: ImageVector, bgColor: Color) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0D3B34), modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D3B34))
        }
    }
}

@Composable
fun ProfessionTipCard(title: String, desc: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(desc, color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

data class Article(val title: String, val description: String, val icon: ImageVector)

val knowledgeArticles = listOf(
    Article("Bí quyết trả lời tình huống", "Sử dụng cấu trúc STAR chuyên nghiệp", Icons.Rounded.AutoStories),
    Article("5 câu hỏi nên hỏi NTD", "Thể hiện sự quan tâm tinh tế", Icons.Rounded.Groups),
    Article("Kỹ năng đàm phán lương", "Cách đề xuất mức lương mong muốn", Icons.Rounded.Payments),
    Article("Chuẩn bị tâm lý", "Giữ bình tĩnh trước áp lực", Icons.Rounded.SelfImprovement)
)
