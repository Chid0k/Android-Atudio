package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.network.KnowledgeArticle
import com.example.androidstudio.network.RetrofitClient
import com.example.androidstudio.ui.components.BottomNavigationBar
import com.example.androidstudio.ui.components.ModernTabItem
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    userId: Int?,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToArticle: (Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    
    var articles by remember { mutableStateOf<List<KnowledgeArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showUploadCVPrompt by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val primaryColor = Color(0xFF0D3B34)
    val backgroundColor = Color(0xFFF8FAF9)

    fun loadArticles() {
        if (userId != null) {
            scope.launch {
                isLoading = true
                errorMessage = null
                showUploadCVPrompt = false
                try {
                    val response = RetrofitClient.apiService.getArticles(userId)
                    articles = response.articles
                } catch (e: HttpException) {
                    if (e.code() == 404) {
                        showUploadCVPrompt = true
                    } else {
                        errorMessage = "Lỗi khi tải bài viết: ${e.localizedMessage}"
                    }
                } catch (e: Exception) {
                    errorMessage = "Lỗi khi tải bài viết: ${e.localizedMessage}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(userId) {
        loadArticles()
    }

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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Vui lòng chờ, chúng tôi đang cập nhật các bài viết phù hợp cho bạn",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (showUploadCVPrompt) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Rounded.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = primaryColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Bạn chưa tải CV lên",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hãy tải CV để chúng tôi gợi ý các bài viết phù hợp nhất cho bạn.",
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNavigateToProfile,
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Tải CV ngay")
                        }
                    }
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { loadArticles() }) {
                            Text("Thử lại")
                        }
                    }
                }
            } else {
                if (selectedTab == 0) {
                    KnowledgeContent(articles, searchQuery, onNavigateToArticle)
                } else {
                    ClothingContent()
                }
            }
        }
    }
}

@Composable
fun KnowledgeContent(articles: List<KnowledgeArticle>, searchQuery: String, onNavigateToArticle: (Int) -> Unit) {
    val filteredArticlesWithIndices = articles.mapIndexed { index, article -> index to article }

        .filter { (_, article) ->
            searchQuery.isEmpty() || 
            article.title.contains(searchQuery, ignoreCase = true) || 
            article.description.contains(searchQuery, ignoreCase = true) 
        }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (filteredArticlesWithIndices.isNotEmpty()) {
            val (firstIndex, firstArticle) = filteredArticlesWithIndices.first()
            item {
                FeaturedLearningCard(
                    title = firstArticle.title,
                    desc = firstArticle.description,
                    icon = Icons.Rounded.AutoAwesome,
                    onClick = { onNavigateToArticle(firstIndex+1) }
                )
            }
            
            item {
                Text(
                    "Tất cả bài viết",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0D3B34),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            itemsIndexed(filteredArticlesWithIndices.drop(1)) { _, (index, article) ->
                EnhancedKnowledgeItem(article, onClick = { onNavigateToArticle(index + 1) })
            }
        } else {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy bài viết nào", color = Color.Gray)
                }
            }
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
                        "BÀI VIẾT MỚI",
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
fun EnhancedKnowledgeItem(article: KnowledgeArticle, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF1F8E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = Color(0xFF0D3B34)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0D3B34)
                )
                Text(
                    article.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
            
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
fun ClothingContent() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, end = 20.dp, bottom = 100.dp)
    ) {
        Text(
            "Trang phục chuyên nghiệp",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF0D3B34)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        ClothingCard(
            title = "Dành cho Nam",
            items = listOf("Sơ mi trắng/xanh nhạt", "Quần tây tối màu", "Giày tây đánh bóng", "Caravat (tùy chọn)"),
            icon = Icons.Rounded.Person
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ClothingCard(
            title = "Dành cho Nữ",
            items = listOf("Áo sơ mi/Blouse thanh lịch", "Chân váy chữ A/Quần tây", "Giày cao gót vừa phải", "Trang điểm nhẹ nhàng"),
            icon = Icons.Rounded.PersonOutline
        )
    }
}

@Composable
fun ClothingCard(title: String, items: List<String>, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0D3B34))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0D3B34))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }
    }
}
