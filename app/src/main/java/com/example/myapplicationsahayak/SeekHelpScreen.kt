package com.example.myapplicationsahayak

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
fun SeekHelpScreen(onBack: () -> Unit, onSubmit: (HelpRequest) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("") }
    var selectedNeedType by remember { mutableStateOf("BASIC") }
    var phoneNumber by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.phone ?: "") }
    var showPopup by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Need Help", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFB300))
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("📢 Request Assistance", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            // Need Type
            Text("Priority Level", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = selectedNeedType == "URGENT",
                    onClick = { selectedNeedType = "URGENT" },
                    label = { Text("🔴 URGENT", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF44336),
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = selectedNeedType == "BASIC",
                    onClick = { selectedNeedType = "BASIC" },
                    label = { Text("🟢 BASIC", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.White
                    )
                )
            }

            // Need/Requirement Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What do you need? *") },
                placeholder = { Text("e.g., Chemistry Lab Manual, Notes, Calculator") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description / Details *") },
                placeholder = { Text("Describe what help you need in detail...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(10.dp)
            )

            // Location Block
            OutlinedTextField(
                value = selectedLocation,
                onValueChange = { selectedLocation = it },
                label = { Text("Your Location / Block *") },
                placeholder = { Text("e.g., Block 34, Room 201, Library") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            // Mobile Number
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Mobile Number *") },
                placeholder = { Text("Enter your contact number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                supportingText = {
                    Text("Helpers will contact you on this number", fontSize = 10.sp, color = Color.Gray)
                }
            )

            // Image Option
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Image, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Image (Optional)", fontSize = 13.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Choose Image", fontSize = 11.sp, color = Color.White)
                        }
                    }
                    if (selectedImageUri != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("✓ Image selected", fontSize = 11.sp, color = Color(0xFF4CAF50))
                    }
                }
            }

            errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    when {
                        title.isBlank() -> errorMessage = "Please enter what you need"
                        description.isBlank() -> errorMessage = "Please enter description"
                        selectedLocation.isBlank() -> errorMessage = "Please enter your location"
                        phoneNumber.isBlank() -> errorMessage = "Please enter your mobile number"
                        else -> {
                            isLoading = true
                            errorMessage = null
                            val request = HelpRequest(
                                title = title,
                                description = description,
                                location = selectedLocation,
                                needType = selectedNeedType,
                                blockNumber = selectedLocation.replace("Block ", "").toIntOrNull() ?: 1,
                                userName = SahayakGlobalEngine.currentUser?.name ?: "Guest",
                                userRegNo = SahayakGlobalEngine.currentUser?.regNo ?: "GUEST",
                                userCourse = SahayakGlobalEngine.currentUser?.course ?: "",
                                userPhone = phoneNumber,
                                userEmail = SahayakGlobalEngine.currentUser?.email ?: ""
                            )
                            SahayakGlobalEngine.addHelpRequest(request) { success ->
                                isLoading = false
                                if (success) {
                                    showPopup = true
                                    onSubmit(request)
                                } else {
                                    errorMessage = "Failed to create request"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("BROADCAST REQUEST", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) },
            title = { Text("✓ Request Broadcasted!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your request has been sent to campus helpers.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("📍 $selectedLocation", color = Color(0xFF1976D2), fontSize = 12.sp)
                    Text("Priority: $selectedNeedType", color = if (selectedNeedType == "URGENT") Color.Red else Color(0xFF4CAF50), fontSize = 12.sp)
                    Text("📞 $phoneNumber", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(onClick = { showPopup = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}