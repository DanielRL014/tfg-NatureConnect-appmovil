package es.unex.natureconnect.ui

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.like
import es.unex.natureconnect.data.models.notificacion
import es.unex.natureconnect.viewModel.NotificacionesVIewModel
import es.unex.natureconnect.viewModel.NotificacionesVIewModelFactory


/**
 * Notifications screen listing the likes received by the user's publications.
 *
 * Requires API 26 or higher because the underlying view model uses
 * `java.time.LocalDate`.
 *
 * @param navController Controller used to navigate to other screens.
 * @param application Application used to build the view model.
 * @param viewModel View model that provides the notifications.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificacionesScreen(navController: NavController, application: Application, viewModel: NotificacionesVIewModel = viewModel(factory = NotificacionesVIewModelFactory(application,navController))) {
    val notificaciones by viewModel.notificaciones.collectAsState(initial = emptyList())
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val error by viewModel.error.collectAsState()
    Scaffold(
        topBar = { SearchBarNotificaciones(
            textoBusqueda = textoBusqueda,
            viewModel = viewModel,
            onTextoCambio = { viewModel.actualizarTextoBusqueda(it) },
            navController = navController
        )
                 },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        if (!error.isNullOrEmpty()) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Notificaciones(
                notificacion = notificaciones,
                modifier = Modifier.padding(innerPadding),
                navController = navController
            )
        }
    }
}

@Composable
/**
 * Scrollable list of notifications, or an empty-state message when there are none.
 *
 * @param notificacion Notifications to display.
 * @param modifier Modifier applied to the list.
 * @param navController Controller used to perform navigation.
 */
fun Notificaciones(notificacion: List<notificacion>, modifier: Modifier, navController: NavController) {

    if (notificacion.isEmpty()) {
        Text(
            text = "No hay notificaciones disponibles",
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
            items(notificacion) { it ->
                NotificacionCard( it)
            }
        }
    }
}



@Composable
/**
 * Card that groups the likes received by a single publication.
 *
 * @param notificacion Notification to display.
 */
fun NotificacionCard(notificacion: notificacion) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = notificacion.id_publicacion)

            if (notificacion.likes.isEmpty()) {
                Text(
                    text = "No hay likes nuevos de esta publicación",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            } else {
                notificacion.likes.forEach { like ->
                    likesCard(like)
                }
            }
        }
    }
}
    @Composable
    /**
 * Row that describes a single like: who gave it and when.
 *
 * @param like Like to display.
 */
fun likesCard(like:like){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp).background(Color(0xFF9ADE9C)),
            elevation = 4.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "like",
                    tint = Color(0xFF5D4037)
                )
                Text(
                    text = "${like.idUsuario.nombreUsuario} dio like a tu publicacion el ${like.fecha}",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color(0xFF5D4037)
                )
            }
        }
    }
/**
 * Top search bar of the notifications screen.
 *
 * @param textoBusqueda Text currently shown in the search field.
 * @param navController Controller used to run the search.
 * @param viewModel View model that runs the search.
 * @param onTextoCambio Callback invoked when the text changes.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchBarNotificaciones(textoBusqueda: String, navController: NavController, viewModel: NotificacionesVIewModel, onTextoCambio: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF55A458)).padding(top=30.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.IconButton(onClick = {
            viewModel.buscarAve(navController)
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
    }
}

