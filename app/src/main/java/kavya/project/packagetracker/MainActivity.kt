package kavya.project.packagetracker


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppNavGraph()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyAppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash.route
    ) {
        composable(AppRoutes.Splash.route) {
            PackageTrackerStartScreen(navController = navController)
        }

        composable(AppRoutes.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(AppRoutes.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(AppRoutes.ForgotPassword.route) {
            ResetPasswordScreen(navController = navController)
        }

        composable(AppRoutes.Home.route) {
            PackageHomeScreen(navController = navController)
        }

        composable(AppRoutes.TrackPackage.route) {
            TrackPackageScreen(navController = navController)
        }


    }

}


@Composable
fun PackageTrackerStartScreen(navController: NavController) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000)

        if(UserPrefs.checkLoginStatus(context))
        {
            navController.navigate(AppRoutes.Home.route) {
                popUpTo(AppRoutes.Splash.route) {
                    inclusive = true
                }
            }
        }else{
            navController.navigate(AppRoutes.Login.route) {
                popUpTo(AppRoutes.Splash.route) {
                    inclusive = true
                }
            }
        }

    }

    WelComeScreenDesign()
}


@Composable
fun WelComeScreenDesign() {
    val context = LocalContext.current

    // Animations
    val logoScale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textOffsetY = remember { Animatable(50f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        textOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, delayMillis = 300)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo with Zoom Animation
            Image(
                painter = painterResource(id = R.drawable.ic_package_tracking),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(
                        scaleX = logoScale.value,
                        scaleY = logoScale.value
                    )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 'By' Text with Slide and Fade Animation
            Text(
                text = "By",
                color = Color.Gray,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = textOffsetY.value.dp)
                    .alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Kavya Sree",
                color = Color.Black,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .offset(y = textOffsetY.value.dp)
                    .alpha(textAlpha.value)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelComeScreenDesignPreview() {
    WelComeScreenDesign()
}