package com.example.myapplicationsahayak

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ActivityLogScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedRequest by remember { mutableStateOf<HelpRequest?>(null) }
    var showAcceptDialog by remember { mutableStateOf<HelpRequest?>(null) }
    var showCompletePopup by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F9))) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF121212),
            contentColor = Color(0xFFFFB300)
        ) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("📋 Available", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("⚡ Accepted", fontWeight = FontWeight.Bold) })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("✅ Completed", fontWeight = FontWeight.Bold) })
        }

        Button(onClick = onBack, modifier = Modifier.padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFB300))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back", color = Color.White)
        }

        // Get filtered list based on selected tab
        val filteredList = when (selectedTab) {
            0 -> SahayakGlobalEngine.allRequests.filter { it.status == "PENDING" }
            1 -> SahayakGlobalEngine.allRequests.filter { it.status == "ACCEPTED" }
            else -> SahayakGlobalEngine.allRequests.filter { it.status == "COMPLETED" }
        }

        if (filteredList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Text("No requests found", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredList) { request ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (request.status == "PENDING") showAcceptDialog = request
                            else if (request.status == "ACCEPTED") selectedRequest = request
                        },
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(request.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                when (request.status) {
                                    "PENDING" -> Badge(containerColor = Color(0xFFFF9800)) { Text("Pending") }
                                    "ACCEPTED" -> Badge(containerColor = Color(0xFF2196F3)) { Text("Accepted") }
                                    "COMPLETED" -> Badge(containerColor = Color(0xFF4CAF50)) { Text("Completed") }
                                }
                            }
                            Text(request.description, fontSize = 12.sp, color = Color.Gray)
                            Text("📍 ${request.location}", fontSize = 11.sp, color = Color(0xFF1976D2))
                            Text("Need: ${request.needType}", fontSize = 11.sp, color = if (request.needType == "URGENT") Color.Red else Color(0xFF4CAF50))
                            Text("👤 ${request.userName} (${request.userRegNo})", fontSize = 11.sp)
                            if (request.status == "ACCEPTED") {
                                Text("🤝 Helper: ${request.helperName}", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                Text("📍 Handover: ${request.handoverLocation}", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            }
                            if (request.status == "COMPLETED") {
                                Text("✅ Completed +5 credits", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Accept Dialog
    if (showAcceptDialog != null) {
        val request = showAcceptDialog!!
        var helperName by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.name ?: "") }
        var helperPhone by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.phone ?: "") }
        var helperId by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.regNo ?: "") }
        var handoverLocation by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAcceptDialog = null },
            title = { Text("🤝 Accept Help Request", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Task: ${request.title}", fontWeight = FontWeight.Bold)
                    Text("Requester: ${request.userName} (${request.userRegNo})")
                    Text("Location: ${request.location}", color = Color(0xFF1976D2))
                    Text("Need: ${request.needType}", color = if (request.needType == "URGENT") Color.Red else Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = helperName, onValueChange = { helperName = it }, label = { Text("Your Name *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = helperPhone, onValueChange = { helperPhone = it }, label = { Text("Your Phone *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = helperId, onValueChange = { helperId = it }, label = { Text("Your LPU ID *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = handoverLocation, onValueChange = { handoverLocation = it }, label = { Text("Handover Location *") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Verification Note *") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (helperName.isNotBlank() && helperPhone.isNotBlank() && handoverLocation.isNotBlank() && note.isNotBlank()) {
                            SahayakGlobalEngine.acceptRequest(request.requestId, helperName, helperPhone, helperId, handoverLocation, note)
                            showAcceptDialog = null
                            selectedTab = 1
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text("ACCEPT & HELP", color = Color(0xFFFFB300)) }
            },
            dismissButton = { TextButton(onClick = { showAcceptDialog = null }) { Text("Cancel") } }
        )
    }

    // Complete Dialog
    if (selectedRequest != null && selectedRequest!!.status == "ACCEPTED") {
        val request = selectedRequest!!
        AlertDialog(
            onDismissRequest = { selectedRequest = null },
            title = { Text("✅ Mark as Completed", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Task: ${request.title}")
                    Text("Handover at: ${request.handoverLocation}")
                    Text("Note: ${request.verificationNote}")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("You will earn +5 credits", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        SahayakGlobalEngine.completeRequest(request.requestId)
                        selectedRequest = null
                        selectedTab = 2
                        showCompletePopup = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) { Text("COMPLETE TASK (+5 CREDITS)") }
            },
            dismissButton = { TextButton(onClick = { selectedRequest = null }) { Text("Cancel") } }
        )
    }

    // Congratulations Popup
    if (showCompletePopup) {
        AlertDialog(
            onDismissRequest = { showCompletePopup = false },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) },
            title = { Text("🎉 Congratulations! 🎉", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You have successfully completed the task!", fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("+5 Credits Added to Your Account", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCompletePopup = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Awesome! 🎉", color = Color.White)
                }
            }
        )
    }
}