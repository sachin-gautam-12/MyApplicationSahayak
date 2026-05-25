package com.example.myapplicationsahayak

import androidx.compose.foundation.background
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
fun MyRequestsScreen(onBack: () -> Unit) {
    val userRequests = SahayakGlobalEngine.getUserRequests()
    var selectedCompleteRequest by remember { mutableStateOf<HelpRequest?>(null) }
    var showCompletePopup by remember { mutableStateOf(false) }
    var showCongratulations by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F8))) {
        TopAppBar(
            title = { Text("My Requests", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFB300))
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
        )

        if (userRequests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Info, null, modifier = Modifier.size(64.dp), tint = Color.Gray)
                    Text("No requests found", color = Color.Gray, fontSize = 16.sp)
                    Text("Create a request from Dashboard", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(userRequests) { request ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(request.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                when (request.status) {
                                    "PENDING" -> Badge(containerColor = Color(0xFFFF9800)) { Text("Pending") }
                                    "ACCEPTED" -> Badge(containerColor = Color(0xFF2196F3)) { Text("Accepted") }
                                    "COMPLETED" -> Badge(containerColor = Color(0xFF4CAF50)) { Text("Completed") }
                                }
                            }
                            Text(request.description, fontSize = 13.sp, color = Color.Gray)
                            Text("📍 ${request.location}", fontSize = 12.sp, color = Color(0xFF1976D2))
                            Text("Need: ${request.needType}", fontSize = 12.sp, color = if (request.needType == "URGENT") Color.Red else Color(0xFF4CAF50))

                            if (request.status == "ACCEPTED") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("🤝 Helper: ${request.helperName}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("📞 Phone: ${request.helperPhone}", fontSize = 12.sp)
                                Text("📍 Handover: ${request.handoverLocation}", fontSize = 12.sp)
                                Text("💬 Reply: \"${request.verificationNote}\"", fontSize = 12.sp, color = Color(0xFF1976D2))
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { selectedCompleteRequest = request },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("MARK AS COMPLETED", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (request.status == "COMPLETED") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Task Completed! +5 credits earned", fontSize = 13.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                }
                                Text("✅ Completed by: ${request.helperName}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Complete Confirmation Dialog
    if (selectedCompleteRequest != null) {
        val request = selectedCompleteRequest!!
        AlertDialog(
            onDismissRequest = { selectedCompleteRequest = null },
            title = { Text("✅ Complete Task", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Task: ${request.title}", fontWeight = FontWeight.Bold)
                    Text("Helper: ${request.helperName}")
                    Text("Handover at: ${request.handoverLocation}")
                    Text("Reply: \"${request.verificationNote}\"")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("You will earn +5 credits", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        SahayakGlobalEngine.completeRequest(request.requestId)
                        selectedCompleteRequest = null
                        showCongratulations = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CONFIRM COMPLETION", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCompleteRequest = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Congratulations Popup
    if (showCongratulations) {
        AlertDialog(
            onDismissRequest = { showCongratulations = false },
            icon = { Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(56.dp)) },
            title = { Text("🎉 Congratulations! 🎉", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You have successfully completed the task!", fontSize = 16.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("+5 Credits Added", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Total Credits: ${SahayakGlobalEngine.userCredits}", fontSize = 14.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCongratulations = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Awesome! 🎉", color = Color.White)
                }
            }
        )
    }
}