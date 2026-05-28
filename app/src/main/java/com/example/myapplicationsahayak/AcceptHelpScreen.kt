package com.example.myapplicationsahayak

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcceptHelpScreen(
    onBack: () -> Unit,
    onOpenChat: (String, String, String) -> Unit
) {
    val pendingRequests = SahayakGlobalEngine.getAllPendingRequests()
    var selectedRequest by remember { mutableStateOf<HelpRequest?>(null) }
    var showReplyPopup by remember { mutableStateOf(false) }
    var replyMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F8))) {
        TopAppBar(
            title = {
                Text("Accept Help", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
            },
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
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendingRequests) { request ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    request.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Badge(
                                    containerColor = if (request.needType == "URGENT") Color(0xFFF44336) else Color(0xFF4CAF50),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        if (request.needType == "URGENT") "URGENT" else "BASIC",
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                request.description,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, null, tint = Color(0xFF1976D2), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(request.location, fontSize = 12.sp, color = Color(0xFF1976D2))
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Person, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(request.userName, fontSize = 12.sp, color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { selectedRequest = request },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ACCEPT", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        onOpenChat(request.userRegNo, request.userName, request.requestId)
                                    },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Filled.Chat, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("MESSAGE", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
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
        var isLoading by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isLoading) selectedRequest = null },
            title = {
                Text("Accept Request", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📋 Request Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${request.title}", fontSize = 12.sp)
                            Text("👤 ${request.userName}", fontSize = 11.sp, color = Color.Gray)
                            Text("📍 ${request.location}", fontSize = 11.sp, color = Color(0xFF1976D2))
                            Text("📞 ${request.userPhone}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    OutlinedTextField(
                        value = helperName,
                        onValueChange = { helperName = it },
                        label = { Text("Your Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = helperPhone,
                        onValueChange = { helperPhone = it },
                        label = { Text("Your Phone Number *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = helperId,
                        onValueChange = { helperId = it },
                        label = { Text("Your LPU ID *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = handoverLocation,
                        onValueChange = { handoverLocation = it },
                        label = { Text("Meeting Location *") },
                        placeholder = { Text("Where will you meet?") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Your Reply / Message *") },
                        placeholder = { Text("I can help you with this...") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (helperName.isNotBlank() && helperPhone.isNotBlank() &&
                            helperId.isNotBlank() && handoverLocation.isNotBlank() && note.isNotBlank()) {
                            isLoading = true

                            SahayakGlobalEngine.acceptRequest(
                                request.requestId,
                                helperName,
                                helperPhone,
                                helperId,
                                handoverLocation,
                                note
                            )

                            SahayakGlobalEngine.sendMessage(
                                helperId,
                                request.userName,
                                "✅ I've accepted your request: \"${request.title}\"\n\n📝 $note\n\n📍 Meet at: $handoverLocation\n\n📞 My phone: $helperPhone",
                                request.requestId
                            )

                            replyMessage = note
                            selectedRequest = null
                            isLoading = false
                            showReplyPopup = true
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ACCEPT & NOTIFY", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { if (!isLoading) selectedRequest = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    if (showReplyPopup) {
        AlertDialog(
            onDismissRequest = { showReplyPopup = false },
            icon = {
                Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
            },
            title = {
                Text("✓ Request Accepted!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Your acceptance has been sent!", fontSize = 14.sp)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Your Message:", fontSize = 11.sp, color = Color.Gray)
                            Text("\"$replyMessage\"", fontSize = 12.sp, color = Color(0xFF1976D2))
                        }
                    }

                    Text("You will earn +5 credits after completion", fontSize = 12.sp, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    Text("The requester has been notified via chat", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReplyPopup = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}