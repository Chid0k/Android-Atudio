package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSetupScreen(
    onNavigateBack: () -> Unit,
    onStartInterview: () -> Unit
) {
    var jobTitle by remember { mutableStateOf("Senior UX Designer") }
    var jobDescription by remember { mutableStateOf("Mô tả công việc, yêu cầu công việc...") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thiết lập phỏng vấn", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Thông tin công việc", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            
            OutlinedTextField(
                value = jobTitle,
                onValueChange = { jobTitle = it },
                label = { Text("Tên công việc") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = jobDescription,
                onValueChange = { jobDescription = it },
                label = { Text("Mô tả công việc") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = border(1.dp, Color.LightGray),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CV-UX_UI_DESIGNER.pdf", modifier = Modifier.weight(1.0f))
                    Text("Thay đổi", color = Color(0xFF0E3C3E), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Cấu hình", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            
            Text("Chế độ luyện tập", fontSize = 14.sp, color = Color.Gray)
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Tự do") }, modifier = Modifier.padding(end = 8.dp))
                FilterChip(selected = false, onClick = {}, label = { Text("Cơ bản") }, modifier = Modifier.padding(end = 8.dp))
                FilterChip(selected = false, onClick = {}, label = { Text("Theo đề") })
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text("Thời lượng", fontSize = 14.sp, color = Color.Gray)
                    OutlinedCard(modifier = Modifier.padding(top = 4.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("30 Phút", modifier = Modifier.weight(1.0f))
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1.0f)) {
                    Text("Độ khó", fontSize = 14.sp, color = Color.Gray)
                    OutlinedCard(modifier = Modifier.padding(top = 4.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Trung bình", modifier = Modifier.weight(1.0f))
                            Icon(Icons.Default.KeyboardArrowDown, null)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Ngôn ngữ", fontSize = 14.sp, color = Color.Gray)
            Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = true, onClick = {})
                Text("Tiếng Việt")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = false, onClick = {})
                Text("Tiếng Anh")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onStartInterview,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E3C3E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Bắt đầu phỏng vấn", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

fun border(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
