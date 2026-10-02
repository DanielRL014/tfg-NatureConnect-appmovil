package es.unex.natureconnect.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import es.unex.natureconnect.data.repository.PublicacionesRespository
import es.unex.natureconnect.network.RetrofitClient


/**
 * Factory that creates [PrincipalViewModel] instances with their repository.
 *
 * @param navController Controller forwarded to the view model.
 */
class PrincipalViewModelFactory(private val navController: NavController): ViewModelProvider.Factory {
    /**
     * Creates the requested view model.
     *
     * @param modelClass Class of the view model to create.
     * @return A new [PrincipalViewModel] backed by [PublicacionesRespository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = PublicacionesRespository(RetrofitClient.api)
        return PrincipalViewModel(repository,navController) as T
    }
}