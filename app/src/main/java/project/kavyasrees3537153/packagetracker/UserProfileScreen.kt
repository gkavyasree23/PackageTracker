package project.kavyasrees3537153.packagetracker

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
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
import com.google.firebase.database.FirebaseDatabase
import project.kavyasrees3537153.packagetracker.ui.theme.App_C1
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {

    val context = LocalContext.current
    val email = TrackingAccountData.getReporterEmail(context)
    val userKey = email.replace(".", ",")

    val dbRef = FirebaseDatabase.getInstance()
        .getReference("SignedUpUsers")
        .child(userKey)

    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isEditing by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        dbRef.get().addOnSuccessListener {
            name = it.child("name").value?.toString() ?: ""
            dob = it.child("dob").value?.toString() ?: ""
            password = it.child("password").value?.toString() ?: ""
            loading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
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
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFF0A2533), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(50.dp))
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    if (!isEditing) {
                        ProfileText("Name", name)
                        ProfileText("Email", email)
                        ProfileText("Date of Birth", dob)
                    } else {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = dob,
                            onValueChange = {},
                            label = { Text("Date of Birth") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    showDatePicker(context) { selectedDate ->
                                        dob = selectedDate
                                    }
                                }) {
                                    Icon(Icons.Default.Edit, null)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (!isEditing) {
                Button(
                    onClick = { isEditing = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = App_C1)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Profile", color = Color.Black)
                }
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    Button(
                        onClick = {
                            dbRef.child("name").setValue(name)
                            dbRef.child("dob").setValue(dob)
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = App_C1)
                    ) {
                        Icon(Icons.Default.Save, null,tint = Color.Black)
                        Text("Save",color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = { isEditing = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedButton(
                onClick = { showPasswordDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Lock, null)
                Spacer(Modifier.width(8.dp))
                Text("Change Password")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    TrackingAccountData.markLoginStatus(context,false)
                    navController.navigate(AppRoutes.Login.route) {
                        popUpTo(0)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }
        }
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            oldPassword = password,
            onUpdate = { newPass ->
                dbRef.child("password").setValue(newPass)
                Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                password = newPass
                showPasswordDialog = false
            },
            onDismiss = { showPasswordDialog = false }
        )
    }
}


@Composable
fun ProfileText(label: String, value: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ChangePasswordDialog(
    oldPassword: String,
    onUpdate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var enteredOld by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column {
                OutlinedTextField(
                    value = enteredOld,
                    onValueChange = { enteredOld = it },
                    label = { Text("Old Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    enteredOld != oldPassword ->
                        Toast.makeText(context, "Old password incorrect", Toast.LENGTH_SHORT).show()

                    newPassword.length < 6 ->
                        Toast.makeText(context, "Password must be 6+ characters", Toast.LENGTH_SHORT).show()

                    else -> onUpdate(newPassword)
                }
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


fun showDatePicker(context: android.content.Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, day ->
            onDateSelected(String.format("%02d-%02d-%04d", day, month + 1, year))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
