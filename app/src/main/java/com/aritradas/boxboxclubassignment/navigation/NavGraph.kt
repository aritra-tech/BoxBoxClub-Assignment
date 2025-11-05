package com.aritradas.boxboxclubassignment.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aritradas.boxboxclubassignment.data.di.NetworkModule
import com.aritradas.boxboxclubassignment.data.repository.F1Repository
import com.aritradas.boxboxclubassignment.domain.usecase.GetDriversUseCase
import com.aritradas.boxboxclubassignment.domain.usecase.GetRaceDetailsUseCase
import com.aritradas.boxboxclubassignment.domain.usecase.GetUpcomingRaceUseCase
import com.aritradas.boxboxclubassignment.ui.screen.DetailScreen
import com.aritradas.boxboxclubassignment.ui.screen.HomeScreen
import com.aritradas.boxboxclubassignment.ui.viewmodel.DetailViewModel
import com.aritradas.boxboxclubassignment.ui.viewmodel.HomeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{raceId}") {
        fun createRoute(raceId: String) = "detail/$raceId"
    }
}

/**
 * Dependency injection helper
 * In a production app, use a proper DI framework like Hilt or Koin
 */
object AppModule {
    private val repository = F1Repository(NetworkModule.apiService)
    
    val getDriversUseCase = GetDriversUseCase(repository)
    val getUpcomingRaceUseCase = GetUpcomingRaceUseCase(repository)
    val getRaceDetailsUseCase = GetRaceDetailsUseCase(repository)
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel {
                HomeViewModel(
                    getDriversUseCase = AppModule.getDriversUseCase,
                    getUpcomingRaceUseCase = AppModule.getUpcomingRaceUseCase
                )
            }
            HomeScreen(
                viewModel = viewModel,
                onNavigateToDetail = { raceId ->
                    navController.navigate(Screen.Detail.createRoute(raceId))
                }
            )
        }
        
        composable(Screen.Detail.route) { backStackEntry ->
            val raceId = backStackEntry.arguments?.getString("raceId") ?: ""
            val viewModel: DetailViewModel = viewModel {
                DetailViewModel(
                    getRaceDetailsUseCase = AppModule.getRaceDetailsUseCase,
                    raceId = raceId
                )
            }
            DetailScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

