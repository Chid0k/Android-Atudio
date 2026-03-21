package com.example.androidstudio.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultAnalysisScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kết quả phân tích", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E3C3E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Điểm tổng quát", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text("8.5/10", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = Color.White.copy(alpha = 0.2f),
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx())
                            )
                            drawArc(
                                color = Color(0xFF4CAF50),
                                startAngle = -90f,
                                sweepAngle = 306f, // 8.5/10 * 360
                                useCenter = false,
                                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nhận xét từ AI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "Bạn đã phỏng vấn rất tốt, trả lời rành mạch, kiến thức chuyên môn tốt. Tuy nhiên bạn cần cải thiện kĩ năng xử lí tình huống khi gặp một số câu hỏi liên quan đến kĩ thuật.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Text("Điểm mạnh", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                        Text("Giọng nói truyền cảm, rõ ràng, lưu loát", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
                        Text("Cải thiện", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                        Text("Kĩ năng xử lí chưa tối ưu khi gặp tình huống lạ", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Đánh giá kĩ năng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            
            SkillBar("Giải quyết vấn đề", 0.9f)
            SkillBar("Kiến thức", 0.85f)
            SkillBar("Giao tiếp", 0.8f)
            SkillBar("Thái độ/ Kĩ năng mềm", 0.95f)
        }
    }
}

@Composable
fun SkillBar(name: String, progress: Float) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, fontSize = 14.sp)
            Text("${(progress * 10).toInt()}/10", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp),
            color = Color(0xFF0E3C3E),
            trackColor = Color(0xFFE0E0E0),
            strokeCap = StrokeCap.Round
        )
    }
}
