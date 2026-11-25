package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.login.LoginScreen
import com.example.myapplication.ui.menu.MenuScreen
import com.example.myapplication.ui.screens.PostScreen
import com.example.myapplication.viewmodel.PostViewModel
import com.example.myapplication.ui.solicitud.ListaSolicitudesScreen
import com.example.myapplication.ui.solicitud.SolicitudFormScreen
import com.example.myapplication.ui.solicitud.SolicitudViewModel

/**
 * Rutas de la aplicacion, centralizadas para no repetir textos sueltos.
 */
object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val LISTA = "lista"
    const val FORMULARIO = "formulario"
    const val POSTS = "posts"
}

/**
 * Grafo de navegacion: Login -> Menu -> Lista -> Formulario.
 *
 * El NavHost define que pantalla corresponde a cada ruta. Las pantallas no
 * se llaman entre si directamente: cada una recibe funciones y es este
 * archivo el que decide a donde lleva cada accion.
 */
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                onIngresar = {
                    navController.navigate(Rutas.MENU) {
                        // popUpTo evita que el boton Atras devuelva al login
                        // una vez que el usuario ya ingreso.
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.MENU) {
            MenuScreen(
                onIrASolicitudes = { navController.navigate(Rutas.LISTA) },
                onIrAPosts = { navController.navigate(Rutas.POSTS) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.MENU) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.POSTS) {
            val viewModel: PostViewModel = viewModel()
            PostScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Rutas.LISTA) {
            val viewModel: SolicitudViewModel =
                viewModel(factory = SolicitudViewModel.Factory)

            ListaSolicitudesScreen(
                viewModel = viewModel,
                onNuevaSolicitud = {
                    // id 0 significa "solicitud nueva".
                    navController.navigate("${Rutas.FORMULARIO}/0")
                },
                onEditarSolicitud = { id ->
                    navController.navigate("${Rutas.FORMULARIO}/$id")
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Rutas.FORMULARIO}/{solicitudId}",
            arguments = listOf(
                navArgument("solicitudId") { type = NavType.LongType }
            )
        ) { backStackEntry ->

            val solicitudId = backStackEntry.arguments?.getLong("solicitudId") ?: 0L

            val viewModel: SolicitudViewModel =
                viewModel(factory = SolicitudViewModel.Factory)

            SolicitudFormScreen(
                viewModel = viewModel,
                solicitudId = solicitudId,
                onGuardado = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
