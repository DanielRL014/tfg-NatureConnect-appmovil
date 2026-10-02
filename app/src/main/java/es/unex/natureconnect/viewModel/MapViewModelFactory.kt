package es.unex.natureconnect.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.unex.natureconnect.data.repository.PublicacionesRespository
import es.unex.natureconnect.network.RetrofitClient

/** Factory that creates [MapViewModel] instances with their repository. */
class MapViewModelFactory: ViewModelProvider.Factory  {
    /**
     * Creates the requested view model.
     *
     * @param modelClass Class of the view model to create.
     * @return A new [MapViewModel] backed by [PublicacionesRespository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = PublicacionesRespository(RetrofitClient.api)
        return MapViewModel(repository) as T
    }
}