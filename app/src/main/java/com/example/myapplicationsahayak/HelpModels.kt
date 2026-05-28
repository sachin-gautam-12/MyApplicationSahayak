package com.example.myapplicationsahayak

import android.net.Uri
import android.content.Context
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

enum class RequestStatus { PENDING, ACCEPTED, COMPLETED }

data class HelpRequest(
    val requestId: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val needType: String = "BASIC",
    val blockNumber: Int = 1,
    val imageUri: String? = null,
    val idCardUri: String? = null,
    val userName: String = "",
    val userRegNo: String = "",
    val userCourse: String = "",
    val userPhone: String = "",
    val userEmail: String = "",
    var status: String = "PENDING",
    val helperName: String = "",
    val helperPhone: String = "",
    val helperId: String = "",
    val handoverLocation: String = "",
    val verificationNote: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class UserProfile(
    var userId: String = "",
    var name: String = "",
    var regNo: String = "",
    var course: String = "",
    var stream: String = "",
    var year: String = "",
    var block: String = "",
    var phone: String = "",
    var email: String = "",
    var photoUrl: String? = null,
    var idCardUrl: String? = null,
    var location: String = "Block 34",
    var credits: Int = 0
)

data class ChatMessage(
    val messageId: String = UUID.randomUUID().toString(),
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class ChatConversation(
    val conversationId: String = "",
    val participant1Id: String = "",
    val participant1Name: String = "",
    val participant2Id: String = "",
    val participant2Name: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val requestId: String = ""
)

object SahayakGlobalEngine {
    var currentUser by mutableStateOf<UserProfile?>(null)
    var userCredits by mutableStateOf(0)
    var isPushEnabled by mutableStateOf(true)
    var selectedCampus by mutableStateOf("LPU Jalandhar Main Hub")

    val allRequests = mutableStateListOf<HelpRequest>()
    val notificationHistory = mutableStateListOf<String>()
    var unreadNotificationCount by mutableStateOf(0)

    val chatMessages = mutableStateListOf<ChatMessage>()
    val conversations = mutableStateListOf<ChatConversation>()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    // Flag to control notification sound
    var shouldPlaySound = false

    fun registerUser(email: String, password: String, profile: UserProfile, onResult: (Boolean, String) -> Unit) {
        if (!isValidEmail(email)) {
            onResult(false, "Invalid email format! Use like: name@lpu.in")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    profile.userId = userId
                    profile.email = email
                    db.collection("users").document(userId).set(profile)
                        .addOnSuccessListener {
                            currentUser = profile
                            userCredits = profile.credits
                            loadRequests()
                            loadConversations()
                            onResult(true, "Registration successful")
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message ?: "Failed to save user")
                        }
                } else {
                    onResult(false, task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        if (!isValidEmail(email)) {
            onResult(false, "Invalid email format!")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                currentUser = document.toObject(UserProfile::class.java)
                                userCredits = currentUser?.credits ?: 0
                                loadRequests()
                                loadConversations()
                                loadMessages()
                                onResult(true, "Login successful")
                            } else {
                                onResult(false, "User data not found")
                            }
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message ?: "Failed to load user")
                        }
                } else {
                    onResult(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    fun loadRequests() {
        db.collection("requests")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    allRequests.clear()
                    for (document in it.documents) {
                        val request = document.toObject(HelpRequest::class.java)
                        request?.let { allRequests.add(it) }
                    }
                }
            }
    }

    fun loadConversations() {
        val userId = currentUser?.userId ?: return
        db.collection("conversations")
            .whereEqualTo("participant1Id", userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    for (document in it.documents) {
                        val conv = document.toObject(ChatConversation::class.java)
                        conv?.let {
                            if (!conversations.any { c -> c.conversationId == it.conversationId }) {
                                conversations.add(it)
                            }
                        }
                    }
                }
            }
        db.collection("conversations")
            .whereEqualTo("participant2Id", userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    for (document in it.documents) {
                        val conv = document.toObject(ChatConversation::class.java)
                        conv?.let {
                            if (!conversations.any { c -> c.conversationId == it.conversationId }) {
                                conversations.add(it)
                            }
                        }
                    }
                }
            }
    }

    fun loadMessages() {
        val userId = currentUser?.userId ?: return
        db.collection("messages")
            .whereEqualTo("senderId", userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    for (document in it.documents) {
                        val msg = document.toObject(ChatMessage::class.java)
                        msg?.let {
                            if (!chatMessages.any { m -> m.messageId == it.messageId }) {
                                chatMessages.add(it)
                                // NO SOUND FOR MESSAGES
                            }
                        }
                    }
                }
            }
        db.collection("messages")
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    for (document in it.documents) {
                        val msg = document.toObject(ChatMessage::class.java)
                        msg?.let {
                            if (!chatMessages.any { m -> m.messageId == it.messageId }) {
                                chatMessages.add(it)
                                // NO SOUND FOR MESSAGES - only add notification without sound flag
                                addNotificationWithoutSound("💬 New message from ${it.senderName}")
                            }
                        }
                    }
                }
            }
    }

    fun sendMessage(receiverId: String, receiverName: String, message: String, requestId: String = "") {
        val senderId = currentUser?.userId ?: return
        val senderName = currentUser?.name ?: "User"

        val newMessage = ChatMessage(
            senderId = senderId,
            senderName = senderName,
            receiverId = receiverId,
            message = message,
            timestamp = System.currentTimeMillis()
        )

        db.collection("messages").document(newMessage.messageId).set(newMessage)
            .addOnSuccessListener {
                chatMessages.add(newMessage)

                val conversationId = if (senderId < receiverId) "${senderId}_${receiverId}" else "${receiverId}_${senderId}"
                val conversation = ChatConversation(
                    conversationId = conversationId,
                    participant1Id = senderId,
                    participant1Name = senderName,
                    participant2Id = receiverId,
                    participant2Name = receiverName,
                    lastMessage = message,
                    lastMessageTime = System.currentTimeMillis(),
                    requestId = requestId
                )

                db.collection("conversations").document(conversationId).set(conversation)
                // NO SOUND FOR SENDING MESSAGES
                addNotificationWithoutSound("📤 Message sent to $receiverName")
            }
    }

    fun getMessagesWithUser(otherUserId: String): List<ChatMessage> {
        val userId = currentUser?.userId ?: return emptyList()
        return chatMessages.filter {
            (it.senderId == userId && it.receiverId == otherUserId) ||
                    (it.senderId == otherUserId && it.receiverId == userId)
        }.sortedBy { it.timestamp }
    }

    // Delete a single message
    fun deleteMessage(messageId: String) {
        db.collection("messages").document(messageId).delete()
            .addOnSuccessListener {
                chatMessages.removeAll { it.messageId == messageId }
                addNotificationWithoutSound("🗑️ Message deleted")
            }
    }

    // Delete all messages with a specific user
    fun deleteAllMessagesWithUser(userId: String) {
        val currentUserId = currentUser?.userId ?: return

        db.collection("messages")
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }

        db.collection("messages")
            .whereEqualTo("senderId", userId)
            .whereEqualTo("receiverId", currentUserId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }

        chatMessages.removeAll { it.senderId == currentUserId && it.receiverId == userId }
        chatMessages.removeAll { it.senderId == userId && it.receiverId == currentUserId }

        val conversationId = if (currentUserId < userId) "${currentUserId}_${userId}" else "${userId}_${currentUserId}"
        db.collection("conversations").document(conversationId).update(
            mapOf(
                "lastMessage" to "Chat cleared",
                "lastMessageTime" to System.currentTimeMillis()
            )
        )

        addNotificationWithoutSound("🗑️ Chat cleared")
    }

    // Delete entire conversation
    fun deleteConversation(conversationId: String, otherUserId: String) {
        val currentUserId = currentUser?.userId ?: return
        deleteAllMessagesWithUser(otherUserId)

        db.collection("conversations").document(conversationId).delete()
            .addOnSuccessListener {
                conversations.removeAll { it.conversationId == conversationId }
                addNotificationWithoutSound("🗑️ Conversation deleted")
            }
    }

    fun addHelpRequest(request: HelpRequest, onComplete: (Boolean) -> Unit) {
        db.collection("requests").document(request.requestId).set(request)
            .addOnSuccessListener {
                shouldPlaySound = true  // Sound for new request
                addNotification("🔔 New Request: ${request.title}")
                loadRequests()
                onComplete(true)
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun acceptRequest(requestId: String, helperName: String, helperPhone: String, helperId: String, handoverLocation: String, note: String) {
        val updates = mapOf(
            "status" to "ACCEPTED",
            "helperName" to helperName,
            "helperPhone" to helperPhone,
            "helperId" to helperId,
            "handoverLocation" to handoverLocation,
            "verificationNote" to note
        )
        db.collection("requests").document(requestId).update(updates)
            .addOnSuccessListener {
                shouldPlaySound = true  // Sound for request acceptance
                addNotification("✅ Request accepted by $helperName")
                loadRequests()
            }
    }

    fun completeRequest(requestId: String) {
        db.collection("requests").document(requestId).update("status", "COMPLETED")
            .addOnSuccessListener {
                userCredits += 5
                currentUser?.credits = userCredits
                db.collection("users").document(auth.currentUser?.uid ?: "").update("credits", userCredits)
                shouldPlaySound = true  // Sound for task completion
                addNotification("🎉 Task Completed! You earned 5 credits")
                loadRequests()
            }
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.child("profile_images/$fileName")
        ref.putFile(uri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { url -> onSuccess(url.toString()) }
            }
            .addOnFailureListener { onFailure() }
    }

    fun updateProfile(profile: UserProfile, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(profile.userId).set(profile)
            .addOnSuccessListener {
                currentUser = profile
                userCredits = profile.credits
                onComplete(true)
            }
            .addOnFailureListener { onComplete(false) }
    }

    fun addNotification(message: String) {
        notificationHistory.add(0, message)
        unreadNotificationCount++
        currentUser?.userId?.let { userId ->
            val notification = mapOf(
                "message" to message,
                "timestamp" to System.currentTimeMillis(),
                "isRead" to false
            )
            db.collection("notifications").document(userId).collection("userNotifications").add(notification)
        }
    }

    fun addNotificationWithoutSound(message: String) {
        notificationHistory.add(0, message)
        unreadNotificationCount++
        shouldPlaySound = false  // No sound for this notification
        currentUser?.userId?.let { userId ->
            val notification = mapOf(
                "message" to message,
                "timestamp" to System.currentTimeMillis(),
                "isRead" to false
            )
            db.collection("notifications").document(userId).collection("userNotifications").add(notification)
        }
    }

    fun getUserRequests(): List<HelpRequest> {
        return allRequests.filter { it.userRegNo == currentUser?.regNo }
    }

    fun getAllPendingRequests(): List<HelpRequest> {
        return allRequests.filter { it.status == "PENDING" }
    }

    fun getAllAcceptedRequests(): List<HelpRequest> {
        return allRequests.filter { it.status == "ACCEPTED" }
    }

    fun getAllCompletedRequests(): List<HelpRequest> {
        return allRequests.filter { it.status == "COMPLETED" }
    }

    fun markNotificationsRead() {
        unreadNotificationCount = 0
    }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun getBlockCoordinates(blockNumber: Int): Pair<Double, Double> {
        val baseLat = 31.2539
        val baseLng = 75.7032
        val row = (blockNumber - 1) / 10
        val col = (blockNumber - 1) % 10
        val offset = 0.0008
        return Pair(baseLat + (row * offset), baseLng + (col * offset))
    }
}