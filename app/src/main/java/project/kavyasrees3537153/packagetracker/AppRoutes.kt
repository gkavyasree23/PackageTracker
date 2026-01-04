package project.kavyasrees3537153.packagetracker

sealed class AppRoutes(val route: String) {
    object Splash : AppRoutes("splash_route")
    object Login : AppRoutes("login_route")
    object Home : AppRoutes("home_route")
    object Register : AppRoutes("register_route")
    object ForgotPassword : AppRoutes("forgot_password")

    object TrackPackage : AppRoutes("track_package")
    object SavedPackages : AppRoutes("saved_package")

    object Notifications : AppRoutes("notifications")
    object Profile : AppRoutes("profile")
    object AboutUs : AppRoutes("aboutus")

}