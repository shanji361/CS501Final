package com.example.weatherapi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

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
        items(makeupTopics) { (topic, duration) ->
            Card(
                modifier = Modifier
                    .width(160.dp)
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDEEF4))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    )
                    Text(text = topic, fontWeight = FontWeight.SemiBold)
                    Text(text = duration, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

suspend fun fetchWeatherData(): WeatherData = withContext(Dispatchers.IO) {
    val url =
        URL("https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current_weather=true")
    val json = url.readText()
    val jsonObject = JSONObject(json)
    val currentWeather = jsonObject.getJSONObject("current_weather")
    val temp = currentWeather.getDouble("temperature")
    val windSpeed = currentWeather.getDouble("windspeed")
    val humidity = 70
    WeatherData(temperature = temp, humidity = humidity, windSpeed = windSpeed)
}

fun celsiusToFahrenheit(celsius: Double): Double = celsius * 9 / 5 + 32
fun kmhToMph(kmh: Double): Double = kmh * 0.621371

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Discover") },
            label = { Text("Discover") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") },
            label = { Text("Cart") },
            selected = selectedTab == 2, // Corrected the typo here
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}
