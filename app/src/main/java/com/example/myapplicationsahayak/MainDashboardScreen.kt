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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onSeekHelp: () -> Unit,
    onAcceptHelp: () -> Unit,
    onMyRequests: () -> Unit,
    onLogout: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showCreditsDialog by remember { mutableStateOf(false) }
    var selectedBlock by remember { mutableStateOf<String?>(null) }

    // Get requested blocks from allRequests
    val requestedBlocks = SahayakGlobalEngine.allRequests.mapNotNull {
        it.location.takeIf { loc -> loc.startsWith("Block") }
    }.distinct()

    // Initialize OSMDroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().setUserAgentValue(context.packageName)
    }

    val thoughts = listOf(
        "\"Campus aid, precise location tracking.\"",
        "\"Help is just a tap away!\"",
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
                    Surface(
                        modifier = Modifier.size(70.dp).clip(CircleShape),
                        color = Color(0xFFFFB300)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.Black, modifier = Modifier.size(45.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(SahayakGlobalEngine.currentUser?.name ?: "Guest", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(SahayakGlobalEngine.currentUser?.regNo ?: "GUEST", color = Color.LightGray, fontSize = 11.sp)
                    Text("Credits: ${SahayakGlobalEngine.userCredits}", color = Color(0xFFFFB300), fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { scope.launch { drawerState.close() }; onProfile() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Profile", color = Color.Black, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { scope.launch { drawerState.close() }; onSettings() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Settings", color = Color.White, fontSize = 12.sp)
                    }

                    Button(
                        onClick = { scope.launch { drawerState.close() }; onLogout() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Logout", color = Color.Red, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Developed by Sachin Singh", color = Color.Gray, fontSize = 9.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.School, null, tint = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text("SAHAYAK APP", color = Color(0xFFFFB300), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("connecting campus care", color = Color.LightGray, fontSize = 9.sp)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, null, tint = Color(0xFFFFB300))
                        }
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showNotificationDialog = true }) {
                                Icon(Icons.Filled.Notifications, null, tint = Color(0xFFFFB300))
                            }
                            if (SahayakGlobalEngine.unreadNotificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        IconButton(onClick = onSettings) {
                            Icon(Icons.Filled.Settings, null, tint = Color(0xFFFFB300))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFFF5EBE1), modifier = Modifier.height(55.dp)) {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Filled.Home, null, modifier = Modifier.size(22.dp)) },
                        label = { Text("Home", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onAcceptHelp,
                        icon = { Icon(Icons.Filled.Share, null, modifier = Modifier.size(22.dp)) },
                        label = { Text("Accept", fontSize = 10.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onMyRequests,
                        icon = { Icon(Icons.AutoMirrored.Filled.List, null, modifier = Modifier.size(22.dp)) },
                        label = { Text("My Req", fontSize = 10.sp) }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF4F6F8))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Map Card
                Card(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    RequestMapView(
                        modifier = Modifier.fillMaxSize(),
                        requestedBlocks = requestedBlocks,
                        onBlockSelected = { block ->
                            selectedBlock = block
                        }
                    )
                }

                // Block Info Bar
                if (selectedBlock != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB300)),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📍 Selected: $selectedBlock", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Icon(Icons.Filled.LocationOn, null, tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Thoughts Box - Smaller
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB300))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 ${thoughts[thoughtIndex]}",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // WITHOUT WEIGHT - Using fillMaxWidth with weight removed
                            NeedBoxSmall("URGENT", Icons.Filled.Warning, Color(0xFFF44336), onSeekHelp)
                            NeedBoxSmall("BASIC", Icons.Filled.Info, Color(0xFF4CAF50), onSeekHelp)
                        }
                    }
                }

                // Stats Row - WITHOUT WEIGHT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCardSmall("Tasks", "${SahayakGlobalEngine.getUserRequests().size}", Icons.Filled.List)
                    StatCardSmall("Done", "${SahayakGlobalEngine.getAllCompletedRequests().size}", Icons.Filled.CheckCircle)
                    StatCardSmall("Credits", "${SahayakGlobalEngine.userCredits}", Icons.Filled.Star, onClick = { showCreditsDialog = true })
                }

                // 4-Grid Layout
                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    item { GridCardSmall("Request Aid", Icons.Filled.AddCircle, Color.White, Color.Black, onSeekHelp) }
                    item { GridCardSmall("Accept Help", Icons.Filled.Share, Color(0xFFFFB300), Color.Black, onAcceptHelp) }
                    item { GridCardSmall("My Requests", Icons.AutoMirrored.Filled.List, Color.White, Color.Black, onMyRequests) }
                    item { GridCardSmall("Earn Credits", Icons.Filled.Star, Color(0xFF1E1E1E), Color(0xFFFFB300)) { showCreditsDialog = true } }
                }
            }
        }
    }

    // Credits Dialog
    if (showCreditsDialog) {
        AlertDialog(
            onDismissRequest = { showCreditsDialog = false },
            title = { Text("💰 Credits Info", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("You have ${SahayakGlobalEngine.userCredits} credits", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("How to earn credits:", fontWeight = FontWeight.Bold)
                    Text("• Complete a task → +5 credits")
                    Text("• Help others → +5 credits per task")
                }
            },
            confirmButton = {
                Button(onClick = { showCreditsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212))) {
                    Text("Got it!", color = Color(0xFFFFB300))
                }
            }
        )
    }

    // Notification Dialog
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("🔔 Notifications", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(250.dp)) {
                    items(SahayakGlobalEngine.notificationHistory.take(15)) { notification ->
                        Text(notification, modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    SahayakGlobalEngine.markNotificationsRead()
                    showNotificationDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF121212))) {
                    Text("Close", color = Color(0xFFFFB300))
                }
            }
        )
    }
}

@Composable
fun RequestMapView(
    modifier: Modifier = Modifier,
    requestedBlocks: List<String>,
    onBlockSelected: (String) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                val center = GeoPoint(31.2539, 75.7032)
                controller.setCenter(center)

                // Add markers ONLY for requested blocks
                for (block in requestedBlocks) {
                    val blockNum = block.replace("Block ", "").toIntOrNull() ?: continue
                    val row = (blockNum - 1) / 10
                    val col = (blockNum - 1) % 10
                    val lat = 31.2539 + (row * 0.0008)
                    val lng = 75.7032 + (col * 0.0008)

                    val marker = Marker(this)
                    marker.position = GeoPoint(lat, lng)
                    marker.title = block
                    marker.snippet = "Help Requested Here"

                    marker.setOnMarkerClickListener { _, _ ->
                        onBlockSelected(block)
                        true
                    }

                    overlays.add(marker)
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun NeedBoxSmall(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(36.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }
}

@Composable
fun StatCardSmall(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(60.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB300))
            Text(title, fontSize = 9.sp, color = Color.Gray)
        }
    }
}

@Composable
fun GridCardSmall(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, bg: Color, fg: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontWeight = FontWeight.Medium, color = fg, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}