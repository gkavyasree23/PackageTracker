package kavya.project.packagetracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase

@Preview(showBackground = true)
@Composable
fun TrackPackageScreenPreview() {
    TrackPackageScreen(navController = NavHostController(LocalContext.current))
}

@Composable
fun TrackPackageScreen(navController: NavHostController) {

    var trackingNumber by remember { mutableStateOf("") }
    var trackingInfo by remember { mutableStateOf<TrackingInfo?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

//    val dbRef = FirebaseDatabase.getInstance().getReference("PackageData")


    if(PackageData.trackingNumber.isNotEmpty())
    {
        trackingNumber= PackageData.trackingNumber

        FirebaseDatabase.getInstance().getReference("PackageData").child(trackingNumber)
            .get()
            .addOnSuccessListener { snapshot ->
                loading = false
                if (snapshot.exists()) {
                    trackingInfo = snapshot.getValue(TrackingInfo::class.java)
                } else {
                    trackingInfo = null
                    error = "Tracking Not Found"
                }
            }
            .addOnFailureListener { e ->
                loading = false
                error = e.message ?: "Error"
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Track Package", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = trackingNumber,
            onValueChange = { trackingNumber = it },
            label = { Text("Enter Tracking Number") },
            placeholder = { Text("e.g. UKPKG1001") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (trackingNumber.isEmpty()) {
                    error = "Please enter tracking number"
                    return@Button
                }

                error = ""
                loading = true

                FirebaseDatabase.getInstance().getReference("PackageData").child(trackingNumber)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        loading = false
                        if (snapshot.exists()) {
                            trackingInfo = snapshot.getValue(TrackingInfo::class.java)
                        } else {
                            trackingInfo = null
                            error = "Tracking Not Found"
                        }
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        error = e.message ?: "Error"
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Track")
        }

        Spacer(Modifier.height(20.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        if (error.isNotEmpty()) {
            Text(error, color = Color.Red)
        }

        trackingInfo?.let { info ->

            Spacer(Modifier.height(10.dp))

            Text("Status: ${info.status}", fontWeight = FontWeight.Bold)
            Text("Last Location: ${info.lastLocation}")
            Text("ETA: ${info.eta}")

            Spacer(Modifier.height(16.dp))

            Text("Tracking History:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(info.history) { event ->
                    SimpleHistoryItem(event)
                }
            }
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
