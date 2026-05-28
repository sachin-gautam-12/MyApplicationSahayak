package com.example.myapplicationsahayak

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onSeekHelp: () -> Unit,
    onAcceptHelp: () -> Unit,
    onMyRequests: () -> Unit,
    onLogout: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    directChatUser: Pair<String, String>? = null,
    onClearDirectChat: () -> Unit = {}
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }
    var selectedBottomTab by remember { mutableIntStateOf(0) }
    var openChatWith by remember { mutableStateOf<Pair<String, String>?>(null) }

    val unreadCount = SahayakGlobalEngine.unreadNotificationCount

    LaunchedEffect(directChatUser) {
        if (directChatUser != null) {
            selectedBottomTab = 1
            openChatWith = directChatUser
            onClearDirectChat()
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().setUserAgentValue(context.packageName)
    }

    val thoughts = listOf(
        "\"Campus aid, precise location tracking.\"",
        "\"Help is just a tap away\"",
        "\"Small acts of kindness multiply.\"",
        "\"Together we make LPU better.\""
    )
    var thoughtIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            thoughtIndex = (thoughtIndex + 1) % thoughts.size
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp), drawerContainerColor = Color(0xFF1A1A1A)) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(modifier = Modifier.size(80.dp).clip(CircleShape), color = Color(0xFFFFB300)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🤝", fontSize = 45.sp, color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(SahayakGlobalEngine.currentUser?.name ?: "Guest User", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(SahayakGlobalEngine.currentUser?.regNo ?: "LPU Guest", color = Color.LightGray, fontSize = 12.sp)
                    Text("⭐ ${SahayakGlobalEngine.userCredits} Credits", color = Color(0xFFFFB300), fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Person, null, tint = Color(0xFFFFB300)) },
                        label = { Text("My Profile", color = Color.White) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; onProfile() }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Help, null, tint = Color(0xFFFFB300)) },
                        label = { Text("Seek Help", color = Color.White) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; onSeekHelp() }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.People, null, tint = Color(0xFFFFB300)) },
                        label = { Text("Give Help", color = Color.White) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; onAcceptHelp() }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.List, null, tint = Color(0xFFFFB300)) },
                        label = { Text("Activity Logs", color = Color.White) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; onMyRequests() }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300)) },
                        label = { Text("Credit History", color = Color.White) },
                        selected = false,
                        onClick = { showCreditsDialog = true }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, null, tint = Color(0xFFFFB300)) },
                        label = { Text("Settings", color = Color.White) },
                        selected = false,
                        onClick = { scope.launch { drawerState.close() }; onSettings() }
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Developed by Sachin Kumar Singh", color = Color.Gray, fontSize = 10.sp)
                    Text("Lovely Professional University", color = Color.Gray, fontSize = 9.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF121212)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = Color(0xFFFFB300), modifier = Modifier.size(26.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🤝", fontSize = 28.sp)
                            Text("SAHAYAK", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }

                        Box {
                            IconButton(onClick = { showNotificationDialog = true }) {
                                Icon(Icons.Default.Notifications, null, tint = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                            }
                            if (unreadCount > 0) {
                                Badge(
                                    modifier = Modifier.offset(x = 18.dp, y = 6.dp),
                                    containerColor = Color.Red
                                ) {
                                    Text("$unreadCount", fontSize = 9.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFFF5EBE1), modifier = Modifier.height(65.dp)) {
                    NavigationBarItem(
                        selected = selectedBottomTab == 0,
                        onClick = { selectedBottomTab = 0; openChatWith = null },
                        icon = { Icon(Icons.Default.Home, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Home", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = selectedBottomTab == 1,
                        onClick = {
                            selectedBottomTab = 1
                            openChatWith = null
                        },
                        icon = { Icon(Icons.Default.Email, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Message", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = selectedBottomTab == 2,
                        onClick = { selectedBottomTab = 2; openChatWith = null },
                        icon = { Icon(Icons.Default.Map, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Map", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = selectedBottomTab == 3,
                        onClick = { selectedBottomTab = 3; openChatWith = null },
                        icon = { Icon(Icons.Default.Settings, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("Settings", fontSize = 11.sp) }
                    )
                }
            }
        ) { paddingValues ->
            when (selectedBottomTab) {
                0 -> DashboardHomeTab(paddingValues, thoughts[thoughtIndex], onSeekHelp, onAcceptHelp, onMyRequests, { showCreditsDialog = true })
                1 -> {
                    if (openChatWith != null) {
                        ChatScreenFunction(
                            receiverId = openChatWith!!.first,
                            receiverName = openChatWith!!.second,
                            onBack = { openChatWith = null }
                        )
                    } else {
                        MessagesScreenFunction(onOpenChat = { userId, userName ->
                            openChatWith = Pair(userId, userName)
                        })
                    }
                }
                2 -> MapScreenFunction()
                3 -> SettingsScreenTabFunction(onSettings, onLogout)
            }
        }
    }

    if (showCreditsDialog) {
        AlertDialog(
            onDismissRequest = { showCreditsDialog = false },
            title = { Text("💰 Credit History", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Current Balance: ${SahayakGlobalEngine.userCredits} PTS", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("How to earn credits:", fontWeight = FontWeight.Bold)
                    Text("• Accept and complete a task → +5 credits")
                    Text("• Get your request completed → +2 credits")
                    Text("• Refer a friend → +10 credits")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tasks Completed: ${SahayakGlobalEngine.getAllCompletedRequests().size}", color = Color(0xFF4CAF50))
                }
            },
            confirmButton = {
                Button(onClick = { showCreditsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212))) {
                    Text("Got it!", color = Color(0xFFFFB300))
                }
            }
        )
    }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("🔔 Notifications", fontWeight = FontWeight.Bold) },
            text = {
                if (SahayakGlobalEngine.notificationHistory.isEmpty()) {
                    Text("No notifications yet")
                } else {
                    Column(modifier = Modifier.heightIn(max = 300.dp)) {
                        SahayakGlobalEngine.notificationHistory.take(15).forEach { notif ->
                            Text("• $notif", modifier = Modifier.padding(vertical = 6.dp), fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { SahayakGlobalEngine.markNotificationsRead(); showNotificationDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212))) {
                    Text("Close", color = Color(0xFFFFB300))
                }
            }
        )
    }
}

@Composable
fun DashboardHomeTab(
    paddingValues: PaddingValues,
    thought: String,
    onSeekHelp: () -> Unit,
    onAcceptHelp: () -> Unit,
    onMyRequests: () -> Unit,
    showCredits: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🗺️ Map Status: Online • Punjab Hub Connected", fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
            Text("📍 LPU Campus", fontSize = 11.sp, color = Color.Gray)
        }

        Card(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8ECEF)),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            LPUCampusMapView(modifier = Modifier.fillMaxSize())
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB300))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Thoughts: $thought", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Need Help? Broadcast Now!", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSeekHelp,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Icon(Icons.Default.Campaign, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Request Assistance", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth().height(210.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FeatureCard(
                    title = "Request Aid",
                    icon = Icons.Default.Add,
                    bgColor = Color(0xFFE8F5E9),
                    iconColor = Color(0xFF4CAF50),
                    onClick = onSeekHelp
                )
            }
            item {
                FeatureCard(
                    title = "Accept Help",
                    icon = Icons.Default.People,
                    bgColor = Color(0xFFFFF3E0),
                    iconColor = Color(0xFFFF9800),
                    onClick = onAcceptHelp
                )
            }
            item {
                FeatureCard(
                    title = "Activity Logs",
                    icon = Icons.Default.List,
                    bgColor = Color(0xFFE3F2FD),
                    iconColor = Color(0xFF2196F3),
                    onClick = onMyRequests
                )
            }
            item {
                FeatureCard(
                    title = "Credit: ${SahayakGlobalEngine.userCredits} PTS",
                    icon = Icons.Default.Star,
                    bgColor = Color(0xFF1A1A1A),
                    iconColor = Color(0xFFFFB300),
                    onClick = showCredits
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Active Queries: ${SahayakGlobalEngine.getAllPendingRequests().size}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Completed: ${SahayakGlobalEngine.getAllCompletedRequests().size}", fontSize = 13.sp, color = Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun FeatureCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, bgColor: Color, iconColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                title,
                fontWeight = FontWeight.Medium,
                color = if (bgColor == Color(0xFF1A1A1A)) Color(0xFFFFB300) else Color.Black,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun LPUCampusMapView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                val lpuCenter = GeoPoint(31.2539, 75.7032)
                controller.setCenter(lpuCenter)

                val mainMarker = Marker(this)
                mainMarker.position = lpuCenter
                mainMarker.title = "LPU Main Campus"
                mainMarker.snippet = "Punjab Hub Connected"
                overlays.add(mainMarker)

                val requestedBlocks = SahayakGlobalEngine.allRequests
                    .filter { it.status == "PENDING" }
                    .mapNotNull { it.location.takeIf { loc -> loc.startsWith("Block") } }
                    .distinct()
                    .take(5)

                requestedBlocks.forEach { block ->
                    val blockNum = block.replace("Block ", "").toIntOrNull() ?: return@forEach
                    val row = (blockNum - 1) / 10
                    val col = (blockNum - 1) % 10
                    val lat = 31.2539 + (row * 0.0008)
                    val lng = 75.7032 + (col * 0.0008)
                    val marker = Marker(this)
                    marker.position = GeoPoint(lat, lng)
                    marker.title = block
                    marker.snippet = "Help Requested Here"
                    overlays.add(marker)
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun MessagesScreenFunction(onOpenChat: (String, String) -> Unit) {
    val conversations = SahayakGlobalEngine.conversations
    val currentUserId = SahayakGlobalEngine.currentUser?.userId ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(16.dp)
    ) {
        Text("💬 Messages", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Chat with campus helpers", fontSize = 13.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        if (conversations.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Chat, null, tint = Color.Gray, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No conversations yet", color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    Text("When someone accepts your request, you can chat here", fontSize = 13.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(conversations) { conv ->
                    val otherUserName = if (conv.participant1Id == currentUserId) conv.participant2Name else conv.participant1Name
                    val otherUserId = if (conv.participant1Id == currentUserId) conv.participant2Id else conv.participant1Id
                    val unreadCount = SahayakGlobalEngine.chatMessages.count {
                        it.senderId == otherUserId && !it.isRead
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenChat(otherUserId, otherUserName) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFB300).copy(alpha = 0.15f),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, tint = Color(0xFFFFB300), modifier = Modifier.size(28.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(otherUserName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    if (unreadCount > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.Red,
                                            modifier = Modifier.size(18.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("$unreadCount", color = Color.White, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                                Text(
                                    conv.lastMessage.take(40),
                                    fontSize = 13.sp,
                                    color = if (unreadCount > 0) Color.Black else Color.Gray,
                                    maxLines = 1
                                )
                            }
                            Text(
                                SimpleDateFormat("hh:mm a", Locale.getDefault())
                                    .format(Date(conv.lastMessageTime)),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenFunction(
    receiverId: String,
    receiverName: String,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = SahayakGlobalEngine.getMessagesWithUser(receiverId)
    val scope = rememberCoroutineScope()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    var isSending by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteChatDialog by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf<ChatMessage?>(null) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF121212)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // BACK BUTTON
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFB300))
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFFB300).copy(alpha = 0.2f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(receiverName, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Online", fontSize = 11.sp, color = Color(0xFF4CAF50))
                    }
                }

                // Delete All Chats Button
                if (messages.isNotEmpty()) {
                    IconButton(onClick = { showDeleteChatDialog = true }) {
                        Icon(Icons.Default.Delete, null, tint = Color(0xFFF44336), modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Chat, null, tint = Color.Gray, modifier = Modifier.size(56.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Start chatting with $receiverName", color = Color.Gray, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            Text("Type a message below", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                items(messages) { msg ->
                    val isMyMessage = msg.senderId == SahayakGlobalEngine.currentUser?.userId
                    ChatBubbleWithDelete(
                        message = msg.message,
                        isMyMessage = isMyMessage,
                        senderName = if (!isMyMessage) msg.senderName else null,
                        time = msg.timestamp,
                        onDelete = {
                            if (isMyMessage) {
                                selectedMessage = msg
                                showDeleteDialog = true
                            }
                        }
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFFF0F0F0))
                    ) {
                        BasicTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            decorationBox = { innerTextField ->
                                if (messageText.isEmpty()) {
                                    Text(
                                        "Type a message...",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                if (messageText.isNotBlank() && !isSending) {
                                    isSending = true
                                    SahayakGlobalEngine.sendMessage(receiverId, receiverName, messageText)
                                    messageText = ""
                                    scope.launch {
                                        listState.animateScrollToItem(messages.size)
                                        delay(300)
                                        isSending = false
                                    }
                                }
                            },
                        shape = CircleShape,
                        color = Color(0xFFFFB300)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isSending) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Send, null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }

    // Delete Single Message Dialog
    if (showDeleteDialog && selectedMessage != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Message", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this message?") },
            confirmButton = {
                Button(
                    onClick = {
                        SahayakGlobalEngine.deleteMessage(selectedMessage!!.messageId)
                        showDeleteDialog = false
                        selectedMessage = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Entire Chat Dialog
    if (showDeleteChatDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteChatDialog = false },
            title = { Text("Delete Chat", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete all messages with $receiverName? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        SahayakGlobalEngine.deleteAllMessagesWithUser(receiverId)
                        showDeleteChatDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteChatDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatBubbleWithDelete(
    message: String,
    isMyMessage: Boolean,
    senderName: String?,
    time: Long,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            if (!isMyMessage && senderName != null) {
                Text(senderName, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 2.dp))
            }
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isMyMessage) 16.dp else 4.dp,
                    bottomEnd = if (isMyMessage) 4.dp else 16.dp
                ),
                color = if (isMyMessage) Color(0xFFFFB300) else Color.White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(message, color = if (isMyMessage) Color.Black else Color.Black, fontSize = 14.sp)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(time)),
                            fontSize = 9.sp,
                            color = if (isMyMessage) Color.Black.copy(alpha = 0.5f) else Color.Gray
                        )
                        if (isMyMessage) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapScreenFunction() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(16.dp)
    ) {
        Text("🗺️ LPU Campus Map", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Real-time request locations", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            LPUCampusMapView(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun SettingsScreenTabFunction(onSettings: () -> Unit, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(16.dp)
    ) {
        Text("⚙️ Settings", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("👤 Profile", fontWeight = FontWeight.Bold)
                Text("Name: ${SahayakGlobalEngine.currentUser?.name ?: "Guest"}")
                Text("Reg No: ${SahayakGlobalEngine.currentUser?.regNo ?: "GUEST"}")
                Text("Email: ${SahayakGlobalEngine.currentUser?.email ?: "Not set"}")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📊 Statistics", fontWeight = FontWeight.Bold)
                Text("Tasks Posted: ${SahayakGlobalEngine.getUserRequests().size}")
                Text("Tasks Completed: ${SahayakGlobalEngine.getAllCompletedRequests().size}")
                Text("Credits: ${SahayakGlobalEngine.userCredits}")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.School, null, tint = Color(0xFFFFB300), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sahayak - Campus Care Connect", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Developed by Sachin Kumar Singh", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                Text("Lovely Professional University • Project", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("© 2025", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                    Text(" • ", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                    Text("Version 2.0", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                    Text(" • ", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                    Text("All Rights Reserved", color = Color.White.copy(alpha = 0.5f), fontSize = 9.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}