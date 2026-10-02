package es.unex.natureconnect.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.unex.natureconnect.data.repository.IARepository
import es.unex.natureconnect.network.RetrofitClient

/**
 * Factory that creates [IaViewModel] instances with their repository.
 *
 * @param application Application forwarded to the view model.
 */
class IaViewModelFactory (private val application: Application): ViewModelProvider.Factory {
    /**
     * Creates the requested view model.
     *
     * @param modelClass Class of the view model to create.
     * @return A new [IaViewModel] backed by [IARepository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = IARepository(RetrofitClient.api)
        return IaViewModel(repository,application) as T
    }
}