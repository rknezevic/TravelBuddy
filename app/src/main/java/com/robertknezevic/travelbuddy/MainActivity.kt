package com.robertknezevic.travelbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.robertknezevic.travelbuddy.data.auth.AuthViewModel
import com.robertknezevic.travelbuddy.ui.screen.CityDetailScreen
import com.robertknezevic.travelbuddy.ui.screen.CitySearchScreen
import com.robertknezevic.travelbuddy.ui.screen.CommentsScreen
import com.robertknezevic.travelbuddy.ui.screen.LoginScreen
import com.robertknezevic.travelbuddy.ui.screen.RegisterScreen
import com.robertknezevic.travelbuddy.ui.theme.TravelBuddyTheme
import com.robertknezevic.travelbuddy.viewmodel.CityViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cityViewModel: CityViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login") { LoginScreen(navController)}
        composable("register") { RegisterScreen(navController)}
        composable("home") { CitySearchScreen(
            cityViewModel = cityViewModel,
            authViewModel = authViewModel,
            navController = navController
        ) }
        composable("city-detail/{cityName}/{country}/{population}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val country = backStackEntry.arguments?.getString("country") ?: ""
            val population = backStackEntry.arguments?.getString("population") ?: ""

            CityDetailScreen(
                navController = navController,
                cityName = cityName,
                cityCountry = country,
                cityPopulation = population
            )
        }
        composable("comments/{cityName}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            CommentsScreen(
                cityName = cityName,
                navController = navController
            )
        }


    })
 }

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TravelBuddyTheme {
        AppNavigation()
    }
}