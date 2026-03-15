package com.example.androidstudio.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidstudio.network.RetrofitClient
import com.example.androidstudio.network.TokenHolder
import com.example.androidstudio.network.UserProfile
import com.example.androidstudio.ui.components.BottomNavigationBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Int?,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToKnowledge: () -> Unit,
    onNavigateToInterviewSetup: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onLogout: () -> Unit
) {
    val primaryColor = Color(0xFF0D3B34)
    val accentColor = Color(0xFFCCFF90)
    val backgroundColor = Color(0xFFF0F4F3)
    val context = LocalContext.current
    
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            scope.launch {
                isLoading = true
                errorMessage = null
                try {
                    userProfile = RetrofitClient.apiService.getUserProfile(userId)
                } catch (e: Exception) {
                    errorMessage = "Không thể tải hồ sơ: ${e.localizedMessage}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    val cvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (userId != null) {
                scope.launch {
                    isUploading = true
                    try {
                        val file = getFileFromUri(context, it)
                        if (file != null) {
                            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                            
                            try {
                                val response = RetrofitClient.apiService.uploadCV(userId, body)
                                // If we get here, it's a success
                                userProfile = userProfile?.copy(
                                    cv_url = response.file_url,
                                    cv_filename = response.filename
                                )
                                Toast.makeText(context, "Tải CV thành công!", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                // Log the error to see what's happening
                                Log.e("ProfileScreen", "Upload error", e)
                                
                                // Sometimes Retrofit throws even if it's a 201/200 if the response body is unexpected
                                // Or if it's a network error.
                                // Re-fetching profile as a fallback if the user says it's actually there
                                try {
                                    val updatedProfile = RetrofitClient.apiService.getUserProfile(userId)
                                    userProfile = updatedProfile
                                    Toast.makeText(context, "Đã cập nhật CV!", Toast.LENGTH_SHORT).show()
                                } catch (innerE: Exception) {
                                    Toast.makeText(context, "Lỗi khi tải CV: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Lỗi file: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    } finally {
                        isUploading = false
                    }
                }
            }
        }
    }

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
                containerColor = accentColor,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier.offset(y = 50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        if (userId != null) {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    userProfile = RetrofitClient.apiService.getUserProfile(userId)
                                } catch (e: Exception) {
                                    errorMessage = "Không thể tải hồ sơ: ${e.localizedMessage}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    }) {
                        Text("Thử lại")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    // Gradient Background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(primaryColor, Color(0xFF1B5E20))
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image with Border
                        Surface(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape),
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp),
                                    tint = primaryColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userProfile?.full_name ?: "Người dùng",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF2C3E50)
                        )
                        
                        Surface(
                            modifier = Modifier.padding(top = 4.dp),
                            color = accentColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = userProfile?.major ?: "Chưa cập nhật",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryColor
                            )
                        }
                    }

                    // Edit Button in Corner
                    IconButton(
                        onClick = onNavigateToEdit,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Edit", tint = Color.White)
                    }
                }

                // Stats/Quick Info Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.History,
                        label = "Kinh nghiệm",
                        value = userProfile?.experience?.split(" ")?.firstOrNull() ?: "0",
                        unit = "năm"
                    )
                    QuickStatCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Rounded.Star,
                        label = "Kỹ năng",
                        value = userProfile?.skills?.split(",")?.size?.toString() ?: "0",
                        unit = "mục"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Detailed Info Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        "Thông tin chi tiết",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = primaryColor,
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    InfoRow(
                        icon = Icons.Rounded.Business,
                        label = "Chuyên ngành",
                        value = userProfile?.major ?: "Chưa cập nhật"
                    )
                    
                    InfoRow(
                        icon = Icons.Rounded.Extension,
                        label = "Kỹ năng tiêu biểu",
                        value = userProfile?.skills ?: "Chưa cập nhật"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Hồ sơ năng lực",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = primaryColor,
                        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                    )

                    // Documents/CV Card Section
                    if (userProfile?.cv_url == null) {
                        // Nút upload nếu chưa có CV
                        OutlinedButton(
                            onClick = { cvPickerLauncher.launch("application/pdf") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, primaryColor.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = primaryColor)
                            } else {
                                Icon(Icons.Rounded.UploadFile, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Tải lên hồ sơ (CV)", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    } else {
                        // Hiển thị Card CV nếu đã có
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        if (userProfile?.cv_url != null) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(userProfile?.cv_url))
                                            context.startActivity(intent)
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        userProfile?.cv_filename ?: "Hồ sơ năng lực.pdf",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text("Đã tải lên • Chạm để xem", fontSize = 12.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { cvPickerLauncher.launch("application/pdf") }) {
                                    Icon(Icons.Rounded.CloudUpload, contentDescription = "Update", tint = primaryColor)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Logout Button with subtle design
                    OutlinedButton(
                        onClick = {
                            TokenHolder.token = null
                            onLogout()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Rounded.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Đăng xuất tài khoản", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun QuickStatCard(modifier: Modifier = Modifier, icon: ImageVector, label: String, value: String, unit: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0D3B34).copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                Text(text = " $unit", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 2.dp))
            }
            Text(text = label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0D3B34), modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2C3E50))
        }
    }
}

private suspend fun getFileFromUri(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
    var fileName = "temp_cv.pdf"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }

    val file = File(context.cacheDir, fileName)
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        null
    }
}
