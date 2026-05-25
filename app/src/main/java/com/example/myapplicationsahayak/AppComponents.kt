package com.example.myapplicationsahayak

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SahayakSplashScreen(onSplashComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
        onSplashComplete()
    }
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_sahayak_logo),
                contentDescription = "Sahayak Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color(0xFFFFB300), strokeWidth = 4.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("SAHAYAK APP", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("connecting campus care", color = Color.LightGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun LoginGatewayScreen(onAuthSuccess: () -> Unit, onGuestMode: () -> Unit) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var regNo by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F6F9)), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp).verticalScroll(rememberScrollState()),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Logo on Login Page
                Image(
                    painter = painterResource(id = R.drawable.ic_sahayak_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isLoginMode) "🔒 WELCOME BACK" else "📝 CREATE ACCOUNT",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121212)
                )
                Text(
                    text = if (isLoginMode) "Login to continue" else "Register to get started",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(20.dp))

                if (!isLoginMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = regNo,
                        onValueChange = { regNo = it },
                        label = { Text("LPU Registration No *") },
                        placeholder = { Text("12XXXXXX") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (isLoginMode) "Email" else "Email *") },
                    placeholder = { Text("student@lpu.in") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    isError = email.isNotEmpty() && !SahayakGlobalEngine.isValidEmail(email),
                    supportingText = {
                        if (email.isNotEmpty() && !SahayakGlobalEngine.isValidEmail(email)) {
                            Text("Enter valid email", color = Color.Red, fontSize = 10.sp)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (!isLoginMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        label = { Text("Course *") },
                        placeholder = { Text("B.Tech CSE") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                errorMessage?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (isLoginMode) {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter email and password"
                            } else if (!SahayakGlobalEngine.isValidEmail(email)) {
                                errorMessage = "Please enter a valid email address"
                            } else {
                                isLoading = true
                                SahayakGlobalEngine.loginUser(email, password) { success, message ->
                                    isLoading = false
                                    if (success) {
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = message
                                    }
                                }
                            }
                        } else {
                            when {
                                name.isBlank() -> errorMessage = "Please enter your name"
                                regNo.isBlank() -> errorMessage = "Please enter registration number"
                                email.isBlank() -> errorMessage = "Please enter email"
                                !SahayakGlobalEngine.isValidEmail(email) -> errorMessage = "Please enter a valid email address"
                                password.isBlank() -> errorMessage = "Please enter password"
                                password != confirmPassword -> errorMessage = "Passwords do not match"
                                course.isBlank() -> errorMessage = "Please enter your course"
                                phone.isBlank() -> errorMessage = "Please enter your phone number"
                                else -> {
                                    isLoading = true
                                    val profile = UserProfile(
                                        name = name,
                                        regNo = regNo,
                                        course = course,
                                        phone = phone,
                                        email = email
                                    )
                                    SahayakGlobalEngine.registerUser(email, password, profile) { success, message ->
                                        isLoading = false
                                        if (success) {
                                            Toast.makeText(context, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
                                            isLoginMode = true
                                            email = ""
                                            password = ""
                                            name = ""
                                            regNo = ""
                                            course = ""
                                            phone = ""
                                            errorMessage = null
                                        } else {
                                            errorMessage = message
                                        }
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
                        Text(if (isLoginMode) "LOGIN" else "REGISTER", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = { isLoginMode = !isLoginMode }) {
                    Text(if (isLoginMode) "New User? Create Account" else "Already have account? Login", color = Color(0xFF1976D2))
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                TextButton(onClick = onGuestMode) {
                    Text("🛡️ Continue as Guest", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}