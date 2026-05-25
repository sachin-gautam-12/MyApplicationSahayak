package com.example.myapplicationsahayak

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isEditing by remember { mutableStateOf(false) }
    var tempProfile by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.copy() ?: UserProfile()) }
    var isLoading by remember { mutableStateOf(false) }
    var localImageBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    // Load saved image from local storage
    LaunchedEffect(Unit) {
        val savedImageFile = File(context.filesDir, "profile_image_${tempProfile.userId}.jpg")
        if (savedImageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(savedImageFile.absolutePath)
            localImageBitmap = bitmap.asImageBitmap()
            tempProfile.photoUrl = savedImageFile.absolutePath
        }
    }

    // Save image to local storage
    fun saveImageToLocalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "profile_image_${tempProfile.userId}.jpg"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            val savedPath = saveImageToLocalStorage(it)
            if (savedPath != null) {
                val bitmap = BitmapFactory.decodeFile(savedPath)
                localImageBitmap = bitmap.asImageBitmap()
                tempProfile.photoUrl = savedPath
                Toast.makeText(context, "Image saved locally!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFFFB300))
                    }
                },
                actions = {
                    if (isEditing) {
                        TextButton(onClick = {
                            isLoading = true
                            SahayakGlobalEngine.updateProfile(tempProfile) { success ->
                                isLoading = false
                                if (success) {
                                    isEditing = false
                                    Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }) {
                            Text("Save", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        TextButton(onClick = { isEditing = true }) {
                            Text("Edit", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF4F6F9))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box(contentAlignment = Alignment.BottomEnd) {
                if (localImageBitmap != null) {
                    Image(
                        bitmap = localImageBitmap!!,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (tempProfile.photoUrl != null) {
                    Image(
                        bitmap = BitmapFactory.decodeFile(tempProfile.photoUrl).asImageBitmap(),
                        contentDescription = "Profile Photo",
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        color = Color(0xFFE0E0E0)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                }
                if (isEditing) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFB300),
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("📷", fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileDetailRow("Full Name", tempProfile.name, isEditing) { tempProfile.name = it }
                    ProfileDetailRow("Registration No.", tempProfile.regNo, isEditing) { tempProfile.regNo = it }
                    ProfileDetailRow("Email", tempProfile.email, isEditing) { tempProfile.email = it }
                    ProfileDetailRow("Course", tempProfile.course, isEditing) { tempProfile.course = it }
                    ProfileDetailRow("Phone Number", tempProfile.phone, isEditing) { tempProfile.phone = it }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Credits Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Credits Earned", color = Color.White, fontWeight = FontWeight.Medium)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFFB300).copy(alpha = 0.2f)
                    ) {
                        Text(
                            "${SahayakGlobalEngine.userCredits}",
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text("$label:", modifier = Modifier.width(110.dp), fontWeight = FontWeight.Medium, color = Color.Black, fontSize = 13.sp)
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        } else {
            Text(value.ifEmpty { "Not set" }, modifier = Modifier.fillMaxWidth(), color = Color.DarkGray, fontSize = 13.sp)
        }
    }
}