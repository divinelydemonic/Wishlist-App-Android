package kr.android.wishlistapp.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import kr.android.wishlistapp.navigation.Screen
import kr.android.wishlistapp.WishViewModel
import kr.android.wishlistapp.ui.screens.AddEditDetailsView
import kr.android.wishlistapp.ui.screens.HomeView


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Navigation(modifier: Modifier) {

    val viewModel: WishViewModel = viewModel()
    val navController: NavHostController = rememberNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

        composable(
            route = Screen.HomeScreen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                )
            }
        ) {
            HomeView(viewModel, navController)
        }
        composable(
            route = Screen.AddScreen.route + "/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = 0L
                    nullable = false
                }
            ),

            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                )
            }
        ) { entry ->
            //if add button is clicked, the id is 0, and the add screen opens
            //if edit/item is clicked, the id is not 0 and the screen with that id opens for updating

            val id = entry.arguments?.getLong("id") ?: 0L

            AddEditDetailsView(
                id = id,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}
