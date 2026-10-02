package es.unex.natureconnect.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import es.unex.natureconnect.data.repository.PublicacionesRespository
import es.unex.natureconnect.network.RetrofitClient

/**
 * Factory that creates [NotificacionesVIewModel] instances with their repository.
 *
 * @param application Application forwarded to the view model.
 * @param navController Controller used by the view model to navigate.
 */
class NotificacionesVIewModelFactory(private val application: Application, private val navController: NavController): ViewModelProvider.Factory {

    /**
     * Creates the requested view model.
     *
     * @param modelClass Class of the view model to create.
     * @return A new [NotificacionesVIewModel] backed by [PublicacionesRespository].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = PublicacionesRespository(RetrofitClient.api)
        return  NotificacionesVIewModel(repository,application) as T
    }

}