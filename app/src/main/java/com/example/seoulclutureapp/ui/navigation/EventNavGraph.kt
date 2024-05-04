package com.example.seoulclutureapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.seoulclutureapp.ui.screens.EventDetailDestination
import com.example.seoulclutureapp.ui.screens.EventDetailScreen
import com.example.seoulclutureapp.ui.screens.HomeDestination
import com.example.seoulclutureapp.ui.screens.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventNavGraph(navController: NavHostController,
                  modifier: Modifier = Modifier,) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ){
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigationToEventDetail = {
                    navController.navigate("${EventDetailDestination.route}/${it}")
                }
            )
        }
        composable(
            route = EventDetailDestination.routeWithArgs,
            arguments = listOf(navArgument(EventDetailDestination.eventIdArg) {
                type = NavType.IntType
            })
        ) {
            EventDetailScreen(
                navigateBack = { navController.navigateUp() }
            )
        }
    }
}