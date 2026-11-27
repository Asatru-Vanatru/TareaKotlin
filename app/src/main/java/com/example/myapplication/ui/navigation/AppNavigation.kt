package com.example.myapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.ui.roles.RolesScreen
import com.example.myapplication.ui.roles.RolesViewModel
import com.example.myapplication.ui.sesion.SesionViewModel
import com.example.myapplication.ui.tecnico.PanelTecnicoScreen
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
import com.example.myapplication.ui.historial.HistorialScreen

/**
 * Rutas de la aplicacion, centralizadas para no repetir textos sueltos.
 */
object Rutas {
    const val LOGIN = "login"
    const val MENU = "menu"
    const val LISTA = "lista"
    const val FORMULARIO = "formulario"
    const val POSTS = "posts"

    const val HISTORIAL = "historial"
    const val PANEL_TECNICO = "panelTecnico"
    const val ROLES = "roles"
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

    // Se crea fuera del NavHost para que todas las pantallas compartan sesion.
    val sesionViewModel: SesionViewModel = viewModel(factory = SesionViewModel.Factory)
    val sesion by sesionViewModel.uiState.collectAsStateWithLifecycle()
    val usuario = sesion.usuario

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {

        composable(Rutas.LOGIN) {
            LaunchedEffect(usuario != null) {
                if (usuario != null) {
                    navController.navigate(Rutas.MENU) {
                        // popUpTo evita que el boton Atras devuelva al login
                        // una vez que el usuario ya ingreso.
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                onIngresar = sesionViewModel::iniciarSesion,
                errorIngreso = sesion.error,
                ingresando = sesion.ingresando,
                onEditar = sesionViewModel::limpiarError
            )
        }

        composable(Rutas.MENU) {
            if (usuario == null) return@composable

            MenuScreen(
                nombreUsuario = usuario.nombre,
                rol = usuario.rol,
                onIrAPanelTecnico = { navController.navigate(Rutas.PANEL_TECNICO) },
                onIrAAdministrarRoles = { navController.navigate(Rutas.ROLES) },
                onIrASolicitudes = { navController.navigate(Rutas.LISTA) },
                onIrAPosts = { navController.navigate(Rutas.POSTS) },
                onIrAHistorial = { navController.navigate(Rutas.HISTORIAL) },
                onCerrarSesion = {
                    sesionViewModel.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.MENU) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.PANEL_TECNICO) {
            if (usuario?.rol?.vePanelTecnico == true) {
                PanelTecnicoScreen(onVolver = { navController.popBackStack() })
            }
        }

        composable(Rutas.ROLES) {
            if (usuario?.rol?.administraRoles == true) {
                val viewModel: RolesViewModel = viewModel(factory = RolesViewModel.Factory)

                RolesScreen(
                    viewModel = viewModel,
                    actor = usuario,
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(Rutas.HISTORIAL) {
            val viewModel: SolicitudViewModel =
                viewModel(factory = SolicitudViewModel.Factory)

            HistorialScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
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
