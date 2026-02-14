package tech.alt255.research.presentation

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import tech.alt255.research.data.local.SecurePrefs
import tech.alt255.research.presentation.notifications.AlarmScheduler
import tech.alt255.research.presentation.screens.auth.AuthScreen
import tech.alt255.research.presentation.screens.home.HomeScreen
import tech.alt255.research.presentation.screens.quest.QuestPreviewScreen
import tech.alt255.research.presentation.screens.quest.QuestStepScreen
import tech.alt255.research.presentation.screens.rating.RatingScreen
import tech.alt255.research.presentation.screens.profile.EditProfileScreen
import tech.alt255.research.presentation.screens.user.UserScreen
import tech.alt255.research.presentation.ui.theme.ReSearchTheme
import tech.alt255.research.presentation.viewmodels.auth.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var securePrefs: SecurePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            ReSearchTheme {
                AppNavigation(securePrefs = securePrefs)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().apply {
                    action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    data = android.net.Uri.parse("package:$packageName")
                    startActivity(this)
                }
            }
        }
        AlarmScheduler.scheduleDailyAlarm(this)
    }
}

@Composable
fun AppNavigation(securePrefs: SecurePrefs) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val savedUserId = remember { securePrefs.getUserId() }
    val savedToken = remember { securePrefs.getToken() }
    val isLoggedIn = remember { securePrefs.isLoggedIn() }

    val startDestination = if (isLoggedIn && savedToken != null && savedUserId != 0) {
        "home/$savedUserId/$savedToken"
    } else {
        "auth"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("auth") {
            val authViewModel: AuthViewModel = hiltViewModel()
            AuthScreen(
                onLoginSuccess = { userId, email ->
                    securePrefs.saveCredentials(email, "", userId)
                    val token = securePrefs.getToken()
                    if (token != null) {
                        navController.navigate("home/$userId/$token") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                },
                onNavigateToHome = { userId, email ->
                    securePrefs.saveCredentials(email, "", userId)
                    val token = securePrefs.getToken()
                    if (token != null) {
                        navController.navigate("home/$userId/$token") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            "home/{userId}/{token}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val token = backStackEntry.arguments?.getString("token") ?: ""

            HomeScreen(
                userId = userId,
                token = token,
                onNavigateToQuest = { questId ->
                    navController.navigate("quest/preview/$questId/$userId/$token")
                },
                onNavigateToUser = {
                    navController.navigate("user/$userId/$token")
                },
                onNavigateToRating = {
                    navController.navigate("rating/$userId/$token")
                }
            )
        }

        composable(
            "user/{userId}/{token}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val token = backStackEntry.arguments?.getString("token") ?: ""

            UserScreen(
                userId = userId,
                token = token,
                onNavigateToQuest = { questId ->
                    navController.navigate("quest/preview/$questId/$userId/$token")
                },
                onNavigateToEditProfile = {
                    navController.navigate("edit_profile/$userId/$token")
                },
                onNavigateToHome = {
                    navController.navigate("home/$userId/$token") {
                        popUpTo("home/$userId/$token") { inclusive = true }
                    }
                },
                onNavigateToRating = {
                    navController.navigate("rating/$userId/$token")
                }
            )
        }

        composable(
            "quest/preview/{questId}/{userId}/{token}",
            arguments = listOf(
                navArgument("questId") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId") ?: 0
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val token = backStackEntry.arguments?.getString("token") ?: ""

            QuestPreviewScreen(
                questId = questId,
                userId = userId,
                token = token,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToQuestStep = { stepQuestId, stepNumber ->
                    navController.navigate("quest/step/$stepQuestId/$stepNumber/$userId/$token")
                }
            )
        }

        composable(
            "quest/step/{questId}/{stepNumber}/{userId}/{token}",
            arguments = listOf(
                navArgument("questId") { type = NavType.IntType },
                navArgument("stepNumber") { type = NavType.IntType },
                navArgument("userId") { type = NavType.IntType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val questId = backStackEntry.arguments?.getInt("questId") ?: 0
            val stepNumber = backStackEntry.arguments?.getInt("stepNumber") ?: 1
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val token = backStackEntry.arguments?.getString("token") ?: ""

            QuestStepScreen(
                questId = questId,
                stepNumber = stepNumber,
                userId = userId,
                token = token,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStepCompleted = { isFinalStep, reward ->
                    if (isFinalStep) {
                        navController.navigate("home/$userId/$token") {
                            popUpTo("quest/preview/$questId/$userId/$token") { inclusive = true }
                        }
                    } else {
                        val nextStep = stepNumber + 1
                        navController.navigate("quest/step/$questId/$nextStep/$userId/$token") {
                            popUpTo("quest/step/$questId/$stepNumber/$userId/$token") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(
            "edit_profile/{userId}/{token}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val token = backStackEntry.arguments?.getString("token") ?: ""

            EditProfileScreen(
                userId = userId,
                token = token,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    securePrefs.logout()
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            "rating/{userId}/{token}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val token = backStackEntry.arguments?.getString("token") ?: ""
            RatingScreen(
                userId = userId,
                token = token,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}