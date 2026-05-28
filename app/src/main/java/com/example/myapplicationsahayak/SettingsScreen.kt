package com.example.myapplicationsahayak

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
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
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold) },
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
                .background(Color(0xFFF4F6F8))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Information
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("👤 Profile Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: ${SahayakGlobalEngine.currentUser?.name ?: "Guest"}")
                    Text("Reg No: ${SahayakGlobalEngine.currentUser?.regNo ?: "GUEST"}")
                    Text("Email: ${SahayakGlobalEngine.currentUser?.email ?: "Not set"}")
                    Text("Course: ${SahayakGlobalEngine.currentUser?.course ?: "Not set"}")
                    Text("Phone: ${SahayakGlobalEngine.currentUser?.phone ?: "Not set"}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Statistics
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Your Statistics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Tasks Posted: ${SahayakGlobalEngine.getUserRequests().size}")
                    Text("Tasks Completed: ${SahayakGlobalEngine.getAllCompletedRequests().size}")
                    Text("Credits Earned: ${SahayakGlobalEngine.userCredits}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App Info
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFFFFB300), modifier = Modifier.size(40.dp))
                    Text("About Sahayak App", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Version 2.0", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Developed by Sachin Singh", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    Text("© 2025 Sahayak App", color = Color.LightGray, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(onClick = { showLogoutDialog = true }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(onClick = { showLogoutDialog = false; onLogout() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }
}