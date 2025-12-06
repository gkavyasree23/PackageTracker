package kavya.project.packagetracker

import android.content.Context


object UserPrefs {

    private const val PREFS_NAME = "NEWS_POST_PREFS"
    private const val KEY_IS_USER_LOGGED_IN = "KEY_IS_USER_LOGGED_IN"
    private const val KEY_REPORTER_NAME = "KEY_REPORTER_NAME"
    private const val KEY_REPORTER_EMAIL = "KEY_REPORTER_EMAIL"

    fun markLoginStatus(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_USER_LOGGED_IN, isLoggedIn).apply()
    }

    fun checkLoginStatus(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_USER_LOGGED_IN, false)
    }

    fun saveReporterName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_REPORTER_NAME, name).apply()
    }

    fun getReporterName(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_REPORTER_NAME, "") ?: ""
    }

    fun saveReporterEmail(context: Context, email: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_REPORTER_EMAIL, email).apply()
    }

    fun getReporterEmail(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_REPORTER_EMAIL, "") ?: ""
    }
}