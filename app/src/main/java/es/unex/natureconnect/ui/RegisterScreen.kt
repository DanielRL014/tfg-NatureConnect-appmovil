package es.unex.natureconnect.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import es.unex.natureconnect.viewModel.RegisterViewModel
import es.unex.natureconnect.viewModel.RegisterViewModelFactory
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(application: Application,
                loginViewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(application)), // ViewModel asociado
                onLoginSuccess: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val userState by loginViewModel.user.collectAsState(initial = null)
    val errorState by loginViewModel.error.collectAsState(initial = null)

    LaunchedEffect(userState) {
        if (userState != null) {
            onLoginSuccess()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("NatureConnect Registro",color = Color(0xFF5D4037)) },
                backgroundColor = Color(0xFF55A458),
                modifier = Modifier.padding(top=30.dp)
            )
        },
        content = { paddingValues ->
            RegisterContent(
                modifier = Modifier.padding(paddingValues),
                onLoginClick = { name, email, password ->
                    loginViewModel.Create(name, password.toString(),email)
                }
            )
        }
    )

    errorState?.let { errorMessage ->
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(errorMessage)
                loginViewModel.clearError()
            }
        }
    }
}

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    onLoginClick: (String, String, Any?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre",color = Color(0xFF5D4037)) },
            modifier = Modifier.fillMaxWidth().background(Color(0xFF55A458))
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email",color = Color(0xFF5D4037)) },
            modifier = Modifier.fillMaxWidth().background(Color(0xFF55A458))
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña",color = Color(0xFF5D4037)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().background(Color(0xFF55A458))
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onLoginClick(name,email,password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))
        ) {
            Text("Iniciar Sesión",color = Color(0xFF5D4037) )
        }
    }
}
