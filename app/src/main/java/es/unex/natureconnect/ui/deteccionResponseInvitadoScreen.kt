package es.unex.natureconnect.ui


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.unex.natureconnect.viewModel.IaViewModel


@Composable
/**
 * Guest version of the detection results screen.
 *
 * Lists the detected birds exactly like the signed-in screen but uses the
 * guest bottom navigation.
 *
 * @param navController Controller used to navigate to other screens.
 * @param viewModel View model that provides the detection results.
 */
fun DeteccionResponseInvitadoScreen(
    navController: NavController,
    viewModel: IaViewModel
) {
    val resultados by viewModel.resultados.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val context = LocalContext.current



    val originalBitmap by produceState<Bitmap?>(initialValue = null, selectedImageUri) {
        value = try {
            selectedImageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val exif = ExifInterface(inputStream)


                    val imageStream = context.contentResolver.openInputStream(uri)
                    val rawBitmap = imageStream?.use { BitmapFactory.decodeStream(it) }

                    val rotationDegrees = when (exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }

                    rawBitmap?.let { bitmap ->
                        if (rotationDegrees != 0) {
                            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                        } else {
                            bitmap
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("DeteccionResponse", "Error al cargar bitmap rotado: ${e.localizedMessage}")
            null
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.material3.Text(
                        "Aves detectadas",
                        color = Color(0xFF5D4037)
                    )
                },
                backgroundColor = Color(0xFF55A458),
                modifier = Modifier.padding(top = 30.dp)
            )
        },
        bottomBar = { BottomNavigationBarInvitado(navController) }
    ) { innerPadding ->

        if (resultados==null || originalBitmap == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay resultados para mostrar")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(resultados!!.objetos) {ave ->
                    AveCard(ave = ave, originalBitmap = originalBitmap!!)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }


    }
}
