package es.unex.natureconnect.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.unex.natureconnect.data.models.AveDetectada
import es.unex.natureconnect.viewModel.IaViewModel


@Composable
/**
 * Screen that lists the birds detected by the model.
 *
 * Decodes the selected image honouring its EXIF rotation and shows one card
 * per detection.
 *
 * @param navController Controller used to navigate to other screens.
 * @param viewModel View model that provides the detection results.
 */
fun DeteccionResponseScreen(
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
        bottomBar = { BottomNavigationBar(navController) }
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

@Composable
/**
 * Card showing the cropped photo of a detected bird and its confidence.
 *
 * The crop is clamped to the bitmap bounds to tolerate invalid boxes.
 *
 * @param ave Detection to display.
 * @param originalBitmap Full photo the detection comes from.
 */
fun AveCard(ave: AveDetectada, originalBitmap: Bitmap) {
    val croppedBitmap = remember(ave) {
        try {
            val (x1, y1, x2, y2) = ave.bbox

            val safeX1 = x1.coerceIn(0, originalBitmap.width - 1)
            val safeY1 = y1.coerceIn(0, originalBitmap.height - 1)
            val safeX2 = x2.coerceIn(safeX1 + 1, originalBitmap.width)
            val safeY2 = y2.coerceIn(safeY1 + 1, originalBitmap.height)

            val width = safeX2 - safeX1
            val height = safeY2 - safeY1

            Bitmap.createBitmap(originalBitmap, safeX1, safeY1, width, height)
        } catch (e: Exception) {
            Log.e("AveCard", "Error al recortar la imagen: ${e.message}")
            null
        }
    }


    Card(
        elevation = 6.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            croppedBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Recorte de ave detectada",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = ave.clase,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Confianza: ${(ave.confianza_clasificacion * 100)}%",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

