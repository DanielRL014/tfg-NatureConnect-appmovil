package es.unex.natureconnect.ui

import android.app.Application
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import es.unex.natureconnect.vi.NuevaPublicacionViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NuevaPublicacionScreen(
    navController: NavController,application: Application,
    viewModel: NuevaPublicacionViewModel
) {
    val error by viewModel.error.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateSelectedImage(uri)
    }

    Scaffold(
        topBar = { TopAppBar(title = { androidx.compose.material.Text("Nueva Publicación",color = Color(0xFF5D4037)) },
            backgroundColor = Color(0xFF55A458),
            modifier = Modifier.padding(top=30.dp)
        )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .size(200.dp)

                )
            } else {
                Text(
                    text = "No se ha seleccionado ninguna imagen",

                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch("image/*") },

                colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))) {
                Text("Seleccionar Imagen",color = Color(0xFF5D4037))
            }

            Spacer(modifier = Modifier.height(16.dp))

            botonUbicacionActual(viewModel,navController)

            Spacer(modifier = Modifier.height(16.dp))
            val locationPermissionsState = rememberMultiplePermissionsState(
                listOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
            if (locationPermissionsState.allPermissionsGranted) {
            Button(onClick = { navController.navigate("NuebaPublicacionUbicacion")  },
                colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))) {
                Text("Seleccionar Ubicación",color = Color(0xFF5D4037))
            }
            }else {
                Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest()
                    if(locationPermissionsState.allPermissionsGranted) {
                        navController.navigate("NuebaPublicacionUbicacion")
                    }
                }  ,
                    colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))) {
                    Text("Seleccionar Ubicación",color = Color(0xFF5D4037))
            }
        }
    }
}
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun botonUbicacionActual(viewModel: NuevaPublicacionViewModel,navController: NavController) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    val context = LocalContext.current

    if (locationPermissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            viewModel.obtenerUbicacionActual(context)

        }
        Button(onClick = { navController.navigate("SeleccionarAves") },colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))) {
            Text("Usar ubicacion Actual",color = Color(0xFF5D4037))
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() },
                colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))) {
                Text("Usar ubicacion Actuals",color = Color(0xFF5D4037))
            }
        }
    }
}





