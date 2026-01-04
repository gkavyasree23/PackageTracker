package project.kavyasrees3537153.packagetracker

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.ArrowRightAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import project.kavyasrees3537153.packagetracker.data.DatabaseProvider
import project.kavyasrees3537153.packagetracker.data.SavedPackage
import project.kavyasrees3537153.packagetracker.ui.theme.App_C1
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Preview(showBackground = true)
@Composable
fun PackageHomeScreenPreview() {
    PackageHomeScreen(navController = NavHostController(LocalContext.current))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageHomeScreen(
    navController: NavHostController
) {

    var trackingNo by remember { mutableStateOf("") }
    val context = LocalContext.current

    val userName = TrackingAccountData.getReporterName(context)

    val dao = DatabaseProvider.getDatabase(context).savedPackageDao()
    var savedPackages by remember { mutableStateOf<List<SavedPackage>>(emptyList()) }


    val todayTomorrowCount = remember(savedPackages) {
        countTodayTomorrowDeliveries(savedPackages)
    }


    LaunchedEffect(Unit) {
        dao.getAllPackages().collectLatest {
            savedPackages = it
        }
    }

    val analytics = calculateAnalytics(savedPackages)


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Package Tracker",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        IconButton(
                            onClick = {
                                navController.navigate(AppRoutes.Profile.route)
                            }
                        ) {
                            Icon(
                                Icons.Filled.AccountCircle, null, modifier = Modifier
                                    .size(36.dp)
                            )
                        }

                    }
                },

                actions = {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        IconButton(
                            onClick = {
                                navController.navigate(AppRoutes.AboutUs.route)
                            }
                        ) {
                            Icon(
                                Icons.Filled.Info, null, modifier = Modifier
                                    .size(36.dp)
                            )
                        }

                        BadgedBox(
                            badge = {
                                if (todayTomorrowCount > 0) {
                                    Badge {
                                        Text(todayTomorrowCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CircleNotifications,
                                contentDescription = "Notifications",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        navController.navigate(AppRoutes.Notifications.route)
                                    }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = App_C1
                )
            )

        }
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)

        ) {

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Hello , $userName",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnalyticsCard(analytics)

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE48A))
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Track Your Package",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter the receipt number that has been given by the officer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6C6C6C)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = trackingNo,
                        onValueChange = { trackingNo = it.uppercase() },
                        placeholder = { Text("ENTER TRACKING NUMBER") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(25.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (trackingNo.isEmpty()) {
                                Toast.makeText(context, "Enter Tracking Number", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                PackageData.trackingNumber = trackingNo
                                navController.navigate(AppRoutes.TrackPackage.route)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A2533)),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Text("Track Now", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Outlined.ArrowRightAlt, null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                modifier = Modifier.clickable {
                    navController.navigate(AppRoutes.SavedPackages.route)
                },
                text = "Manage Tracking",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TrackingItemCard(
                R.drawable.ic_package_tracking,
                "Saved Packages",
                "See list of your saved packages"
            ) {
                navController.navigate(AppRoutes.SavedPackages.route)
            }
        }
    }
}


@Composable
fun AnalyticsCard(data: AnalyticsData) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE48A))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = "Your Packages",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            AnalyticsRow("Total Packages", data.total, Icons.Default.Inventory2)
            AnalyticsRow("In Transit", data.inTransit, Icons.Default.LocalShipping)
            AnalyticsRow("Delivered", data.delivered, Icons.Default.CheckCircle)
            AnalyticsRow("Pending", data.pending, Icons.Default.Schedule)
        }
    }
}

@Composable
fun AnalyticsRow(label: String, value: Int, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null)
        Spacer(Modifier.width(10.dp))
        Text(label)
        Spacer(Modifier.weight(1f))
        Text(value.toString(), fontWeight = FontWeight.Bold)
    }
}


fun countTodayTomorrowDeliveries(list: List<SavedPackage>): Int {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    return list.count {
        try {
            val etaDate = LocalDate.parse(it.eta, formatter)
            etaDate == today || etaDate == today.plusDays(1)
        } catch (e: Exception) {
            false
        }
    }
}


fun calculateAnalytics(list: List<SavedPackage>): AnalyticsData {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now()

    val total = list.size

    val delivered = list.count { pkg ->
        try {
            val etaDate = LocalDate.parse(pkg.eta, formatter)

            etaDate.isBefore(today) || etaDate.isEqual(today)
        } catch (e: Exception) {
            pkg.status == "Arrived at Hub"
        }
    }

    val inTransit = list.count { pkg ->
        try {
            val etaDate = LocalDate.parse(pkg.eta, formatter)

            etaDate.isAfter(today) && pkg.status == "In Transit"
        } catch (e: Exception) {
            pkg.status == "In Transit"
        }
    }

    val pending = list.count { pkg ->
        try {
            val etaDate = LocalDate.parse(pkg.eta, formatter)

            etaDate.isAfter(today) &&
                    (pkg.status == "Order Created" || pkg.status == "Shipped")
        } catch (e: Exception) {
            pkg.status == "Order Created" || pkg.status == "Shipped"
        }
    }

    return AnalyticsData(
        total = total,
        inTransit = inTransit,
        delivered = delivered,
        pending = pending
    )
}


data class AnalyticsData(
    val total: Int,
    val inTransit: Int,
    val delivered: Int,
    val pending: Int
)

@Composable
fun TrackingItemCard(
    imageid: Int,
    trackingid: String,
    orderstatus: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE48A))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = imageid),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(trackingid, fontWeight = FontWeight.Bold)
                Text(orderstatus, color = Color.Gray)
            }

            Icon(Icons.Filled.ArrowForwardIos, null)
        }
    }
}
