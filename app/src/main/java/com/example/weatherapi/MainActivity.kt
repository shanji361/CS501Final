package com.example.weatherapi
import LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapi.ui.theme.WeatherAPITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


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

    @Composable
    fun WeatherScreen(modifier: Modifier = Modifier, userName: String) {
        var weatherData by remember { mutableStateOf<WeatherData?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var selectedTab by remember { mutableStateOf(0) }
        var isFahrenheit by remember { mutableStateOf(true) }
        val scope = rememberCoroutineScope()

        // Fetch weather data on launch
        LaunchedEffect(Unit) {
            scope.launch {
                try {
                    val data = fetchWeatherData()
                    weatherData = data
                    isLoading = false
                } catch (e: Exception) {
                    errorMessage = "Failed to load weather: ${e.message}"
                    isLoading = false
                }
            }
        }

        Scaffold(
            bottomBar = {
                BottomNavigationBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // header section, pass userNamer as an argument
                HeaderSection(userName=userName)

                Spacer(modifier = Modifier.height(24.dp))

                // Weather Card with loading/error states
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    errorMessage != null -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = errorMessage ?: "Unknown error",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    weatherData != null -> {
                        WeatherCard(
                            weatherData = weatherData!!,
                            isFahrenheit = isFahrenheit,
                            onUnitToggle = { isFahrenheit = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // For You Section and See More Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "For You",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { }) {
                        Text(text = "See More")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                VideoCardPlaceHolder()
            }
        }
    }

    @Composable
    //add userName as a param
    fun HeaderSection(userName:String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hi 👋",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = userName, // here were using username passed in by user, not static, but dynamic
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Discover your natural beauty with our makeup products!",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    @Composable
    fun WeatherCard(
        weatherData: WeatherData,
        isFahrenheit: Boolean,
        onUnitToggle: (Boolean) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sun Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB84D)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "☀️", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Temperature (converted based on selected unit)
                val displayTemp = if (isFahrenheit) {
                    celsiusToFahrenheit(weatherData.temperature).toInt()
                } else {
                    weatherData.temperature.toInt()
                }

                //temperature number display
                Text(
                    text = "$displayTemp",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold
                )

                // Clickable F or Celsius
                Column {
                    Text(
                        text = "°F",
                        fontSize = 16.sp,
                        fontWeight = if (isFahrenheit) FontWeight.Bold else FontWeight.Medium,
                        color = if (isFahrenheit) Color.Black else Color.Gray,
                        modifier = Modifier.clickable { onUnitToggle(true) }
                    )
                    Text(
                        text = "|",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "°C",
                        fontSize = 16.sp,
                        fontWeight = if (!isFahrenheit) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isFahrenheit) Color.Black else Color.Gray,
                        modifier = Modifier.clickable { onUnitToggle(false) }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Humidity and Wind speed column
                Column {
                    WeatherDetailItem("Humidity: ${weatherData.humidity}%")
                    val windSpeed = if (isFahrenheit) {
                        "${kmhToMph(weatherData.windSpeed).toInt()} mph"
                    } else {
                        "${weatherData.windSpeed.toInt()} km/h"
                    }
                    WeatherDetailItem("Wind: $windSpeed")
                }
            }
        }
    }

    @Composable
    fun WeatherDetailItem(text: String) {
        Text(
            text = text,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }


    @Composable
    fun VideoCardPlaceHolder() {
        val makeupTopics = listOf(
            "Foundation" to "3 min",
            "Blush Application" to "2 min",
            "Contour Tips" to "4 min",
            "Eye Makeup" to "5 min",
            "Lipstick Guide" to "2 min"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(makeupTopics.size) { index ->
                val (topic, duration) = makeupTopics[index]
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .height(400.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4D6))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Time indicator
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.9f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⏱️",
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = duration,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Play button
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF6B9D))
                                    .clickable {
                                        // Handle play button click
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Topic text
                            Text(
                                text = topic,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 8.dp
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home", fontSize = 10.sp) },
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            NavigationBarItem(
                icon = { Text("🧹", fontSize = 24.sp) },
                label = { Text("Products", fontSize = 10.sp) },
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .offset(y = (-8).dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF6B9D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "📷",
                            fontSize = 24.sp
                        )
                    }
                },
                label = { Text("AI Scan Face", fontSize = 10.sp) },
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
                label = { Text("Cart", fontSize = 10.sp) },
                selected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                label = { Text("Profile", fontSize = 10.sp) },
                selected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }


    // API fetching function
    suspend fun fetchWeatherData(): WeatherData {
        return withContext(Dispatchers.IO) {
            // Using Boston coordiantes
            val url =
                "https://api.open-meteo.com/v1/forecast?latitude=42.3555&longitude=-71.0565&current=temperature_2m,relative_humidity_2m,wind_speed_10m&models=gfs_seamless"

            val response = URL(url).readText()
            val jsonObject = JSONObject(response)
            val current = jsonObject.getJSONObject("current")

            WeatherData(
                temperature = current.getDouble("temperature_2m"),
                humidity = current.getInt("relative_humidity_2m"),
                windSpeed = current.getDouble("wind_speed_10m")
            )
        }
    }

    // Helper functions for unit conversion from C to F
    fun celsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }

    // change wind speed from kilometers/h to Miles/h if Celsius is selected
    fun kmhToMph(kmh: Double): Double {
        return kmh * 0.621371
    }

