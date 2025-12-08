package kavya.project.packagetracker

import android.R.attr.text
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kavya.project.packagetracker.data.DatabaseProvider
import kavya.project.packagetracker.data.SavedPackage
import kavya.project.packagetracker.ui.theme.App_C1
import kavya.project.packagetracker.ui.theme.App_C1_Light
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPackagesScreen(navController: NavHostController) {

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).savedPackageDao()

    val allPackages by dao.getAllPackages().collectAsState(initial = emptyList())

    // Filter options
    val statusFilters = listOf("All", "Confirmed", "Shipped", "In Transit", "Arrived at Hub")
    var selectedFilter by remember { mutableStateOf("All") }

    // Apply filter
    val filteredPackages = if (selectedFilter == "All") {
        allPackages
    } else {
        allPackages.filter { it.status == selectedFilter }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Packages") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
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
                .padding(16.dp)
        ) {

            // ⭐ Status Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(statusFilters) { filter ->

                    val isSelected = selectedFilter == filter

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 13.sp) },

                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = App_C1_Light,   // ★ your light color
                            selectedLabelColor = Color.Black,
                            labelColor = Color.Black
                        ),

                        border = if (isSelected) null else BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier.height(40.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ⭐ List of filtered items
            LazyColumn {
                items(filteredPackages) { pkg ->

                    SavedPackageItem(
                        pkg = pkg,
                        onDelete = {
                            CoroutineScope(Dispatchers.IO).launch {
                                dao.deletePackage(pkg)
                            }
                        },
                        onClick = {
                            PackageData.trackingNumber = pkg.trackingNumber
                            navController.navigate(AppRoutes.TrackPackage.route)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SavedPackageItem(
    pkg: SavedPackage,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_package_tracking),
                contentDescription = "Order Status",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(pkg.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(pkg.trackingNumber, color = Color.Gray, fontSize = 13.sp)

                // Status badge
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(App_C1_Light, shape = RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(pkg.status, color = Color.Black, fontSize = 12.sp)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}



@Composable
fun SavedPackageItemOld(
    imageid: Int, pkg: SavedPackage,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFE48A) // Yellow background INSIDE the Card
        )
    ) {

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // ---------------- LEFT IMAGE ----------------
            Image(
                painter = painterResource(id = imageid),
                contentDescription = "Order Status",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ---------------- MIDDLE 2-COLUMN TEXT ----------------
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pkg.trackingNumber,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = pkg.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text(
                    text = pkg.status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }



            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(36.dp).clickable {
                    onDelete()
                }
            )

        }
    }

}


@Composable
fun SavedPackageItemOld(
    pkg: SavedPackage,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(Modifier.weight(1f)) {
                Text(pkg.name, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
                Text(pkg.trackingNumber, color = Color.Gray)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
