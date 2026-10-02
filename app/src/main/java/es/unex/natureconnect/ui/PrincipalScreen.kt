package es.unex.natureconnect.ui
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.viewModel.PrincipalViewModel
import es.unex.natureconnect.viewModel.PrincipalViewModelFactory

@Composable
fun PrincipalScreen(navController: NavController, viewModel: PrincipalViewModel = viewModel(factory = PrincipalViewModelFactory(navController))) {
    val publicaciones by viewModel.publicaciones.collectAsState(initial = emptyList())
    val familias by viewModel.familias.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()
    Scaffold(
        topBar = { SearchBar(
            textoBusqueda = textoBusqueda,viewModel,
            onTextoCambio = { viewModel.actualizarTextoBusqueda(it) },
            onFilterClick = { showFilterDialog = true }
        ) },
        bottomBar = { BottomNavigationBar(navController) }
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
fun Publicaciones(publicaciones: List<Publicacion>, modifier: Modifier, navController: NavController) {

    if (publicaciones.isEmpty()) {
        Text(
            text = "No hay publicaciones disponibles",
            style = MaterialTheme.typography.h6,
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        )
    } else {
        LazyColumn(
            modifier = modifier.padding(8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(publicaciones) { publicacion ->
                PublicacionCard(publicacion = publicacion) {
                    navController.navigate("detalle/${publicacion.idPublicacion}")
                }
            }
        }
    }
}

@Composable
fun PublicacionCard(publicacion: Publicacion, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp).background(Color(0xFFCFE3EC)),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = "http://192.168.1.44:8080/publicaciones/"+publicacion.idFoto+".jpg",
                contentDescription = null ,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { onClick() },
                contentScale = ContentScale.Crop
            )



            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    tint = Color(0xFF5D4037)
                )
                Text(
                    text = publicacion.latitud + ", " + publicacion.longitud,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 8.dp)
                    ,color = Color(0xFF5D4037)

                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar( navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF55A458),
        modifier=Modifier.padding(bottom  =20.dp)
    ) {
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Inicio",tint = Color(0xFF5D4037)) },
            label = { Text("Inicio") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("map") },
            icon = { Icon(imageVector = Icons.Default.Place, contentDescription = "Mapa",tint = Color(0xFF5D4037)) },
            label = { Text("Mapa") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("NuevaPublicacion") },
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar",tint = Color(0xFF5D4037)) },
            label = { Text("Publicar") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("IAMandar") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "IA"
                    ,tint = Color(0xFF5D4037)
                )
            },
            label = { Text("IA") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("notificaciones") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones"
                    ,tint = Color(0xFF5D4037)
                )
            },
            label = { Text("Notificaciones") }
        )
        BottomNavigationItem(
            selected = false,
            onClick = { navController.navigate("Perfil") },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Perfil",tint = Color(0xFF5D4037)) },
            label = { Text("Perfil") }
        )
    }
}
    @Composable
    fun SearchBar(textoBusqueda: String,viewModel: PrincipalViewModel ,onTextoCambio: (String) -> Unit,onFilterClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF55A458)).padding(top=30.dp),
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

@Composable
fun FilterDialog(
    familias: List<String>,
    onDismiss: () -> Unit,
    onApply: (String) -> Unit
) {
    var selectedFamily by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Selecciona una Familia",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(familias.size) { index ->
                        val family = familias[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFamily = family }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFamily == family,
                                onClick = { selectedFamily = family }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = family)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedFamily?.let { onApply(it) }
                            ?: Toast.makeText(
                                context,
                                "Por favor selecciona una familia",
                                Toast.LENGTH_SHORT
                            ).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Aplicar")
                }
            }
        }
    }
}


