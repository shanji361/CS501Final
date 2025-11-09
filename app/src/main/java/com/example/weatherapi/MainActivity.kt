package com.example.weatherapi
import LoginScreen
import SplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import com.example.weatherapi.ui.theme.WeatherAPITheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapi.com.example.weatherapi.SignUpScreen


data class WeatherData(
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAPITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // here we have navController, this is for the purpose of navigations between screens
                    val navController = rememberNavController()

                    // navhost setup here
                    NavHost(navController = navController, startDestination = "splash") {
                        // this takes us to splash screen
                        composable("splash") {
                            SplashScreen(navController = navController)
                        }
                        //login screen route
                        composable("login") {
                            LoginScreen(
                                //handles display name as an argument for home route
                                onLoginSuccess = { userName ->                                    // Navigate to the main part of your app after login
                                    // for example to a "home" screen, we pass in the username as an argument
                                    navController.navigate("home/$userName") {                                        // Clear the back stack up to the login screen
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                },
                                    onRegisterClick = {
                                        //navigate to sign up screen
                                        navController.navigate("signup")
                                    }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                onSignUpSuccess = {
                                    // Navigate back to the login screen after successful sign-up
                                    navController.popBackStack()
                                },
                                onLoginClick = {
                                    // Navigate back to the login screen
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("home/{userName}") { backStackEntry ->
                            val userName=backStackEntry.arguments?.getString("userName")
                             WeatherScreen(userName= userName ?: "Guest")
                        }

                    }
                }
            }
        }
    }
}

