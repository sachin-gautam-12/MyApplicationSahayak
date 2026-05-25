package com.example.myapplicationsahayak

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

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
                .background(Color(0xFFF4F6F8))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("👤 Profile Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (SahayakGlobalEngine.currentUser?.photoUrl != null) {
                            AsyncImage(
                                model = SahayakGlobalEngine.currentUser?.photoUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.size(50.dp).clip(CircleShape)
                            )
                        } else {
                            Icon(Icons.Filled.Person, null, modifier = Modifier.size(50.dp), tint = Color(0xFFFFB300))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(SahayakGlobalEngine.currentUser?.name ?: "Guest", fontWeight = FontWeight.Bold)
                            Text(SahayakGlobalEngine.currentUser?.email ?: "No email", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailRow("Registration No.", SahayakGlobalEngine.currentUser?.regNo ?: "GUEST")
                    DetailRow("Course", SahayakGlobalEngine.currentUser?.course ?: "Not set")
                    DetailRow("Phone", SahayakGlobalEngine.currentUser?.phone ?: "Not set")
                    DetailRow("Credits", "${SahayakGlobalEngine.userCredits}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Statistics Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Your Statistics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow("Total Tasks Posted", "${SahayakGlobalEngine.getUserRequests().size}")
                    DetailRow("Tasks Completed", "${SahayakGlobalEngine.getAllCompletedRequests().size}")
                    DetailRow("Credits Earned", "${SahayakGlobalEngine.userCredits}")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App Info Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "About", tint = Color(0xFFFFB300), modifier = Modifier.size(48.dp))
                    Text("Sahayak App", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Version 2.0", color = Color.LightGray, fontSize = 12.sp)
                    Text("Campus Care Connect", color = Color.LightGray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Developed by Sachin Singh", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    Text("© 2025 Sahayak App", color = Color.LightGray, fontSize = 10.sp)
                    Text("LPU Campus Project", color = Color.LightGray, fontSize = 10.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button - FIXED
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}