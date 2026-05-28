# 🤝 Sahayak - Campus Care Connect

**Sahayak** is an Android application developed for LPU (Lovely Professional University) students. The main purpose of this app is to provide a platform where students can help each other on campus.

---

## 📌 Project Overview

| Detail | Information |
|--------|-------------|
| **Project Name** | Sahayak - Campus Care Connect |
| **Developer** | Sachin Kumar Singh |
| **University** | Lovely Professional University |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Backend** | Firebase |
| **Minimum SDK** | API 24 (Android 7.0) |
| **Target SDK** | API 36 (Android 16) |

---

## 🎯 What This App Does?

With this app, students can:

| Feature | Explanation |
|---------|-------------|
| 📝 **Request Help** | Any student can request help (e.g., notes, lab coat, calculator, etc.) |
| 🤝 **Accept Help** | Other students can accept help requests |
| 💬 **Real-time Chat** | Helper and requester can chat with each other |
| 📍 **Campus Map** | See where help is needed on the campus map |
| ⭐ **Credit System** | Earn +5 credits for each completed task |
| 🔔 **Notifications** | Get notifications when someone accepts your request |
| 👤 **Profile** | Edit profile and upload photo |

---

## 🛠️ Technologies Used

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Main programming language |
| **Jetpack Compose** | UI design (modern Android UI toolkit) |
| **Firebase Auth** | Login / Signup system |
| **Firebase Firestore** | Database (stores requests, users, messages) |
| **Firebase Storage** | Stores images (profile photos, request images) |
| **Firebase Messaging (FCM)** | Push notifications |
| **OpenStreetMap (OSMDroid)** | Campus map (free alternative to Google Maps) |
| **Coil** | Loads images |

---
MyApplicationSahayak2/
├── MainActivity.kt # Navigation
├── AppComponents.kt # Splash + Login/Signup
├── MainDashboardScreen.kt # Home screen
├── SeekHelpScreen.kt # Request form
├── AcceptHelpScreen.kt # Accept requests
├── MyRequestsScreen.kt # Activity logs
├── ProfileScreen.kt # User profile
├── SettingsScreen.kt # App info + logout
├── ChatScreen.kt # Real-time messaging
├── HelpModels.kt # Data + Firebase
└── ui/theme/Theme.kt # App theme

text

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| Kotlin | Main language |
| Jetpack Compose | UI |
| Firebase Auth | Login/Signup |
| Firebase Firestore | Database |
| Firebase Storage | Images |
| Firebase Messaging | Notifications |
| OpenStreetMap | Campus map |
| Coil | Image loading |

---

## 📦 Key Dependencies

```kotlin
// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

// Compose
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.material3:material3")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-storage-ktx")
implementation("com.google.firebase:firebase-messaging-ktx")

// Map
implementation("org.osmdroid:osmdroid-android:6.1.17")

// Image & Icons
implementation("io.coil-kt:coil-compose:2.5.0")
implementation("androidx.compose.material:material-icons-extended:1.6.0")
🚀 Installation
bash
# Clone
git clone https://github.com/sachin-gautam-12/MyApplicationSahayak.git

# Open in Android Studio
File → Open → Select project

# Sync Gradle
File → Sync Project with Gradle Files

# Add google-services.json (from Firebase Console) to app/ folder

# Build & Run
Build → Clean Project → Rebuild Project → Run 'app'


## 📂 File Structure
