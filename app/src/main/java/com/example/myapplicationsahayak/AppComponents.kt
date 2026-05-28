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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Purana Logo - ic_sahayak_logo_image.png
            Image(
                painter = painterResource(id = R.drawable.ic_sahayak_logo_image),
                contentDescription = "Sahayak Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "SAHAYAK",
                color = Color(0xFFFFB300),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Text(
                "connecting campus care",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                color = Color(0xFFFFB300),
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
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
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Purana Logo - ic_sahayak_logo_image.png
                Image(
                    painter = painterResource(id = R.drawable.ic_sahayak_logo_image),
                    contentDescription = "Sahayak Logo",
                    modifier = Modifier.size(70.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isLoginMode) "Welcome Back!" else "Create Account",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = if (isLoginMode) "Login to continue" else "Sign up to get started",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (!isLoginMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = regNo,
                        onValueChange = { regNo = it },
                        label = { Text("LPU Registration No") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("student@lpu.in") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(8.dp)
                )

                if (!isLoginMode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = course,
                        onValueChange = { course = it },
                        label = { Text("Course") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
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
                            } else {
                                isLoading = true
                                SahayakGlobalEngine.loginUser(email, password) { success, message ->
                                    isLoading = false
                                    if (success) onAuthSuccess() else errorMessage = message
                                }
                            }
                        } else {
                            when {
                                name.isBlank() -> errorMessage = "Please enter your name"
                                regNo.isBlank() -> errorMessage = "Please enter registration number"
                                email.isBlank() -> errorMessage = "Please enter email"
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
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            if (isLoginMode) "LOGIN" else "REGISTER",
                            color = Color(0xFFFFB300),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { isLoginMode = !isLoginMode }) {
                    Text(
                        if (isLoginMode) "New User? Create Account" else "Already have account? Login",
                        color = Color(0xFF1976D2),
                        fontSize = 13.sp
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)

                TextButton(onClick = onGuestMode) {
                    Text("Continue as Guest", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}