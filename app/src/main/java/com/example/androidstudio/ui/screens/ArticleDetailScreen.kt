package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.network.KnowledgeArticle
import com.example.androidstudio.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    userId: Int?,
    articleIndex: Int?,
    onNavigateBack: () -> Unit
) {
    var article by remember { mutableStateOf<KnowledgeArticle?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val primaryColor = Color(0xFF0D3B34)
    val backgroundColor = Color(0xFFF8FAF9)

    fun loadArticleDetail() {
        if (userId != null && articleIndex != null) {
            scope.launch {
                isLoading = true
                errorMessage = null
                try {
                    val response = RetrofitClient.apiService.getArticleDetail(userId, articleIndex)
                    article = response.article
                } catch (e: Exception) {
                    errorMessage = "Lỗi khi tải chi tiết bài viết: ${e.localizedMessage}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(userId, articleIndex) {
        loadArticleDetail()
    }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Chi tiết bài viết",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = primaryColor
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { loadArticleDetail() },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Thử lại")
                    }
                }
            }
        } else if (article != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Content Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Surface(
                            color = primaryColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = article?.category?.uppercase() ?: "HƯỚNG DẪN",
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = article?.title ?: "",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor,
                            lineHeight = 34.sp
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = article?.description ?: "",
                            fontSize = 15.sp,
                            color = Color.Gray,
                            lineHeight = 22.sp
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 24.dp),
                            thickness = 1.dp,
                            color = Color(0xFFF0F0F0)
                        )
                        
                        Text(
                            text = article?.content ?: "",
                            fontSize = 16.sp,
                            lineHeight = 28.sp,
                            color = Color(0xFF2D312E)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
