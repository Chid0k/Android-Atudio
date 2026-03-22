package com.example.androidstudio.ui.screens

import android.util.Log
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FeedbackData(
    @kotlinx.serialization.SerialName("general_feedback") val generalFeedback: String,
    val scores: Map<String, Int>,
    val strengths: List<String>,
    val improvements: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultAnalysisScreen(
    userId: Int?,
    sessionId: Int? = null,
    isFromInterview: Boolean = false,
    onNavigateBack: () -> Unit
) {
    var feedbackData by remember { mutableStateOf<FeedbackData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId, sessionId) {
        if (userId != null) {
            try {
                // Chỉ delay khi đi từ màn hình phỏng vấn
                if (isFromInterview) {
                    delay(10000)
                }

                val session = if (sessionId != null) {
                    RetrofitClient.apiService.getInterviewSession(sessionId)
                } else {
                    val sessions = RetrofitClient.apiService.getInterviewSessions(userId)
                    if (sessions.isNotEmpty()) sessions[0] else null
                }

                if (session != null) {
                    val feedbackJson = session.feedbackJson
                    if (feedbackJson != null) {
                        try {
                            val json = Json { ignoreUnknownKeys = true }
                            feedbackData = json.decodeFromString<FeedbackData>(feedbackJson)
                        } catch (e: Exception) {
                            Log.e("ResultAnalysis", "Error parsing feedback: ${e.message}")
                            error = "Không thể phân tích dữ liệu đánh giá."
                        }
                    } else {
                        error = "Không tìm thấy dữ liệu đánh giá cho buổi phỏng vấn này."
                    }
                } else {
                    error = "Không tìm thấy thông tin buổi phỏng vấn."
                }
            } catch (e: Exception) {
                Log.e("ResultAnalysis", "Error fetching sessions: ${e.message}")
                error = "Lỗi kết nối máy chủ: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF0E3C3E))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Vui lòng chờ chúng tôi đang đánh giá kết quả",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(error!!, color = Color.Red, textAlign = TextAlign.Center)
            }
        } else if (feedbackData != null) {
            val data = feedbackData!!
            val totalScore = if (data.scores.isNotEmpty()) {
                (data.scores.values.sum().toFloat() / (data.scores.size * 10)) * 10
            } else 0f

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
                            Text(String.format("%.1f/10", totalScore), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
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
                                    sweepAngle = (totalScore / 10f) * 360f,
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
                    data.generalFeedback,
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
                            data.strengths.forEach { strength ->
                                Text("• $strength", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
                            Text("Cải thiện", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                            data.improvements.forEach { improvement ->
                                Text("• $improvement", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Đánh giá kĩ năng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                SkillBar("Giải quyết vấn đề", (data.scores["problem_solving"] ?: 0) / 10f)
                SkillBar("Kiến thức", (data.scores["knowledge"] ?: 0) / 10f)
                SkillBar("Giao tiếp", (data.scores["communication"] ?: 0) / 10f)
                SkillBar("Thái độ/ Kĩ năng mềm", (data.scores["soft_skills"] ?: 0) / 10f)
            }
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
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .padding(top = 4.dp),
            color = Color(0xFF0E3C3E),
            trackColor = Color(0xFFE0E0E0),
            strokeCap = StrokeCap.Round,
        )
    }
}
