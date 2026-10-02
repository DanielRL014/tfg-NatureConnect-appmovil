package es.unex.natureconnect.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.unex.natureconnect.data.repository.UsuaraiosRepository
import es.unex.natureconnect.network.RetrofitClient

/**
 * Factory that creates [RegisterViewModel] instances with their repository.
 *
 * @param application Application forwarded to the view model.
 */
class RegisterViewModelFactory (private val application: Application) : ViewModelProvider.Factory{

    /**
     * Creates the requested view model.
     *
     * @param modelClass Class of the view model to create.
     * @return A new [RegisterViewModel] backed by [UsuaraiosRepository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = UsuaraiosRepository(RetrofitClient.api)
        return RegisterViewModel(repository,application) as T
    }
}