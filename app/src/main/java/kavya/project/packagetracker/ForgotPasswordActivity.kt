package kavya.project.packagetracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase


class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordScreen()
        }
    }
}


@Composable
fun ResetPasswordScreen() {

    var email by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var step2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    val context = LocalContext.current.findActivity()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
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
            text = "Reset Password",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(Modifier.height(20.dp))

        // STEP 1 -> EMAIL + DOB
        if (!step2) {

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                label = { Text("Date of Birth (dd-mm-yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    loading = true
                    errorMessage = ""
                    successMessage = ""

                    val key = email.replace(".", ",")

                    FirebaseDatabase.getInstance().getReference("SignedUpUsers").child(key).get()
                        .addOnSuccessListener { snapshot ->
                            loading = false

                            if (!snapshot.exists()) {
                                errorMessage = "User not found"
                                return@addOnSuccessListener
                            }

                            val dbEmail = snapshot.child("email").value?.toString() ?: ""
                            val dbDob = snapshot.child("dob").value?.toString() ?: ""

                            if (dbEmail == email && dbDob == dob) {
                                step2 = true // show new password fields
                            } else {
                                errorMessage = "Email or DOB incorrect"
                            }
                        }
                        .addOnFailureListener {
                            loading = false
                            errorMessage = "Error: ${it.localizedMessage}"
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5) // Nice blue
                )
            ) {
                Text("Verify")
            }
        }

        // STEP 2 -> ENTER NEW PASSWORD
        if (step2) {

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    errorMessage = ""
                    successMessage = ""

                    if (newPassword != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }

                    loading = true

                    val key = email.replace(".", ",")

                    FirebaseDatabase.getInstance().getReference("SignedUpUsers").child(key).child("password").setValue(newPassword)
                        .addOnSuccessListener {
                            loading = false
                            successMessage = "Password updated successfully!"

                            context!!.startActivity(Intent(context, SignInActivity::class.java))
                            context.finish()
                        }
                        .addOnFailureListener {
                            loading = false
                            errorMessage = "Failed to update password"
                        }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5) // Nice blue
                )
            ) {
                Text("Update Password")
            }
        }

        Spacer(Modifier.height(20.dp))

        if (loading) Text("Processing...")

        if (errorMessage.isNotEmpty())
            Text(errorMessage, color = MaterialTheme.colorScheme.error)

        if (successMessage.isNotEmpty())
            Text(successMessage, color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordScreenPreview() {
    ResetPasswordScreen()
}