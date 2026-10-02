package es.unex.natureconnect.ui

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import es.unex.natureconnect.R
import es.unex.natureconnect.viewModel.LoginViewModel
import es.unex.natureconnect.viewModel.LoginViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(application: Application,navController: NavController,
    loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(application)),
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
            TopAppBar(title = { Text("NatureConnect Login",color = Color(0xFF5D4037)) },
                backgroundColor = Color(0xFF55A458),
                modifier = Modifier.padding(top=30.dp)
            )

        },
        content = { paddingValues ->
            LoginContent(navController,
                modifier = Modifier.padding(paddingValues),
                onLoginClick = { email, password ->
                    loginViewModel.login(email, password)
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
fun LoginContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLoginClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.natureconetclogo),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Nombre",color = Color(0xFF5D4037)) },
            modifier = Modifier.fillMaxWidth().background(Color(0xFF55A458))
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña",color= Color(0xFF5D4037)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().background(Color(0xFF55A458))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onLoginClick(email, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))
        ) {
            Text("Iniciar Sesión",color = Color(0xFF5D4037) )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))
        ) {
            Text("Registrarse",color = Color(0xFF5D4037) )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navController.navigate("homeInvitado") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors( backgroundColor = Color(0xFF55A458))
        ) {
            Text("Invitado",color = Color(0xFF5D4037) )
        }
    }
}
