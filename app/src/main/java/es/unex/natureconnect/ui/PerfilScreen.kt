package es.unex.natureconnect.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.Publicacion
import es.unex.natureconnect.viewModel.PerfilViewModel
import es.unex.natureconnect.viewModel.PerfilViewModelFactory


@Composable
fun PerfilScreen(application: Application,navController: NavController, viewModel: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(application))) {
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val publicaciones by viewModel.publicaciones.collectAsState(initial = emptyList())
    val error by viewModel.error.collectAsState()
    Scaffold(
        topBar = { SearchBarPerfil(
                 textoBusqueda = textoBusqueda,
                 viewModel = viewModel,
                 onTextoCambio = { viewModel.actualizarTextoBusqueda(it) },
                 navController = navController)
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
            PublicacionesPerfil(
                publicaciones = publicaciones,
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                viewModel.nombre.toString()
            )
        }
    }
}


@Composable
fun PublicacionesPerfil(publicaciones: List<Publicacion>, modifier: Modifier, navController: NavController,nombre:String) {


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp).background(Color(0xFF9ADE9C)).padding(8.dp),

            contentAlignment = Alignment.Center
        ) {
            Text(text = nombre, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
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
                modifier = modifier.padding(top=150.dp),
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
fun SearchBarPerfil(textoBusqueda: String, navController: NavController, viewModel: PerfilViewModel, onTextoCambio: (String) -> Unit) {
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

