package project.kavyasrees3537153.packagetracker

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.*
import project.kavyasrees3537153.packagetracker.data.DatabaseProvider
import project.kavyasrees3537153.packagetracker.data.SavedPackage
import project.kavyasrees3537153.packagetracker.ui.theme.App_C1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackPackageScreen(navController: NavHostController) {

    var trackingNumber by remember { mutableStateOf(PackageData.trackingNumber) }
    var etaNumber by remember { mutableStateOf(PackageData.trackingNumber) }

    var trackingInfo by remember { mutableStateOf<TrackingInfo?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).savedPackageDao()
    val scope = rememberCoroutineScope()

    LaunchedEffect(trackingNumber) {
        FirebaseDatabase.getInstance()
            .getReference("PackageData")
            .child(trackingNumber)
            .get()
            .addOnSuccessListener { snapshot ->
                loading = false
                if (snapshot.exists()) {

                    val newInfo = snapshot.getValue(TrackingInfo::class.java)
                    trackingInfo = newInfo

                    etaNumber=trackingInfo!!.eta

                    newInfo?.let { info ->

                        scope.launch(Dispatchers.IO) {

                            val lastSaved =
                                dao.getPackageByTrackingNumber(trackingNumber)

                            if (lastSaved != null && lastSaved.status != info.status) {

                                Log.e("Test","Status changed notification")

                                NotificationHelper.showNotification(
                                    context,
                                    "Package Update",
                                    "Your package $trackingNumber is now ${info.status}"
                                )

                                dao.updateStatus(trackingNumber, info.status)
                            }

                            if (info.status == "Arrived at Hub") {
                                NotificationHelper.showNotification(
                                    context,
                                    "Delivered 🎉",
                                    "Your package $trackingNumber has been delivered"
                                )
                            }
                        }
                    }

                }
            }
            .addOnFailureListener {
                loading = false
                error = "Failed to load tracking"
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Package") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = App_C1)
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (error.isNotEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(error, color = Color.Red)
            }
            return@Scaffold
        }

        trackingInfo?.let { info ->

            val currentStatus = calculateCurrentStatus(info.history)
            val progress = calculateProgress(info.history)

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                Text(
                    text = trackingNumber,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                if (isDelivered(info.eta)) {
                    Spacer(Modifier.height(6.dp))
                    DeliveredBadge()
                }

                Spacer(Modifier.height(12.dp))

                SummaryCardWithProgress(
                    info = info,
                    progress = progress,
                    currentStatus = currentStatus
                )

                Spacer(Modifier.height(16.dp))

                PackageMapAnimation()

                Spacer(Modifier.height(16.dp))

                Text("History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(info.history) { event ->
                        TimelineItem(event)
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Package")
                }
            }
        }


        if (showSaveDialog) {
            AlertDialog(
                onDismissRequest = { showSaveDialog = false },
                title = { Text("Save Package") },
                text = {
                    OutlinedTextField(
                        value = saveName,
                        onValueChange = { saveName = it },
                        placeholder = { Text("Enter package name") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch(Dispatchers.IO) {
                            dao.insertPackage(
                                SavedPackage(
                                    name = saveName,
                                    trackingNumber = trackingNumber,
                                    status = trackingInfo?.status ?: "Unknown",
                                    eta = etaNumber
                                )
                            )
                        }
                        Toast.makeText(context, "Saved successfully", Toast.LENGTH_SHORT).show()
                        showSaveDialog = false
                        saveName = ""
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SummaryCardWithProgress(
    info: TrackingInfo,
    progress: Float,
    currentStatus: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = App_C1),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("FROM", fontSize = 12.sp)
                    Text("China, Shanghai", fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text("TO", fontSize = 12.sp)
                    Text("UK, ${info.lastLocation}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("STATUS", fontSize = 12.sp)
                    Text(currentStatus, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text("ETA", fontSize = 12.sp)
                    Text(info.eta, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.Black,
                trackColor = Color.White
            )

            Spacer(Modifier.height(4.dp))
            Text("${(progress * 100).toInt()}% Delivered", fontSize = 12.sp)
        }
    }
}

@Composable
fun PackageMapAnimation() {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(52.3555, -1.1743),
            6f
        )
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        cameraPositionState = cameraPositionState
    ) {
        val route = listOf(
            LatLng(51.5074, -0.1278),
            LatLng(52.4862, -1.8904),
            LatLng(53.4808, -2.2426)
        )

        Polyline(points = route, color = Color.Blue, width = 6f)
        route.forEach { Marker(state = MarkerState(it)) }
    }
}

@Composable
fun TimelineItem(event: TrackEvent) {

    val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    val today = LocalDate.now()
    val eventDate = LocalDate.parse(event.date, apiFormatter)

    if (eventDate.isAfter(today.plusDays(7))) return

    val isToday = eventDate.isEqual(today)
    val isFuture = eventDate.isAfter(today)

    val dotColor = when {
        eventDate.isBefore(today) -> Color(0xFF2E7D32)
        isToday -> Color(0xFFF57C00)
        else -> Color.Gray
    }

    val dotScale by animateFloatAsState(
        targetValue = if (isToday) 1.4f else 1f,
        animationSpec = tween(durationMillis = 600),
        label = "dotScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isFuture) 0.5f else 1f,
        label = "fade"
    )

    val displayStatus = getDisplayStatus(
        originalStatus = event.status,
        eventDate = eventDate,
        today = today
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .alpha(alpha)
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .scale(dotScale)
                    .background(dotColor, CircleShape)
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(36.dp)
                    .background(Color.LightGray)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {

            Row(verticalAlignment = Alignment.CenterVertically) {

                StatusIcon(
                    status = event.status,
                    eventDate = eventDate,
                    today = today,
                    color = dotColor
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = displayStatus,
                    fontWeight = FontWeight.Bold,
                    color = dotColor
                )
            }

            Text(
                text = eventDate.format(displayFormatter),
                fontSize = 13.sp,
                color = Color.Gray
            )

            Text(
                text = event.location,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun DeliveredBadge() {
    Box(
        modifier = Modifier
            .background(Color(0xFF2E7D32), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "DELIVERED",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}


fun calculateCurrentStatus(history: List<TrackEvent>): String {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return history.lastOrNull {
        val d = LocalDate.parse(it.date, formatter)
        !d.isAfter(today)
    }?.status ?: "Order Created"
}

fun calculateProgress(history: List<TrackEvent>): Float {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val completed = history.count {
        val d = LocalDate.parse(it.date, formatter)
        !d.isAfter(today)
    }
    return completed.toFloat() / history.size
}

data class TrackingInfo(
    val status: String = "",
    val lastLocation: String = "",
    val eta: String = "",
    val history: List<TrackEvent> = emptyList()
)

data class TrackEvent(
    val date: String = "",
    val status: String = "",
    val location: String = ""
)

fun getDisplayStatus(
    originalStatus: String,
    eventDate: LocalDate,
    today: LocalDate
): String {
    return when {
        eventDate.isBefore(today) -> originalStatus
        eventDate.isEqual(today) -> "$originalStatus (Today)"
        else -> when (originalStatus) {
            "Arrived at Hub" -> "Expected at Hub"
            else -> "Expected $originalStatus"
        }
    }
}


fun isDelivered(eta: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val etaDate = LocalDate.parse(eta, formatter)
        val today = LocalDate.now()

        !etaDate.isAfter(today)

    } catch (e: Exception) {
        false
    }
}


@Composable
fun StatusIcon(
    status: String,
    eventDate: LocalDate,
    today: LocalDate,
    color: Color
) {
    val icon = when {
        eventDate.isBefore(today) -> Icons.Filled.CheckCircle
        eventDate.isEqual(today) -> Icons.Filled.Schedule
        status == "Order Created" -> Icons.Filled.Inventory2
        else -> Icons.Filled.LocalShipping
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(18.dp)
    )
}

