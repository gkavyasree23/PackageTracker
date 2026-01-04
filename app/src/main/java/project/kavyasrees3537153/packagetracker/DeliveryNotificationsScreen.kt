package project.kavyasrees3537153.packagetracker


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import project.kavyasrees3537153.packagetracker.data.DatabaseProvider
import project.kavyasrees3537153.packagetracker.data.SavedPackage
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageDeliveryNotificationsScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).savedPackageDao()

    var list by remember { mutableStateOf<List<SavedPackage>>(emptyList()) }

    LaunchedEffect(Unit) {
        dao.getAllPackages().collect {
            list = filterTodayTomorrow(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upcoming Deliveries") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        if (list.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No deliveries today or tomorrow 🎉")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(list) { pkg ->
                    NotificationItem(pkg)
                }
            }
        }
    }
}


fun filterTodayTomorrow(list: List<SavedPackage>): List<SavedPackage> {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return list.filter {
        try {
            val eta = LocalDate.parse(it.eta, formatter)
            eta == today || eta == today.plusDays(1)
        } catch (e: Exception) {
            false
        }
    }
}


@Composable
fun NotificationItem(pkg: SavedPackage) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3BD))
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(pkg.name, fontWeight = FontWeight.Bold)

            Text(
                text = "Tracking: ${pkg.trackingNumber}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Expected delivery: ${pkg.eta}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
