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
import es.unex.natureconnect.viewModel.IaViewModel

@Composable
fun IAmandarInvitadoScreen(
    navController: NavController,
    application: Application,
    viewModel: IaViewModel
) {
    val context = LocalContext.current
    val error by viewModel.error.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val recivido by viewModel.recivido.collectAsState()


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateSelectedImage(uri)
    }


    LaunchedEffect(recivido) {
        if (recivido) {
            navController.navigate("ia_resultadosInvitados")
            viewModel.resetEstado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detección de aves por IA", color = Color(0xFF5D4037))
                },
                backgroundColor = Color(0xFF55A458),
                modifier = Modifier.padding(top = 30.dp)
            )
        },
        bottomBar = { BottomNavigationBarInvitado(navController) }
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
                    modifier = Modifier.size(200.dp)
                )
            } else {
                Text(text = "No se ha seleccionado ninguna imagen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF55A458))
            ) {
                Text("Seleccionar Imagen", color = Color(0xFF5D4037))
            }

            Spacer(modifier = Modifier.height(16.dp))


            Button(
                onClick = {
                    selectedImageUri?.let {
                        viewModel.detectarAves(context)
                    }
                },
                enabled = selectedImageUri != null && !isLoading,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF55A458))
            ) {
                Text(
                    if (isLoading) "Procesando..." else "Enviar Imagen a IA",
                    color = Color(0xFF5D4037)
                )
            }


            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = Color.Red)
            }
        }
    }
}