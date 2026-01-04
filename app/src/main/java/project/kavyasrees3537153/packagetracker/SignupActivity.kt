package project.kavyasrees3537153.packagetracker


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterScreen(navController: NavHostController) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    val genderOptions = listOf("Male", "Female", "Other")
    var selectedGender by remember { mutableStateOf("Male") }

    val context = LocalContext.current.findActivity()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_package_tracking),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.dp, Color.LightGray, CircleShape)
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Hey,\nRegister Now!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    leadingIcon = {
                        Icon(Icons.Default.AccountCircle, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = dob,
                    onValueChange = { dob = it },
                    label = { Text("Date of Birth (dd-mm-yyyy)") },
                    leadingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "DOB Icon")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("dd-mm-yyyy") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Select Gender",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                genderOptions.forEach { gender ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        RadioButton(
                            selected = (gender == selectedGender),
                            onClick = { selectedGender = gender }
                        )
                        Text(text = gender)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {

                    if (fullName.isEmpty()) {
                        Toast.makeText(context, "Enter Full Name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (dob.isEmpty()) {
                        Toast.makeText(context, "Enter Date of Birth", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val dobRegex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
                    if (!dob.matches(dobRegex)) {
                        Toast.makeText(context, "Invalid DOB format. Use dd-mm-yyyy", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    try {
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        val date = LocalDate.parse(dob, formatter)

                        // Age must be at least 13 (optional)
                        val age = Period.between(date, LocalDate.now()).years
                        if (age < 13) {
                            Toast.makeText(context, "You must be at least 13 years old", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                    } catch (e: Exception) {
                        Toast.makeText(context, "Enter a valid calendar date", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (password.isEmpty()) {
                        Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (password != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val userData = UserData(
                        name = fullName,
                        email = email,
                        dob = dob,
                        password = password
                    )


                    val db = FirebaseDatabase.getInstance()
                    val ref = db.getReference("SignedUpUsers")
                    ref.child(userData.email.replace(".", ",")).setValue(userData)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()


                                navController.navigate(AppRoutes.Login.route){
                                    popUpTo(AppRoutes.Register.route) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "User Registration Failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                context,
                                "User Registration Failed: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text("Register", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?", color = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Login",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.clickable {

                        navController.navigate(AppRoutes.Login.route){
                            popUpTo(AppRoutes.Register.route) { inclusive = true }
                        }

                    }
                )
            }
        }
    }
}


data class UserData
    (
    var name: String = "",
    var dob: String ="",
    var email: String ="",
    var password: String ="",
)

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = NavHostController(LocalContext.current))
}