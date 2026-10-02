package es.unex.natureconnect.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.Ave
import es.unex.natureconnect.vi.NuevaPublicacionViewModel


@Composable
fun SeleccionarAvesScreen(navController: NavController, viewModel: NuevaPublicacionViewModel) {
    val listaAves by viewModel.Aves.collectAsState()
    val textoBusqueda by viewModel.textoBusqueda.collectAsState()
    val familias by viewModel.familias.collectAsState(initial = emptyList())
    var showFilterDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                title = {
                    androidx.compose.material.Text(
                        "Aves",
                        color = Color(0xFF5D4037)
                    )
                },
                backgroundColor = Color(0xFF55A458),
                modifier = Modifier.padding(top=30.dp)
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
                    onValueChange = { viewModel.actualizarTextoBusquedaA(it) },
                    placeholder = { Text("Buscar",color = Color(0xFF5D4037)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp).background(Color(0xFF55A458)),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar",tint = Color(0xFF5D4037))
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { showFilterDialog = true },
                    modifier = Modifier.background(Color(0xFF55A458))) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Filtrar",tint = Color(0xFF5D4037))
                }
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 8.dp),

                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(listaAves) { ave ->
                    AveItem(ave = ave, onCheckedChange = { viewModel.actualizarSeleccion(ave, it) })
                }
            }
            Button(
                onClick = {
                    viewModel.actualizarTextoBusquedaA("")
                    navController.navigate("SeleccionarEtiqueta") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),

                colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))
            ) {
                Text("Siguiente", color = Color(0xFF5D4037))
            }
        }
    }
    if (showFilterDialog) {
        FilterDialogA(
            familias = familias,
            onDismiss = { showFilterDialog = false },
            onApply = { selectedFamily ->
                showFilterDialog = false
                viewModel.filtrarPorFamilia(selectedFamily)
            }
        )
    }
}



@Composable
fun AveItem(ave: Ave, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xFF9ADE9C), shape = RoundedCornerShape(8.dp)) // Fondo verde claro
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
            text = ave.nombreComun ?:"error",
            modifier = Modifier.weight(1f),
            color=Color(0xFF5D4037),

        )
        Checkbox(
            checked = ave.selecionada,
            onCheckedChange = onCheckedChange
        )
    }
}
@Composable
fun FilterDialogA(
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
        androidx.compose.material.Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colors.surface,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                androidx.compose.material.Text(
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
                            androidx.compose.material.RadioButton(
                                selected = selectedFamily == family,
                                onClick = { selectedFamily = family }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            androidx.compose.material.Text(text = family ?:"erro")
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
                    androidx.compose.material.Text(text = "Aplicar")
                }
            }
        }
    }
}
