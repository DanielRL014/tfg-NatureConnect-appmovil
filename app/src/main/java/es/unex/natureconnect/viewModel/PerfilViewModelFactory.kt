package es.unex.natureconnect.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.unex.natureconnect.data.repository.PublicacionesRespository
import es.unex.natureconnect.network.RetrofitClient

/**
 * Factory that creates [PerfilViewModel] instances with their repository.
 *
 * @param application Application forwarded to the view model.
 */
class PerfilViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    /**
     * Creates the requested view model.
     *
     * @param modelClass Class of the view model to create.
     * @return A new [PerfilViewModel] backed by [PublicacionesRespository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = PublicacionesRespository(RetrofitClient.api)
        return PerfilViewModel(repository,application) as T
    }
}