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
import com.example.androidstudio.network.QuizCategory
import com.example.androidstudio.network.RetrofitClient
import com.example.androidstudio.ui.components.BottomNavigationBar
import com.example.androidstudio.ui.components.ModernTabItem
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromStream
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    userId: Int?,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToArticle: (Int) -> Unit,
    onNavigateToQuizSetup: (QuizCategory) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    
    var articles by remember { mutableStateOf<List<KnowledgeArticle>>(emptyList()) }
    var quizCategories by remember { mutableStateOf<List<QuizCategory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showUploadCVPrompt by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

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

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    fun loadQuiz() {
        scope.launch {
            try {
                val inputStream = context.assets.open("quizz.json")
                quizCategories = RetrofitClient.json.decodeFromStream(inputStream)
            } catch (e: Exception) {
                // Silently fail or log for quiz loading
            }
        }
    }

    LaunchedEffect(userId) {
        loadArticles()
        loadQuiz()
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
                    placeholder = { Text("Tìm kiếm bài viết, mẹo phỏng vấn...", fontSize = 14.sp, color = Color.Gray) },
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
                    text = "Trắc nghiệm",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                ModernTabItem(
                    text = "Phục trang",
                    isSelected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedTab == 0) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTab) {
                0 -> {
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
                                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp), textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { loadArticles() }, colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) {
                                    Text("Thử lại")
                                }
                            }
                        }
                    } else {
                        KnowledgeContent(articles, searchQuery, onNavigateToArticle)
                    }
                }
                1 -> QuizContent(quizCategories, onNavigateToQuizSetup)
                2 -> ClothingContent()
            }
        }
    }
}

@Composable
fun KnowledgeContent(
    articles: List<KnowledgeArticle>,
    searchQuery: String,
    onNavigateToArticle: (Int) -> Unit
) {
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
                    category = firstArticle.category ?: "Mới nhất",
                    onClick = { onNavigateToArticle(firstIndex + 1) }
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
fun FeaturedLearningCard(title: String, desc: String, category: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFB388FF), Color(0xFF7C4DFF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        title,
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        color = Color(0xFFF3E5F5),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            category.uppercase(),
                            color = Color(0xFF7C4DFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Bí quyết trả lời câu hỏi tình huống",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        desc,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        maxLines = 2
                    )
                }
            }
            
            Icon(
                Icons.Rounded.FavoriteBorder,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .offset(y = (-60).dp)
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when (article.category) {
                            "Chuẩn bị" -> Color(0xFFFFD54F)
                            "Văn hóa" -> Color(0xFFAED581)
                            else -> Color(0xFFF1F8E9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    article.category ?: "Kiến thức",
                    color = when (article.category) {
                        "Chuẩn bị" -> Color(0xFFE65100)
                        "Văn hóa" -> Color(0xFF33691E)
                        else -> Color(0xFF0D3B34)
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    article.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
            }
            
            Icon(
                Icons.Rounded.BookmarkBorder,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }
}

@Composable
fun QuizContent(categories: List<QuizCategory>, onCategoryClick: (QuizCategory) -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            "Chủ đề luyện tập",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        categories.forEach { category ->
            val icon = when (category.category) {
                "Kiến thức viết CV" -> Icons.Rounded.Description
                "Kỹ năng trả lời phỏng vấn" -> Icons.Rounded.ChatBubbleOutline
                "Tác phong & Giao tiếp" -> Icons.Rounded.Group
                "Trang phục" -> Icons.Rounded.WorkOutline
                "Xử lý tình huống" -> Icons.Rounded.ErrorOutline
                else -> Icons.Rounded.Quiz
            }
            
            val colors = when (category.category) {
                "Kiến thức viết CV" -> Color(0xFFE3F2FD) to Color(0xFF1976D2)
                "Kỹ năng trả lời phỏng vấn" -> Color(0xFFFFF3E0) to Color(0xFFF57C00)
                "Tác phong & Giao tiếp" -> Color(0xFFE8F5E9) to Color(0xFF388E3C)
                "Trang phục" -> Color(0xFFFCE4EC) to Color(0xFFC2185B)
                "Xử lý tình huống" -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
                else -> Color(0xFFF5F5F5) to Color(0xFF757575)
            }

            QuizCard(
                QuizTopic(
                    title = category.category,
                    count = "${category.totalQuestions}+",
                    icon = icon,
                    iconBg = colors.first,
                    iconTint = colors.second
                ),
                onClick = { onCategoryClick(category) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

data class QuizTopic(
    val title: String,
    val count: String,
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color
)

@Composable
fun QuizCard(topic: QuizTopic, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(topic.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    topic.icon,
                    contentDescription = null,
                    tint = topic.iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    topic.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    "Kho: ${topic.count} câu hỏi",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            
            Icon(
                Icons.Rounded.PlayCircleOutline,
                contentDescription = "Start",
                tint = Color(0xFF0D3B34),
                modifier = Modifier.size(28.dp)
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
