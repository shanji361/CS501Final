package com.example.weatherapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapi.ui.theme.WeatherAPITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

data class WeatherData(
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAPITheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier) {
    var weatherData by remember { mutableStateOf<WeatherData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Weather Information",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
            weatherData != null -> {
                WeatherBox(weatherData = weatherData!!)
            }
        }
    }
}

@Composable
fun WeatherBox(weatherData: WeatherData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherItem(
                label = "Temperature",
                value = "${weatherData.temperature}°C",
                icon = "🌡️"
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeatherItem(
                label = "Humidity",
                value = "${weatherData.humidity}%",
                icon = "💧"
            )
            Spacer(modifier = Modifier.height(16.dp))
            WeatherItem(
                label = "Wind Speed",
                value = "${weatherData.windSpeed} km/h",
                icon = "💨"
            )
        }
    }
}

@Composable
fun WeatherItem(label: String, value: String, icon: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

suspend fun fetchWeatherData(): WeatherData {
    return withContext(Dispatchers.IO) {
        // Using Berlin coordinates as example (52.52°N, 13.41°E)
        val url = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,relative_humidity_2m,wind_speed_10m&models=gfs_seamless"

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