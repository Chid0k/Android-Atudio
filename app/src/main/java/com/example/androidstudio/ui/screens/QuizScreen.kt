package com.example.androidstudio.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.network.QuizCategory
import com.example.androidstudio.network.QuizQuestion
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    category: QuizCategory,
    questionCount: Int,
    timeLimitMinutes: Int?,
    onQuizComplete: (Int, Int) -> Unit, // score, totalQuestions
    onExit: () -> Unit
) {
    val quizQuestions = remember { 
        category.questions.shuffled().take(questionCount) 
    }
    
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var timeLeftSeconds by remember { 
        mutableIntStateOf(timeLimitMinutes?.let { it * 60 } ?: -1) 
    }

    val currentQuestion = quizQuestions[currentQuestionIndex]
    val progress = (currentQuestionIndex + 1).toFloat() / questionCount
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    val primaryColor = Color(0xFF0D3B34)
    val backgroundColor = Color(0xFFF8FAF9)

    LaunchedEffect(timeLeftSeconds) {
        if (timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        } else if (timeLeftSeconds == 0) {
            onQuizComplete(score, questionCount)
        }
    }

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onExit) {
                        Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.Black)
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = category.category.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "Câu ${currentQuestionIndex + 1} / $questionCount",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                    }

                    if (timeLimitMinutes != null) {
                        Surface(
                            color = Color(0xFFE3F2FD),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Timer, null, tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatTime(timeLeftSeconds),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = primaryColor,
                    trackColor = Color(0xFFE0E0E0)
                )
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = {
                        if (!isSubmitted) {
                            if (selectedOptionIndex == currentQuestion.correctAnswerIndex) {
                                score++
                            }
                            isSubmitted = true
                        } else {
                            if (currentQuestionIndex < quizQuestions.size - 1) {
                                currentQuestionIndex++
                                selectedOptionIndex = null
                                isSubmitted = false
                            } else {
                                onQuizComplete(score, questionCount)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(56.dp),
                    enabled = selectedOptionIndex != null,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (!isSubmitted) "Kiểm tra" 
                                   else if (currentQuestionIndex == quizQuestions.size - 1) "Hoàn thành" 
                                   else "Câu tiếp theo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSubmitted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    text = currentQuestion.question,
                    modifier = Modifier.padding(24.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor,
                    lineHeight = 26.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isCorrect = index == currentQuestion.correctAnswerIndex
                    val isSelected = selectedOptionIndex == index
                    
                    val state = when {
                        !isSubmitted && isSelected -> OptionState.SELECTED
                        isSubmitted && isCorrect -> OptionState.CORRECT
                        isSubmitted && isSelected && !isCorrect -> OptionState.WRONG
                        else -> OptionState.DEFAULT
                    }

                    OptionItem(
                        text = option,
                        prefix = ('A' + index).toString(),
                        state = state,
                        onClick = { 
                            if (!isSubmitted) {
                                selectedOptionIndex = index 
                            }
                        }
                    )
                }
            }

            if (isSubmitted) {
                Spacer(modifier = Modifier.height(24.dp))
                ExplanationCard(
                    explanation = currentQuestion.explanation,
                    suggestion = currentQuestion.reviewSuggestion
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

enum class OptionState {
    DEFAULT, SELECTED, CORRECT, WRONG
}

@Composable
fun OptionItem(text: String, prefix: String, state: OptionState, onClick: () -> Unit) {
    val primaryColor = Color(0xFF0D3B34)
    val correctColor = Color(0xFF2E7D32)
    val errorColor = Color(0xFFD32F2F)

    val borderColor = when (state) {
        OptionState.SELECTED -> primaryColor
        OptionState.CORRECT -> correctColor
        OptionState.WRONG -> errorColor
        else -> Color(0xFFE0E0E0)
    }

    val borderWidth = if (state == OptionState.DEFAULT) 1.dp else 2.dp
    
    val contentColor = when (state) {
        OptionState.CORRECT -> correctColor
        OptionState.WRONG -> errorColor
        else -> Color.Black
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(width = borderWidth, color = borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$prefix. ",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (state == OptionState.SELECTED) primaryColor else contentColor
            )
            Text(
                text = text,
                fontSize = 15.sp,
                color = if (state == OptionState.SELECTED) primaryColor else contentColor,
                modifier = Modifier.weight(1f)
            )
            
            if (state == OptionState.CORRECT) {
                Icon(Icons.Rounded.CheckCircle, null, tint = correctColor, modifier = Modifier.size(20.dp))
            } else if (state == OptionState.WRONG) {
                Icon(Icons.Rounded.Cancel, null, tint = errorColor, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ExplanationCard(explanation: String?, suggestion: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Giải thích chi tiết", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 15.sp,
                    color = Color(0xFF1976D2)
                )
            }
            
            if (explanation != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = explanation,
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2),
                    lineHeight = 20.sp
                )
            }

            if (suggestion != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFF1976D2).copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Gợi ý ôn tập", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF2E7D32))
                        Text(suggestion, fontSize = 13.sp, color = Color(0xFF2E7D32).copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
