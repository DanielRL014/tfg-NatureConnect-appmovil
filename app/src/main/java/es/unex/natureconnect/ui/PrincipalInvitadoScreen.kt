package es.unex.natureconnect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.unex.natureconnect.viewModel.PrincipalViewModel
import es.unex.natureconnect.viewModel.PrincipalViewModelFactory


@Composable
/**
 * Guest version of the main feed screen.
 *
 * Lists the publications exactly like the signed-in feed but uses the guest
 * bottom navigation, so no account is required.
 *
 * @param navController Controller used to navigate to other screens.
 * @param viewModel View model that provides the publications and filters.
 */
fun PrincipalInvitadoScreen(navController: NavController, viewModel: PrincipalViewModel = viewModel(factory = PrincipalViewModelFactory(navController))) {
    val publicaciones by viewModel.publicaciones.collectAsState(initial = emptyList())
    val familias by viewModel.familias.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()
    Scaffold(
        topBar = {  SearchBarIn(
            textoBusqueda = textoBusqueda,viewModel,
            onTextoCambio = { viewModel.actualizarTextoBusqueda(it) },
            onFilterClick = { showFilterDialog = true }
        )  },
        bottomBar = { BottomNavigationBarInvitado(navController) }
    ) { innerPadding ->
        if (!error.isNullOrEmpty()) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Publicaciones(
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


@Composable
/**
 * Bottom navigation bar available to guest users.
 *
 * @param navController Controller used to perform the navigation.
 */
fun BottomNavigationBarInvitado( navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF55A458),
        modifier = Modifier.padding(bottom  =20.dp)
    ) {
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("homeInvitado") },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio",tint = Color(0xFF5D4037)) },
            label = { Text("Inicio") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {navController.navigate("mapInvitado")  },
            icon = { Icon(imageVector = Icons.Default.Place, contentDescription = "Mapa",tint = Color(0xFF5D4037)) },
            label = { Text("Mapa") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("IAMandarInvitado") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "IA"
                    ,tint = Color(0xFF5D4037)
                )
            },
            label = { Text("IA") }
        )

    }
}
@Composable
/**
 * Top search bar of the guest feed, with a trigger for the filter dialog.
 *
 * @param textoBusqueda Text currently shown in the search field.
 * @param viewModel View model that runs the search.
 * @param onTextoCambio Callback invoked when the text changes.
 * @param onFilterClick Callback invoked when the filter button is tapped.
 */
fun SearchBarIn(textoBusqueda: String,viewModel: PrincipalViewModel ,onTextoCambio: (String) -> Unit,onFilterClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF55A458)).padding(top=30.dp),
        verticalAlignment = Alignment.CenterVertically,

    ) {
        androidx.compose.material3.IconButton(onClick = {
            viewModel.buscarAve()
        }) {
            Icon(
                imageVector = Icons.Default.Search ,
                contentDescription = "busqueda",
                modifier = Modifier.background(Color(0xFF55A458)).padding(top=20.dp),
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
        Button(onClick = onFilterClick,
            colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))) {
            Icon(
                imageVector = Icons.Default.Menu ,
                contentDescription = "filtar",
                modifier = Modifier.background(Color(0xFF55A458)),
                tint = Color(0xFF5D4037)
            )
        }
    }
}

