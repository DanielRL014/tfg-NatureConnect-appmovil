package es.unex.natureconnect.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.viewModel.PublicacionViewModel
import es.unex.natureconnect.viewModel.PublicacionViewModelFactory

@Composable
/**
 * Publication detail screen.
 *
 * Shows the photo, its birds, tags, location and like counter, using the
 * signed-in or the guest bottom navigation depending on the session.
 *
 * @param navController Controller used to navigate to other screens.
 * @param application Application used to build the view model.
 * @param viewModel View model that loads the publication.
 */
fun PublicacionScreen(navController: NavController, application: Application, viewModel: PublicacionViewModel=viewModel(factory = PublicacionViewModelFactory(application,navController))){
    val publicacion by viewModel.publicacion.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val error by viewModel.error.collectAsState()
    Scaffold(
        topBar = { SearchBarPublicacion(
            textoBusqueda = textoBusqueda,
            viewModel = viewModel,
            onTextoCambio = { viewModel.actualizarTextoBusqueda(it) },
            navController = navController
        ) },
        bottomBar = { if(viewModel.iniciado.collectAsState().value){
            BottomNavigationBar(navController)}else{
                BottomNavigationBarInvitado(navController)
        }
            }
    ) { innerPadding ->
        if (!error.isNullOrEmpty()) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            PublicacionV(
                viewModel = viewModel,
                PublicacionS = publicacion,
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                sesion =viewModel.iniciado.collectAsState()
            )
        }
    }



}


@Composable
/**
 * Content of the publication detail: photo, birds, tags and like button.
 *
 * Guests are redirected to the login screen when they tap the like button.
 *
 * @param viewModel View model that performs the like operations.
 * @param PublicacionS Publication to display, or `null` while loading.
 * @param modifier Modifier applied to the content.
 * @param navController Controller used to navigate or refresh the detail.
 * @param sesion Whether the screen was opened by a signed-in user.
 */
fun PublicacionV(
    viewModel: PublicacionViewModel,
    PublicacionS: Publicacion?, modifier: Modifier, navController: NavController,
    sesion: State<Boolean>
) {
    Column() {
        if (PublicacionS != null) {
            AsyncImage(
                model = "http://192.168.1.44:8080/publicaciones/"+PublicacionS.idFoto+".jpg",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth().height(400.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.width(100.dp)) {
                Text(text = "Aves", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = modifier.padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    if (PublicacionS != null) {
                        if (PublicacionS.avesPublicacions != null) {

                            items(PublicacionS.avesPublicacions) { ave ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = 4.dp
                                ) {
                                    Text(text = ave.idAve.nombreComun, color = Color(0xFF5D4037))
                                }

                            }
                        }
                    }
                }
            }
            Column(modifier = Modifier.width(100.dp)) {
                Text(text = "Etiquetas", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = modifier.padding(8.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    if (PublicacionS != null) {
                        if (PublicacionS.etiquetaPublicacions != null) {

                            items(PublicacionS.etiquetaPublicacions) { Etiqueta ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    elevation = 4.dp
                                ) {
                                    Text(text = Etiqueta.idEtiqueta.nombre, color = Color(0xFF5D4037))
                                }

                            }
                        }
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Ubicación",
                tint = Color(0xFF5D4037)
            )
            if (PublicacionS != null) {
                Text(
                    text = PublicacionS.meGustas.toString(),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (sesion.value) {
                IconButton(onClick = {
                    if (viewModel.publicacion.value?.hasLiked ?: false){
                        viewModel.quitarLike()
                        if (PublicacionS != null) {
                            navController.navigate("detalle/${PublicacionS.idPublicacion}")
                        }
                    } else{
                        viewModel.darLike()
                        if (PublicacionS != null) {
                            navController.navigate("detalle/${PublicacionS.idPublicacion}")
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (viewModel.publicacion.value?.hasLiked
                                ?: false
                        ) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (viewModel.publicacion.value?.hasLiked
                                ?: false
                        ) Color.Red else Color(0xFF5D4037)
                    )
                }
            } else {
                IconButton(onClick = { navController.navigate("login") }) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = Color(0xFF5D4037)
                    )
                }
            }
        }
    }
}
    @Composable
    /**
 * Top search bar of the publication detail screen.
 *
 * @param textoBusqueda Text currently shown in the search field.
 * @param navController Controller used to run the search.
 * @param viewModel View model that runs the search.
 * @param onTextoCambio Callback invoked when the text changes.
 */
fun SearchBarPublicacion(
        textoBusqueda: String,
        navController: NavController,
        viewModel: PublicacionViewModel,
        onTextoCambio: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .background(Color(0xFF55A458)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(onClick = {
                viewModel.buscarAve(navController)
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "busqueda",
                    modifier = Modifier.background(Color(0xFF55A458)),
                    tint = Color(0xFF5D4037)
                )
            }
            TextField(
                value = textoBusqueda,
                onValueChange = onTextoCambio,
                placeholder = { Text("Buscar publicaciones...", color = Color(0xFF5D4037)) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp).background(Color(0xFF55A458)),
                singleLine = true
            )
        }
    }



