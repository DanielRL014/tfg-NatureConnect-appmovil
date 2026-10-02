package es.unex.natureconnect.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.unex.natureconnect.data.repository.UsuaraiosRepository
import es.unex.natureconnect.network.RetrofitClient

class LoginViewModelFactory(private val application: Application) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = UsuaraiosRepository(RetrofitClient.api)
        return LoginViewModel(repository,application) as T
    }
}