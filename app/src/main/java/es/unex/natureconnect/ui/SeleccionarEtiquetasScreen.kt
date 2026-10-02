package es.unex.natureconnect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.Etiqueta
import es.unex.natureconnect.vi.NuevaPublicacionViewModel

@Composable
fun SeleccionarEtiquetasScreen(navController: NavController, viewModel: NuevaPublicacionViewModel) {
    val listaEtiquetas by viewModel.Etiquetas.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()

    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    androidx.compose.material.Text(
                        "Etiquetas",
                        color = Color(0xFF5D4037),
                        modifier = Modifier.padding(top=30.dp)
                    )
                },
                backgroundColor = Color(0xFF55A458)
            )

        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                   .background(Color(0xFF55A458)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textoBusqueda,
                    onValueChange = { viewModel.actualizarTextoBusquedaE(it) },
                    placeholder = { Text("Buscar",color = Color(0xFF5D4037)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp) .background(Color(0xFF55A458)),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar",tint = Color(0xFF5D4037))
                    },
                    singleLine = true
                )

            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(listaEtiquetas) { etiqueta ->
                    EtiquetaItem(etiqueta = etiqueta, onCheckedChange = { viewModel.actualizarSeleccionEtiquta(etiqueta, it) })
                }
            }
            val contex= LocalContext.current
            Button(
                onClick = { viewModel.publicar(contex)
                    navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))

                ) {
                Text("Publicar", color = Color(0xFF5D4037))
            }
        }
    }
}



@Composable
fun EtiquetaItem(etiqueta: Etiqueta, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFFE0F7E0), shape = RoundedCornerShape(8.dp)) // Fondo verde claro
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFF5D4037), shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = etiqueta.nombre,
            modifier = Modifier.weight(1f),
            color=Color(0xFF5D4037),
            )
        Checkbox(
            checked = etiqueta.seleccionada,
            onCheckedChange = onCheckedChange
        )
    }
}
