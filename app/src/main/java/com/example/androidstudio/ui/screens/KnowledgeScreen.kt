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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToArticle: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = onNavigateToHome,
                onHistoryClick = onNavigateToHistory,
                onProfileClick = onNavigateToProfile,
                selectedItem = 2 // "Learn" tab
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle Add */ },
                containerColor = Color(0xFF0D3B34),
                contentColor = Color.White,
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
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Góc học tập",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm bài viết, mẹo phỏng vấn...", fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF0D3B34)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedTab == 0) Color(0xFF0D3B34) else Color.Transparent)
                ) {
                    Text(
                        "Kiến thức",
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = if (selectedTab == 0) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedTab == 1) Color(0xFF0D3B34) else Color.Transparent)
                ) {
                    Text(
                        "Phục trang",
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = if (selectedTab == 1) Color.White else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == 0) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item {
                        FeaturedCard(
                            title = "Phương pháp STAR",
                            color = Color(0xFFA18CFF),
                            onClick = onNavigateToArticle
                        )
                    }
                    items(knowledgeArticles) { article ->
                        KnowledgeItem(article, onClick = onNavigateToArticle)
                    }
                }
            } else {
                ClothingAdviceSection()
            }
        }
    }
}

@Composable
fun FeaturedCard(title: String, color: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun KnowledgeItem(article: Article, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(article.icon, contentDescription = null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = article.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = article.description, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ClothingAdviceSection() {
    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFE8F5E9)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Ấn tượng đầu tiên rất quan trọng. Hãy chọn phong cách phù hợp với văn hóa công ty.",
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Gợi ý theo ngành nghề", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            ClothingCategoryCard(Modifier.weight(1f), "Công nghệ (IT, Dev)", Icons.Default.Work)
            ClothingCategoryCard(Modifier.weight(1f), "Business / Marketing", Icons.Default.Public)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Tác phong & Hình ảnh", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF1F8E9)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SentimentSatisfied, contentDescription = null, tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Nụ cười & Ánh mắt", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Duy trì eye-contact 60-70% thời gian.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ClothingCategoryCard(modifier: Modifier, title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.LightGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit,
    selectedItem: Int
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 10.sp) },
            selected = selectedItem == 0,
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
            label = { Text("Lịch sử", fontSize = 10.sp) },
            selected = selectedItem == 1,
            onClick = onHistoryClick
        )
        Spacer(modifier = Modifier.weight(1f))
        NavigationBarItem(
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Learn") },
            label = { Text("Học", fontSize = 10.sp) },
            selected = selectedItem == 2,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Hồ sơ", fontSize = 10.sp) },
            selected = selectedItem == 3,
            onClick = onProfileClick
        )
    }
}

data class Article(val title: String, val description: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val knowledgeArticles = listOf(
    Article("Bí quyết trả lời câu hỏi tình huống", "Mẹo cách sử dụng cấu trúc Situation, Task, Action...", Icons.Default.Description),
    Article("5 câu hỏi nên hỏi ngược lại NTD", "Thể hiện sự quan tâm bằng những câu hỏi tinh tế...", Icons.Default.People)
)
