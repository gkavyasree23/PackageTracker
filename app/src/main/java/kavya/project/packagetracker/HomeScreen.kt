package kavya.project.packagetracker


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.outlined.ArrowRightAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Preview(showBackground = true)
@Composable
fun PackageHomeScreenPreview() {
    PackageHomeScreen(navController = NavHostController(LocalContext.current))
}

@Composable
fun PackageHomeScreen(
    navController: NavHostController
) {

    var trackingNo by remember { mutableStateOf("") }
    val context = LocalContext.current

    val userName = UserPrefs.getReporterName(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
    )
    {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween


        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(36.dp)
            )




            Icon(
                imageVector = Icons.Filled.CircleNotifications,
                contentDescription = "Notification Icon",
                modifier = Modifier
                    .size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Hello , $userName",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFE48A)
            )
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                // Title
                Text(
                    text = "Track Your Package",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ), color = Color(0xFF000000)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = "Enter the receipt number that has been given by the officer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6C6C6C)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Text Input
                OutlinedTextField(
                    value = trackingNo,
                    onValueChange = { input ->
                        trackingNo = input.uppercase()   // Force only CAPS
                    },
                    placeholder = { Text("ENTER TRACKING NUMBER") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(25.dp)
                )


                Spacer(modifier = Modifier.height(18.dp))

                // Track Now Button
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0A2533)
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(text = "Track Now", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Outlined.ArrowRightAlt,
                        contentDescription = "Track Now"
                    )

//                    painterResource(id = R.drawable.baseline_arrow_right_alt_24),
                }
                Spacer(modifier = Modifier.height(20.dp))

            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            modifier = Modifier.clickable {
                navController.navigate(AppRoutes.SavedPackages.route)
            },
            text = "Manage Tracking",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )

        )
        Spacer(modifier = Modifier.height(12.dp))

        TrackingItemCard(
            R.drawable.ic_package_tracking,
            "Saved Packages",
            "See list of your saved packages",
            onClick = {
                navController.navigate(AppRoutes.SavedPackages.route)
            }
        )
//        TrackingItemCard(R.drawable.ic_package_tracking, "UKPKG1002", "Order reached at hub")


    }
}

@Composable
fun TrackingItemCard(imageid: Int, trackingid: String, orderstatus: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                onClick()
            },
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
                    text = trackingid,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = orderstatus,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Filled.ArrowForwardIos,
                contentDescription = "Right Arrow",
                modifier = Modifier.size(36.dp)
            )
        }
    }

}


