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

class MainActivity : ComponentActivity() {

    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }
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

