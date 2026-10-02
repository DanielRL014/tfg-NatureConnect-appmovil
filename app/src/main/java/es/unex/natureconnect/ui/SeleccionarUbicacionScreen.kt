package es.unex.natureconnect.ui

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import es.unex.natureconnect.vi.NuevaPublicacionViewModel


@Composable
fun SeleccionarUbicacionScreen(
    navController: NavController,
    application: Application,
    viewModel: NuevaPublicacionViewModel
) {
    val localizacion by viewModel.Location.collectAsState()
    val context = LocalContext.current
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var location by remember { mutableStateOf<LatLng?>(null) }
    val mapProperties = remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val uiSettings = remember { mutableStateOf(MapUiSettings()) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(location?.latitude ?: 38.881409, location?.longitude ?: -6.976360), 18f
        )
    }

    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = { Text("Seleccionar Ubicación", color = Color(0xFF5D4037)) },
                backgroundColor = Color(0xFF55A458),
                modifier = Modifier.padding(top=30.dp)

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    location?.let {
                        viewModel.guardarUbicacionManual(it)
                        navController.navigate("SeleccionarAves")
                    }
                },
                modifier = Modifier.padding(bottom = 16.dp, end = 50.dp),
            ) {
                Icon(Icons.Default.Check, contentDescription = "Seleccionar ubicación")
            }
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = mapProperties.value,
                    uiSettings = uiSettings.value,
                    onMapClick = { latLng ->
                        location = latLng
                    },
                    cameraPositionState = cameraPositionState,
                ) {
                    if (location != null) {
                        Marker(
                            state = MarkerState(position = LatLng(location!!.latitude, location!!.longitude)),
                        )
                    }
                }
            }
        }
    )
}



