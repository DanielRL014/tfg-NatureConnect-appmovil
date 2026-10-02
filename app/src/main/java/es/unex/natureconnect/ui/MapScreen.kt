package es.unex.natureconnect.ui

import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.ktx.MapsExperimentalFeature
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.viewModel.MapViewModel
import es.unex.natureconnect.viewModel.MapViewModelFactory
import kotlinx.coroutines.tasks.await


/**
 * Map screen showing every publication as a marker.
 *
 * Renders a search bar with a family filter dialog on top and the bottom
 * navigation below. Tapping a marker opens the publication detail.
 *
 * @param navController Controller used to navigate to other screens.
 * @param viewModel View model that provides the publications and filters.
 */
@OptIn(MapsExperimentalFeature::class)
@Composable
fun MapScreen(navController: NavController, viewModel: MapViewModel = viewModel(factory = MapViewModelFactory())) {
    val publicaciones by viewModel.publicaciones.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val familias by viewModel.familias.collectAsState(initial = emptyList())
    var showFilterDialog by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar =  { SearchBarMap(
            textoBusqueda = textoBusqueda,viewModel,
            onTextoCambio = { viewModel.actualizarTextoBusqueda(it) },
            onFilterClick = { showFilterDialog = true }
        )  },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        if (!error.isNullOrEmpty()) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Mapa(
                publicaciones = publicaciones,
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }

        if (showFilterDialog) {
            FilterDialog(
                familias = familias,
                onDismiss = { showFilterDialog = false },
                onApply = { selectedFamily ->
                    showFilterDialog = false
                    viewModel.filtrarPorFamilia(selectedFamily)
                }
            )
        }
    }
}

/**
 * Google Map that plots the given publications.
 *
 * Requests the location permissions at runtime and centres the camera on the
 * last known location, falling back to a default coordinate.
 *
 * @param publicaciones Publications to plot as markers.
 * @param modifier Modifier applied to the map.
 * @param navController Controller used to open a publication detail.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Mapa(publicaciones: List<Publicacion>, modifier: Modifier, navController: NavController) {
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    val context = LocalContext.current
    var location by remember { mutableStateOf<Location?>(null) }
    val mapProperties = remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }
    val uiSettings = remember { mutableStateOf(MapUiSettings()) }
    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    if (locationPermissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            try {
                location = fusedLocationProviderClient.lastLocation.await()
            } catch (e: SecurityException) {
                Log.e("Mapa", "No se puede acceder a la ubicación: ${e.message}")
            }
        }
    } else {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Se requieren permisos de ubicación para mostrar el mapa.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                Text("Otorgar permisos")
            }
        }
    }
    Spacer(modifier = Modifier.height(26.dp))

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(location?.latitude ?: 38.881409, location?.longitude ?: -6.976360), 18f
        )
    }
    if (locationPermissionsState.allPermissionsGranted) {

        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
            properties = mapProperties.value,
            uiSettings = uiSettings.value,
            cameraPositionState = cameraPositionState,

        ) {

            publicaciones.forEach { publicacion ->
                val lat = publicacion.latitud.toDoubleOrNull()
                val lng = publicacion.longitud.toDoubleOrNull()

                if (lat != null && lng != null) {
                    Marker(
                        state = MarkerState(position = LatLng(lat, lng)),
                        onClick = {

                            navController.navigate("detalle/${publicacion.idPublicacion}")
                            true
                        }
                        )
                }
            }
        }
    }
}
@Composable
/**
 * Top search bar of the map screen, with a trigger for the filter dialog.
 *
 * @param textoBusqueda Text currently shown in the search field.
 * @param viewModel View model that runs the search.
 * @param onTextoCambio Callback invoked when the text changes.
 * @param onFilterClick Callback invoked when the filter button is tapped.
 */
fun SearchBarMap(textoBusqueda: String, viewModel: MapViewModel, onTextoCambio: (String) -> Unit,onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF55A458)).padding(top = 30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.IconButton(onClick = {
            viewModel.buscarAve()
        }) {
            Icon(
                imageVector = Icons.Default.Search ,
                contentDescription = "busqueda",
                modifier = Modifier.background(Color(0xFF55A458)),
                tint = Color(0xFF5D4037)

                )
        }
        TextField(
            value = textoBusqueda,
            onValueChange = onTextoCambio,
            placeholder = { Text("Buscar publicaciones...",color = Color(0xFF5D4037)) },
            modifier = Modifier
                .weight(1f)
                .height(56.dp).background(Color(0xFF55A458)),
            singleLine = true
        )
        androidx.compose.material.Button(
            onClick = onFilterClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF55A458))
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "filtar",
                modifier = Modifier.background(Color(0xFF55A458)),
                tint = Color(0xFF5D4037)
            )
        }
    }
}







