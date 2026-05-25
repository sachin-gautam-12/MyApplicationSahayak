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
    var selectedLocation by remember { mutableStateOf("Block 1") }
    var selectedNeedType by remember { mutableStateOf("BASIC") }
    var showPopup by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val blockNumbers = (1..60).map { "Block $it" }
    var locationExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Aid", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFFFB300))
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
            Text("📢 New Help Request", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            // Need Type Selection
            Text("Need Type *", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // URGENT Button
                Button(
                    onClick = { selectedNeedType = "URGENT" },
                    modifier = Modifier.width(160.dp).height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedNeedType == "URGENT") Color(0xFFF44336) else Color(0xFFF44336).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("URGENT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                // BASIC Button
                Button(
                    onClick = { selectedNeedType = "BASIC" },
                    modifier = Modifier.width(160.dp).height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedNeedType == "BASIC") Color(0xFF4CAF50) else Color(0xFF4CAF50).copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BASIC", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Help Required *") },
                placeholder = { Text("e.g., Need Chemistry Lab Manual") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFB300),
                    unfocusedBorderColor = Color.Gray
                )
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description *") },
                placeholder = { Text("Describe what help you need...") },
                modifier = Modifier.fillMaxWidth().height(80.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFFB300),
                    unfocusedBorderColor = Color.Gray
                )
            )

            // Block Selection (1 to 60)
            Text("Block Number (1-60) *", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            ExposedDropdownMenuBox(
                expanded = locationExpanded,
                onExpandedChange = { locationExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedLocation,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFB300),
                        unfocusedBorderColor = Color.Gray
                    )
                )
                ExposedDropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false },
                    modifier = Modifier.height(250.dp)
                ) {
                    blockNumbers.forEach { block ->
                        DropdownMenuItem(
                            text = { Text(block) },
                            onClick = {
                                selectedLocation = block
                                locationExpanded = false
                            }
                        )
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        isLoading = true
                        val request = HelpRequest(
                            title = title,
                            description = description,
                            location = selectedLocation,
                            needType = selectedNeedType,
                            blockNumber = selectedLocation.replace("Block ", "").toIntOrNull() ?: 1,
                            userName = SahayakGlobalEngine.currentUser?.name ?: "Guest",
                            userRegNo = SahayakGlobalEngine.currentUser?.regNo ?: "GUEST",
                            userCourse = SahayakGlobalEngine.currentUser?.course ?: "",
                            userPhone = SahayakGlobalEngine.currentUser?.phone ?: "",
                            userEmail = SahayakGlobalEngine.currentUser?.email ?: ""
                        )
                        SahayakGlobalEngine.addHelpRequest(request) { success ->
                            isLoading = false
                            if (success) {
                                showPopup = true
                                onSubmit(request)
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212)),
                shape = RoundedCornerShape(10.dp),
                enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Send, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LAUNCH BROADCAST", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    // Success Popup
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            icon = { Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(40.dp)) },
            title = { Text("✅ Request Added!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your request has been broadcasted!", fontSize = 13.sp)
                    Text("Location: $selectedLocation", color = Color(0xFF1976D2), fontSize = 12.sp)
                    Text("Need Type: $selectedNeedType", color = if (selectedNeedType == "URGENT") Color.Red else Color(0xFF4CAF50), fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPopup = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}