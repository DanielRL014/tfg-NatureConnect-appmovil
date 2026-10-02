package es.unex.natureconnect.ui.navigation


import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import es.unex.natureconnect.ui.DeteccionResponseInvitadoScreen
import es.unex.natureconnect.ui.DeteccionResponseScreen
import es.unex.natureconnect.ui.IAmandarInvitadoScreen
import es.unex.natureconnect.ui.IAmandarScreen
import es.unex.natureconnect.ui.LoginScreen
import es.unex.natureconnect.ui.MapInvitadoScreen
import es.unex.natureconnect.ui.MapScreen
import es.unex.natureconnect.ui.NotificacionesScreen
import es.unex.natureconnect.ui.NuevaPublicacionScreen
import es.unex.natureconnect.ui.PerfilScreen
import es.unex.natureconnect.ui.PrincipalInvitadoScreen
import es.unex.natureconnect.ui.PrincipalScreen
import es.unex.natureconnect.ui.PublicacionScreen
import es.unex.natureconnect.ui.RegisterScreen
import es.unex.natureconnect.ui.SeleccionarAvesScreen
import es.unex.natureconnect.ui.SeleccionarEtiquetasScreen
import es.unex.natureconnect.ui.SeleccionarUbicacionScreen
import es.unex.natureconnect.vi.NuevaPublicacionViewModel
import es.unex.natureconnect.viewModel.IaViewModel
import es.unex.natureconnect.viewModel.IaViewModelFactory
import es.unex.natureconnect.viewModel.NuevaPublicacionViewModelFactory


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController) {

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val sharedPreferences: SharedPreferences = application.getSharedPreferences("NatureConnectPrefs", Context.MODE_PRIVATE)
    val idU: String? = sharedPreferences.getString("id", null)
    var startdestination="login"
    var IaviewModel: IaViewModel = viewModel(factory = IaViewModelFactory(application))
    var viewModel: NuevaPublicacionViewModel= viewModel(factory = NuevaPublicacionViewModelFactory( application))

    if(idU != null){
        startdestination="home"
    }

    NavHost(navController = navController, startDestination = startdestination) {
        composable("login") {
            LoginScreen(application,navController,onLoginSuccess = {
                navController.navigate("home")
            })
        }
        composable("register") {
            RegisterScreen(application,onLoginSuccess = {
                navController.navigate("home")
            })
        }
        composable("home") {
            PrincipalScreen(navController)
        }
        composable("homeInvitado") {
            PrincipalInvitadoScreen(navController)
        }
        composable("detalle/{idPublicacion}") { backStackEntry ->
            val idPublicacion = backStackEntry.arguments?.getString("idPublicacion") ?: ""
            PublicacionScreen(navController,application)
        }
        composable("map") {
            MapScreen(navController)

        }
        composable("mapInvitado") {
            MapInvitadoScreen(navController)

        }
        composable("NuevaPublicacion") {
            viewModel= viewModel(factory = NuevaPublicacionViewModelFactory( application))
            NuevaPublicacionScreen(navController,application,viewModel)
        }
        composable("NuebaPublicacionUbicacion") {
            SeleccionarUbicacionScreen(navController,application,viewModel)
        }
        composable("SeleccionarAves") {
            SeleccionarAvesScreen(navController,viewModel)
        }
        composable("SeleccionarEtiqueta") {
            SeleccionarEtiquetasScreen(navController,viewModel)
        }
        composable("Perfil") {
            PerfilScreen(application,navController)
        }
        composable("notificaciones") {
            NotificacionesScreen(navController,application)
        }
        composable("home/{texto}") { backStackEntry ->
            val texto = backStackEntry.arguments?.getString("texto") ?: ""
            PrincipalScreen(navController)
        }
        composable("homeInvitado/{texto}") { backStackEntry ->
            val texto = backStackEntry.arguments?.getString("texto") ?: ""
            PrincipalInvitadoScreen(navController)
        }
        composable("IAMandar") {
            IaviewModel = viewModel(factory = IaViewModelFactory(application))
            IAmandarScreen(navController,application,IaviewModel)
        }
        composable("ia_resultados") {

            DeteccionResponseScreen(navController,IaviewModel)
        }
        composable("IAMandarInvitado") {
            IaviewModel = viewModel(factory = IaViewModelFactory(application))
            IAmandarInvitadoScreen(navController,application,IaviewModel)
        }
        composable("ia_resultadosInvitados") {

            DeteccionResponseInvitadoScreen(navController,IaviewModel)
        }
    }
}




