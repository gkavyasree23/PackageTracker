package project.kavyasrees3537153.packagetracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import project.kavyasrees3537153.packagetracker.data.DatabaseProvider
import project.kavyasrees3537153.packagetracker.data.SavedPackage
import project.kavyasrees3537153.packagetracker.ui.theme.App_C1
import project.kavyasrees3537153.packagetracker.ui.theme.App_C1_Light
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPackagesScreen(navController: NavHostController) {

    val context = LocalContext.current
    val dao = DatabaseProvider.getDatabase(context).savedPackageDao()
    val scope = rememberCoroutineScope()

    val allPackages by dao.getAllPackages().collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }

    val statusFilters = listOf(
        "All",
        "Shipped",
        "In Transit",
        "Delivering Soon",
        "Delivered"
    )


    var selectedFilter by remember { mutableStateOf("All") }

    val filteredPackages = allPackages.filter { pkg ->

        val matchesSearch =
            pkg.name.contains(searchQuery, true) ||
                    pkg.trackingNumber.contains(searchQuery, true)

        val delivered = isDeliveredByEta(pkg.eta)
        val deliveringSoon = isDeliveringSoon(pkg.eta)

        val matchesFilter = when (selectedFilter) {
            "Shipped" -> pkg.status == "Shipped" && !delivered
            "In Transit" -> pkg.status == "In Transit" && !delivered
            "Delivering Soon" -> deliveringSoon && !delivered
            "Delivered" -> delivered
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Packages") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = App_C1)
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name or tracking ID") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp)
            )

            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(statusFilters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = App_C1_Light
                        ),
                        border = BorderStroke(1.dp, Color.Gray)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(filteredPackages) { pkg ->
                    SavedPackageItemUpdated(
                        pkg = pkg,
                        onDelete = {
                            scope.launch(Dispatchers.IO) {
                                dao.deletePackage(pkg)
                            }
                        },
                        onEdit = { newName ->
                            scope.launch(Dispatchers.IO) {
                                dao.updateName(pkg.trackingNumber, newName)
                            }
                        },
                        onTrackNow = {
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
fun SavedPackageItemUpdated(
    pkg: SavedPackage,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit,
    onTrackNow: () -> Unit
) {

    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(pkg.name) }

    var showDeleteDialog by remember { mutableStateOf(false) }


    val delivered = isDeliveredByEta(pkg.eta)

    val statusText = when {
        delivered -> "Delivered"
        isDeliveringSoon(pkg.eta) -> "Delivering Soon"
        else -> pkg.status
    }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = R.drawable.ic_package_tracking),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(pkg.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(pkg.trackingNumber, color = Color.Gray, fontSize = 13.sp)
                }

                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, null)
                }

                IconButton(onClick = {
                    showDeleteDialog = true
                }) {
                    Icon(Icons.Default.Delete, null)
                }
            }

            Spacer(Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .background(
                        if (delivered) Color(0xFFDFF5E1) else App_C1_Light,
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onTrackNow,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = App_C1)
            ) {
                Icon(Icons.Filled.TrackChanges, null, tint = Color.Black)
                Spacer(Modifier.width(6.dp))
                Text("Track Now", color = Color.Black)
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Package Name") },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onEdit(editedName)
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Package") },
            text = {
                Text("Are you sure you want to delete this package?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}


fun isDeliveredByEta(eta: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val etaDate = LocalDate.parse(eta, formatter)
        !etaDate.isAfter(LocalDate.now())
    } catch (e: Exception) {
        false
    }
}


fun isDeliveringSoon(eta: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val etaDate = LocalDate.parse(eta, formatter)
        val today = LocalDate.now()

        etaDate == today || etaDate == today.plusDays(1)
    } catch (e: Exception) {
        false
    }
}
