package com.example.androidstudio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToKnowledge: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val primaryColor = Color(0xFF0D3B34)
    val backgroundColor = Color(0xFFF8FAF9)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = onNavigateToHome,
                onHistoryClick = onNavigateToHistory,
                onKnowledgeClick = onNavigateToKnowledge,
                onProfileClick = { },
                selectedItem = 3
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
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with Gradient Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(primaryColor, Color(0xFF1B5E20))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Profile Picture with Ring
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Person,
                                contentDescription = null,
                                modifier = Modifier.size(60.dp),
                                tint = primaryColor
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Chính Đỗ",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pen tester • 1 năm kinh nghiệm",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }

                // Edit Button
                IconButton(
                    onClick = onNavigateToEdit,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = Color.White)
                }
            }

            // Profile Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    "Thông tin cá nhân",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Info Cards
                ProfileInfoCard(
                    label = "Email",
                    value = "chinhdo09@gmail.com",
                    icon = Icons.Rounded.Email,
                    color = Color(0xFFE3F2FD)
                )
                ProfileInfoCard(
                    label = "Ngành nghề",
                    value = "An toàn thông tin",
                    icon = Icons.Rounded.Work,
                    color = Color(0xFFFFF3E0)
                )
                ProfileInfoCard(
                    label = "Trình độ",
                    value = "Đại học - Cử nhân",
                    icon = Icons.Rounded.School,
                    color = Color(0xFFF1F8E9)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Tài liệu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = primaryColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // CV Section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Download CV */ },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, tint = Color(0xFFC62828))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CV mặc định", fontSize = 12.sp, color = Color.Gray)
                            Text("CV_DoDucChinh.pdf", fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Rounded.Download, contentDescription = null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Logout Button
                Button(
                    onClick = { /* Logout */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = null
                ) {
                    Icon(Icons.Rounded.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoCard(label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
