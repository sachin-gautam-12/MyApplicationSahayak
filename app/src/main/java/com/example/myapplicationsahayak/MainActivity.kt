package com.example.myapplicationsahayak

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var screen by remember { mutableStateOf("splash") }
            var isGuest by remember { mutableStateOf(false) }
            val context = LocalContext.current
            var mediaPlayer: MediaPlayer? = null

            // For direct chat navigation from AcceptHelp
            var directChatUser by remember { mutableStateOf<Pair<String, String>?>(null) }
            var openChatDirectly by remember { mutableStateOf(false) }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (!granted) Toast.makeText(context, "Notification permission denied!", Toast.LENGTH_SHORT).show()
            }

            LaunchedEffect(SahayakGlobalEngine.unreadNotificationCount) {
                if (SahayakGlobalEngine.unreadNotificationCount > 0 && SahayakGlobalEngine.isPushEnabled) {
                    try {
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                        mediaPlayer?.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            when (screen) {
                "splash" -> SahayakSplashScreen { screen = "login" }

                "login" -> LoginGatewayScreen(
                    onAuthSuccess = { isGuest = false; screen = "dashboard" },
                    onGuestMode = { isGuest = true; screen = "dashboard" }
                )

                "dashboard" -> {
                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                    MainDashboardScreen(
                        onSeekHelp = { screen = "seek" },
                        onAcceptHelp = { screen = "accept" },
                        onMyRequests = { screen = "myrequests" },
                        onLogout = { SahayakGlobalEngine.currentUser = null; screen = "login" },
                        onProfile = { screen = "profile" },
                        onSettings = { screen = "settings" },
                        directChatUser = directChatUser,
                        onClearDirectChat = { directChatUser = null; openChatDirectly = false }
                    )
                }

                "seek" -> SeekHelpScreen(onBack = { screen = "dashboard" }, onSubmit = { screen = "dashboard" })
                "accept" -> AcceptHelpScreen(
                    onBack = { screen = "dashboard" },
                    onOpenChat = { userId, userName, requestId ->
                        // Directly open chat without going to profile first
                        directChatUser = Pair(userId, userName)
                        screen = "dashboard"
                    }
                )
                "myrequests" -> MyRequestsScreen(onBack = { screen = "dashboard" })
                "profile" -> ProfileScreen(onBack = { screen = "dashboard" })
                "settings" -> SettingsScreen(onBack = { screen = "dashboard" }, onLogout = { SahayakGlobalEngine.currentUser = null; screen = "login" })
            }
        }
    }
}