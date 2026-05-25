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
fun AcceptHelpScreen(onBack: () -> Unit) {
    val pendingRequests = SahayakGlobalEngine.getAllPendingRequests()
    var selectedRequest by remember { mutableStateOf<HelpRequest?>(null) }
    var showReplyPopup by remember { mutableStateOf(false) }
    var replyMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F8))) {
        TopAppBar(
            title = { Text("Accept Help", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFB300))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
        )

        if (pendingRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Info, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Text("No pending requests", color = Color.Gray, fontSize = 16.sp)
                    Text("Check back later", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(pendingRequests) { request ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedRequest = request },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(request.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Badge(
                                    containerColor = if (request.needType == "URGENT") Color(0xFFF44336) else Color(0xFF4CAF50),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(if (request.needType == "URGENT") "URGENT" else "BASIC", fontSize = 10.sp)
                                }
                            }
                            Text(request.description, fontSize = 13.sp, color = Color.Gray)
                            Text("📍 ${request.location}", fontSize = 12.sp, color = Color(0xFF1976D2))
                            Text("👤 ${request.userName}", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { selectedRequest = request },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Filled.Share, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ACCEPT & HELP", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Accept Dialog
    if (selectedRequest != null) {
        val request = selectedRequest!!
        var helperName by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.name ?: "") }
        var helperPhone by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.phone ?: "") }
        var helperId by remember { mutableStateOf(SahayakGlobalEngine.currentUser?.regNo ?: "") }
        var handoverLocation by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { selectedRequest = null },
            title = { Text("🤝 Accept Request", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Task: ${request.title}", fontWeight = FontWeight.Bold)
                    Text("Requester: ${request.userName} (${request.userRegNo})")
                    Text("Location: ${request.location}", color = Color(0xFF1976D2))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = helperName,
                        onValueChange = { helperName = it },
                        label = { Text("Your Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = helperPhone,
                        onValueChange = { helperPhone = it },
                        label = { Text("Your Phone *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = helperId,
                        onValueChange = { helperId = it },
                        label = { Text("Your LPU ID *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = handoverLocation,
                        onValueChange = { handoverLocation = it },
                        label = { Text("Handover Location *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Reply Message *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (helperName.isNotBlank() && helperPhone.isNotBlank() && handoverLocation.isNotBlank() && note.isNotBlank()) {
                            SahayakGlobalEngine.acceptRequest(
                                request.requestId,
                                helperName,
                                helperPhone,
                                helperId,
                                handoverLocation,
                                note
                            )
                            replyMessage = note
                            selectedRequest = null
                            showReplyPopup = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SUBMIT REPLY", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequest = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reply Confirmation Popup
    if (showReplyPopup) {
        AlertDialog(
            onDismissRequest = { showReplyPopup = false },
            icon = { Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp)) },
            title = { Text("✅ Reply Sent!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Your reply has been sent to the requester.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Message: \"$replyMessage\"", fontSize = 12.sp, color = Color(0xFF1976D2))
                    Text("You will earn +5 credits after completion", fontSize = 11.sp, color = Color(0xFFFFB300))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReplyPopup = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}