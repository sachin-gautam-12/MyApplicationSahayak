package com.example.myapplicationsahayak

import android.net.Uri
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

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

object SahayakGlobalEngine {
    var currentUser by mutableStateOf<UserProfile?>(null)
    var userCredits by mutableStateOf(0)
    var isPushEnabled by mutableStateOf(true)
    var selectedCampus by mutableStateOf("LPU Jalandhar Main Hub")

    val allRequests = mutableStateListOf<HelpRequest>()
    val notificationHistory = mutableStateListOf<String>()
    var unreadNotificationCount by mutableStateOf(0)

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

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
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                snapshot?.let {
                    allRequests.clear()
                    for (document in it.documents) {
                        val request = document.toObject(HelpRequest::class.java)
                        request?.let { allRequests.add(it) }
                    }
                }
            }
    }

    fun addHelpRequest(request: HelpRequest, onComplete: (Boolean) -> Unit) {
        db.collection("requests").document(request.requestId).set(request)
            .addOnSuccessListener {
                playNotificationSound("🔔 New Request: ${request.title}")
                loadRequests()
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
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
                playNotificationSound("✅ ${helperName} accepted your request!")
                loadRequests()
            }
    }

    fun completeRequest(requestId: String) {
        db.collection("requests").document(requestId).update("status", "COMPLETED")
            .addOnSuccessListener {
                userCredits += 5
                currentUser?.credits = userCredits
                db.collection("users").document(auth.currentUser?.uid ?: "").update("credits", userCredits)
                playNotificationSound("🎉 Task Completed! +5 credits (Total: $userCredits)")
                loadRequests()
            }
    }

    fun uploadImage(uri: Uri, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.child("profile_images/$fileName")

        ref.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                }.addOnFailureListener {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
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

    fun playNotificationSound(message: String) {
        addNotification(message)
    }

    fun addNotification(message: String) {
        notificationHistory.add(0, message)
        unreadNotificationCount++
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

    fun getUserCompletedRequests(): List<HelpRequest> {
        return allRequests.filter { it.status == "COMPLETED" && it.helperId == currentUser?.regNo }
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