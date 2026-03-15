package com.example.androidstudio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiêu đề bài viết", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "HƯỚNG DẪN",
                color = Color(0xFF0D3B34),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Trang phục gọn gàng",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Placeholder for article content as seen in the mockup
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                color = Color(0xFFF9F9F9),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {}

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Bài viết khác", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ModernCategoryCard(Modifier.weight(1f), "Công nghệ (IT, Dev)", Icons.Default.Work, Color(0xFFE3F2FD))
                ModernCategoryCard(Modifier.weight(1f), "Business / Marketing", Icons.Default.Public, Color(0xFFFFF3E0))
            }
        }
    }
}
