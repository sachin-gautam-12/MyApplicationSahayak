package com.example.myapplicationsahayak

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    receiverId: String = "",
    receiverName: String = "",
    requestId: String = ""
) {
    var selectedChatUser by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showUserList by remember { mutableStateOf(selectedChatUser == null) }

    if (receiverId.isNotEmpty() && selectedChatUser == null) {
        selectedChatUser = Pair(receiverId, receiverName)
        showUserList = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showUserList) {
                        Text("Messages", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    } else {
                        Text(selectedChatUser?.second ?: "Chat", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showUserList) {
                            onBack()
                        } else {
                            showUserList = true
                            selectedChatUser = null
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFB300))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        }
    ) { paddingValues ->
        if (showUserList) {
            ConversationListScreen(
                paddingValues = paddingValues,
                onUserSelected = { userId, userName ->
                    selectedChatUser = Pair(userId, userName)
                    showUserList = false
                }
            )
        } else {
            selectedChatUser?.let { (userId, userName) ->
                ChatDetailScreen2(
                    paddingValues = paddingValues,
                    receiverId = userId,
                    receiverName = userName,
                    requestId = requestId,
                    onBackToConversations = { showUserList = true }
                )
            }
        }
    }
}

@Composable
fun ConversationListScreen(
    paddingValues: PaddingValues,
    onUserSelected: (String, String) -> Unit
) {
    val conversations = SahayakGlobalEngine.conversations
    val currentUserId = SahayakGlobalEngine.currentUser?.userId ?: ""

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color(0xFFF4F6F8))
            .padding(16.dp)
    ) {
        Text("💬 Your Conversations", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        if (conversations.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Chat, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No conversations yet", color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text("When someone accepts your request or you accept someone's request, you can chat here", fontSize = 12.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(conversations) { conv ->
                    val otherUserName = if (conv.participant1Id == currentUserId) conv.participant2Name else conv.participant1Name
                    val otherUserId = if (conv.participant1Id == currentUserId) conv.participant2Id else conv.participant1Id
                    val unreadCount = SahayakGlobalEngine.chatMessages.count {
                        it.senderId == otherUserId && !it.isRead
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onUserSelected(otherUserId, otherUserName) },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFB300).copy(alpha = 0.2f),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, tint = Color(0xFFFFB300), modifier = Modifier.size(28.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(otherUserName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    if (unreadCount > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = Color.Red,
                                            modifier = Modifier.size(18.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("$unreadCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                Text(
                                    conv.lastMessage.take(40),
                                    fontSize = 12.sp,
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
fun ChatDetailScreen2(
    paddingValues: PaddingValues,
    receiverId: String,
    receiverName: String,
    requestId: String,
    onBackToConversations: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = SahayakGlobalEngine.getMessagesWithUser(receiverId)
    val scope = rememberCoroutineScope()
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Mark messages as read when opening chat
    LaunchedEffect(Unit) {
        messages.filter { it.senderId == receiverId && !it.isRead }.forEach { msg ->
            // Update read status in Firebase - implement if needed
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFB300).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(receiverName, color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Online", fontSize = 10.sp, color = Color(0xFF4CAF50))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackToConversations) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFFFFB300))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF4F6F8))
        ) {
            // Chat messages area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                state = listState,
                reverseLayout = false,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Chat, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Start chatting with $receiverName", color = Color.Gray, fontWeight = FontWeight.Medium)
                                Text("Send a message below", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                } else {
                    items(messages) { msg ->
                        val isMyMessage = msg.senderId == SahayakGlobalEngine.currentUser?.userId
                        ChatBubbleMessage(
                            message = msg.message,
                            isMyMessage = isMyMessage,
                            senderName = if (!isMyMessage) msg.senderName else null,
                            time = msg.timestamp
                        )
                    }
                }
            }

            // Message input area
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text("Type a message...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFB300),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )
                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                SahayakGlobalEngine.sendMessage(receiverId, receiverName, messageText, requestId)
                                messageText = ""
                                scope.launch {
                                    listState.animateScrollToItem(messages.size)
                                }
                            }
                        },
                        containerColor = Color(0xFFFFB300),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, null, tint = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleMessage(message: String, isMyMessage: Boolean, senderName: String?, time: Long) {
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
                    Text(message, color = if (isMyMessage) Color.Black else Color.Black, fontSize = 13.sp)
                    Text(
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(time)),
                        fontSize = 9.sp,
                        color = if (isMyMessage) Color.Black.copy(alpha = 0.6f) else Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}