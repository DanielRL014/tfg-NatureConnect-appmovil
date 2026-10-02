package es.unex.natureconnect

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import es.unex.natureconnect.ui.navigation.NavGraph
import es.unex.natureconnect.ui.theme.NatureConnectTheme
import es.unex.natureconnect.vi.NuevaPublicacionViewModel
import es.unex.natureconnect.viewModel.NuevaPublicacionViewModelFactory

/**
 * Entry point of the application.
 *
 * Sets the Compose content with the app theme and the root navigation graph.
 */
class MainActivity : ComponentActivity() {

    /** Firebase Analytics instance, initialised on first access. */
    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }
    /**
     * Creates the activity and installs the Compose content.
     *
     * Requires API 26 or higher because [NavGraph] hosts the notifications
     * screen, which relies on `java.time.LocalDate`.
     *
     * @param savedInstanceState Saved instance state, or `null` on first creation.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NatureConnectTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}

