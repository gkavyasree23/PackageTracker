package kavya.project.packagetracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


//this is the old home screen code

data class PackageItem(
    val id: Int,
    val title: String,
    val trackingId: String,
    val status: String // "In Transit", "Out for Delivery", "Delivered"
)



@Preview(showBackground = true)
@Composable
fun PackageHomeScreenOldPreview() {
    PackageHomeScreenOld(navController = NavHostController(LocalContext.current))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageHomeScreenOld(navController: NavHostController) {

    val packages = remember {
        mutableStateOf(
            listOf(
                PackageItem(1, "Laptop Order", "TRK12345", "In Transit"),
                PackageItem(2, "Phone Case", "TRK22345", "Out for Delivery"),
                PackageItem(3, "Shoes", "TRK99999", "Delivered"),
                PackageItem(4, "Keyboard", "TRK77777", "In Transit")
            )
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("In Transit", "Out for Delivery", "Delivered")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Package Tracker") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(AppRoutes.TrackPackage.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // ----- DASHBOARD BOX -----
            DashboardSection(packages.value)

            Spacer(modifier = Modifier.height(16.dp))

            // ----- TABS -----
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ----- FILTERED LIST -----
            val filteredList = packages.value.filter { it.status == tabs[selectedTab] }

            LazyColumn {
                items(filteredList) { pkg ->
                    PackageListItem(pkg = pkg, onClick = {})
                }
            }
        }
    }
}

// -------------------- COMPONENTS ----------------------

@Composable
fun DashboardSection(list: List<PackageItem>) {

    val total = list.size
    val inTransit = list.count { it.status == "In Transit" }
    val delivered = list.count { it.status == "Delivered" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            DashboardRow(label = "Total Packages", value = total.toString(), icon = Icons.Default.Home)
            Spacer(modifier = Modifier.height(6.dp))

            DashboardRow(label = "In Transit", value = inTransit.toString(), icon = Icons.Default.LocationOn)
            Spacer(modifier = Modifier.height(6.dp))

            DashboardRow(label = "Delivered", value = delivered.toString(), icon = Icons.Default.CheckCircle)
        }
    }
}

@Composable
fun DashboardRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold)
    }
}

// ---------------------- LIST ITEM ----------------------

@Composable
fun PackageListItem(pkg: PackageItem, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(pkg.title, style = MaterialTheme.typography.titleMedium)

                Text(
                    text = "Tracking: ${pkg.trackingId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            StatusChip(status = pkg.status)
        }
    }
}

// ---------------------- STATUS CHIP ----------------------

@Composable
fun StatusChip(status: String) {
    val (color, icon) = when (status) {
        "In Transit" -> Color(0xFF1E88E5) to Icons.Default.PlayArrow
        "Out for Delivery" -> Color(0xFFFFA000) to Icons.Default.LocationOn
        else -> Color(0xFF2E7D32) to Icons.Default.Check
    }

    AssistChip(
        onClick = {},
        label = { Text(status, color = Color.White) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color.White) },
        colors = AssistChipDefaults.assistChipColors(containerColor = color)
    )
}