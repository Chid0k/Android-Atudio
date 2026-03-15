package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InterviewScreen(
    onEndInterview: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Placeholder for Interviewer Video
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            // In a real app, this would be a VideoPlayer or CameraPreview
            Text("Interviewer Video", color = Color.White, modifier = Modifier.align(Alignment.Center))
        }

        // Recording Indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Red.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(8.dp).background(Color.White, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
            Text("REC 00:04:11", color = Color.White, fontSize = 12.sp)
        }

        // Interviewer Question Card
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .width(280.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Harriet M.", fontWeight = FontWeight.Bold, color = Color.Black)
                Text(
                    "Bạn hãy mô tả quá trình thiết kế UX mà bạn thường áp dụng cho một dự án mới?",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // User PIP (Picture-in-Picture)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp)
                .size(100.dp, 140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
        ) {
            Text("User", color = Color.White, modifier = Modifier.align(Alignment.Center))
        }

        // Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mic toggle
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
            }

            // End call
            IconButton(
                onClick = onEndInterview,
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Red, CircleShape)
            ) {
                Icon(Icons.Default.CallEnd, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }

            // Camera toggle
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
            }
        }
    }
}
