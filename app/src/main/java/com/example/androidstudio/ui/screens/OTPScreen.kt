package com.example.androidstudio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPScreen(
    onNavigateBack: () -> Unit,
    onVerifyOTP: () -> Unit
) {
    var otpValue by remember { mutableStateOf(List(6) { "" }) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Icon(
                    Icons.Outlined.MailOutline,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nhập mã OTP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Chúng tôi đã gửi mã về địa chỉ email\nluanle.ae3002@gmail.com",
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                otpValue.forEachIndexed { index, char ->
                    OutlinedTextField(
                        value = char,
                        onValueChange = {
                            if (it.length <= 1) {
                                val newList = otpValue.toMutableList()
                                newList[index] = it
                                otpValue = newList
                            }
                        },
                        modifier = Modifier.width(45.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onVerifyOTP,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0E3C3E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Xác thực OTP", modifier = Modifier.padding(vertical = 8.dp))
            }

            TextButton(onClick = { /* Resend OTP */ }) {
                Text("Không nhận được mã? Gửi lại", color = Color.Gray)
            }
        }
    }
}
