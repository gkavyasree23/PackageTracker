package kavya.project.packagetracker

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import kavya.project.packagetracker.data.DatabaseProvider
import kavya.project.packagetracker.data.SavedPackage
import kavya.project.packagetracker.ui.theme.App_C1
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java

@Preview(showBackground = true)
@Composable
fun TrackPackageScreenPreview() {
    TrackPackageScreen(navController = NavHostController(LocalContext.current))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackPackageScreen(navController: NavHostController) {

    var trackingNumber by remember { mutableStateOf(PackageData.trackingNumber) }
    var trackingInfo by remember { mutableStateOf<TrackingInfo?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveName by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).savedPackageDao()


    // Load from Firebase once
    LaunchedEffect(trackingNumber) {
        if (trackingNumber.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference("PackageData")
                .child(trackingNumber)
                .get()
                .addOnSuccessListener { snapshot ->
                    loading = false
                    if (snapshot.exists()) {
                        trackingInfo = snapshot.getValue(TrackingInfo::class.java)
                    } else error = "Tracking Not Found"
                }
                .addOnFailureListener { e ->
                    loading = false
                    error = e.message ?: "Error loading data"
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSaveDialog = true  }) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = App_C1   // mint green like screenshot
                )
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (loading) {
                CircularProgressIndicator()
                return@Column
            }

            if (error.isNotEmpty()) {
                Text(error, color = Color.Red)
                return@Column
            }

            trackingInfo?.let { info ->

                // Tracking number
                Text(
                    text = trackingNumber,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // 🌿 Green Summary Card Like Screenshot
                SummaryCard(info)

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                LazyColumn {
                    items(info.history) { event ->
                        TimelineItem(event)
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
                            placeholder = { Text("Enter a name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (saveName.isNotEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    dao.insertPackage(
                                        SavedPackage(
                                            name = saveName,
                                            trackingNumber = trackingNumber,
                                            status = trackingInfo?.status ?: "Unknown"
                                        )
                                    )
                                }
                                Toast.makeText(context, "Package saved : $saveName", Toast.LENGTH_SHORT).show()
                                showSaveDialog = false
                                saveName = ""
                            }
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
}


@Composable
fun SummaryCard(info: TrackingInfo) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = App_C1  // green tone matching screenshot
        ),
        shape = RoundedCornerShape(14.dp)
    ) {

        Column(Modifier.padding(18.dp)) {

            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("from", color = Color.Black.copy(alpha = 0.7f))
                    Text("China, Shanghai", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Column(Modifier.weight(1f)) {
                    Text("to", color = Color.Black.copy(alpha = 0.7f))
                    Text("UK, ${info.lastLocation}", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {

                Column(Modifier.weight(1f)) {
                    Text("status", color = Color.Black.copy(alpha = 0.7f))
                    Text(info.status, color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Column(Modifier.weight(1f)) {
                    Text("ETA", color = Color.Black.copy(alpha = 0.7f))
                    Text(info.eta, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun TimelineItem(event: TrackEvent) {

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {

        // Dot + Line
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(12.dp)
                    .background(App_C1, CircleShape)
            )
            Spacer(Modifier.height(6.dp))
            Box(
                Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(Color.LightGray)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(event.status, fontWeight = FontWeight.Bold)
            Text(event.date, fontSize = 13.sp, color = Color.Gray)
            Text(event.location, fontSize = 13.sp, color = Color.Gray)
        }
    }
}


@Composable
fun SimpleHistoryItem(event: TrackEvent) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.date, fontWeight = FontWeight.Bold)
            Text(event.status)
            Text("Location: ${event.location}")
        }
    }
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
