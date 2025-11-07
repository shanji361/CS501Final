package com.example.instaglowhome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.BorderStroke

// Custom Color Scheme
private val CustomColorScheme = lightColorScheme(
    primary = Color(0xFFFF6B9D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAE5),
    onPrimaryContainer = Color(0xFF3E001F),
    secondary = Color(0xFFFFEBEE),
    onSecondary = Color(0xFF5C0028),
    secondaryContainer = Color(0xFFFFEBEE),
    onSecondaryContainer = Color(0xFF3E001F),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A1B),
    surfaceVariant = Color(0xFFF3DDE1),
    onSurfaceVariant = Color(0xFF524345),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A1B),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = CustomColorScheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(modifier = Modifier.padding(innerPadding), context = this)
                }
            }
        }
    }
}
@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    context: ComponentActivity,
    viewModel: WeatherViewModel = viewModel()
) {
    var cityName by remember { mutableStateOf("Boston") }
    val weatherState by viewModel.weatherState.collectAsState()
    val apiKey = "769e458d899c570bd71a25267411d1f9"
    var selectedTab by remember { mutableStateOf(0) }
    var isFahrenheit by remember { mutableStateOf(true) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchInput by remember { mutableStateOf("") }
    var searchError by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // Store the last successful weather state
    var lastSuccessfulState by remember {
        mutableStateOf<WeatherViewModel.WeatherState>(WeatherViewModel.WeatherState.Initial)
    }

    // Fetch Boston weather on initial load
    LaunchedEffect(Unit) {
        viewModel.fetchWeather("Boston", apiKey)
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
            // Header Section
            HeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Show either Search Bar OR Weather Display Card
            if (showSearchBar) {
                // Search Bar Card (replaces weather card)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Change Location",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        TextField(
                            value = searchInput,
                            onValueChange = {
                                searchInput = it
                                searchError = ""
                            },
                            label = { Text("Enter City Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            enabled = !isSearching
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Error Message
                        if (searchError.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = "Error",
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = searchError,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    when {
                                        searchInput.isBlank() -> {
                                            searchError = "Please enter a city name"
                                        }
                                        isSearching -> {
                                            // Already searching, do nothing
                                        }
                                        else -> {
                                            isSearching = true
                                            searchError = ""
                                            viewModel.fetchWeather(searchInput, apiKey)
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                enabled = !isSearching
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text(
                                        "Get Weather",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    showSearchBar = false
                                    searchInput = ""
                                    searchError = ""
                                    isSearching = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                enabled = !isSearching
                            ) {
                                Text(
                                    "Cancel",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Handle search result state
                LaunchedEffect(weatherState) {
                    if (isSearching) {
                        when (weatherState) {
                            is WeatherViewModel.WeatherState.Success -> {
                                cityName = searchInput
                                showSearchBar = false
                                searchInput = ""
                                searchError = ""
                                isSearching = false
                                // Update the last successful state
                                lastSuccessfulState = weatherState
                            }
                            is WeatherViewModel.WeatherState.Error -> {
                                val errorMsg = (weatherState as WeatherViewModel.WeatherState.Error).errorMessage
                                searchError = if (errorMsg.contains("404")) {
                                    "City not found. Please try another location."
                                } else {
                                    "Failed to fetch weather. Please try again."
                                }
                                isSearching = false
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                // Weather Display Card (shows when not searching)
                // Use lastSuccessfulState if available, otherwise use current weatherState
                val displayState = if (lastSuccessfulState is WeatherViewModel.WeatherState.Success) {
                    lastSuccessfulState
                } else {
                    weatherState
                }

                when (displayState) {
                    WeatherViewModel.WeatherState.Initial -> {}
                    WeatherViewModel.WeatherState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    is WeatherViewModel.WeatherState.Success -> {
                        val weatherResponse = (displayState as WeatherViewModel.WeatherState.Success).weatherResponse
                        WeatherDisplayCard(
                            cityName = weatherResponse.name,
                            temperature = weatherResponse.main.temp,
                            description = weatherResponse.weather[0].description,
                            humidity = weatherResponse.main.humidity,
                            feelsLike = weatherResponse.main.feelsLike,
                            isFahrenheit = isFahrenheit,
                            onUnitToggle = { isFahrenheit = it },
                            onChangeLocation = { showSearchBar = true }
                        )
                        // Update the last successful state when displaying
                        LaunchedEffect(displayState) {
                            lastSuccessfulState = displayState
                        }
                    }
                    is WeatherViewModel.WeatherState.Error -> {
                        val errorMessage = (displayState as WeatherViewModel.WeatherState.Error).errorMessage
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Error: $errorMessage",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Video Section
            VideoCardPlaceHolder(context)
        }
    }
}
@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hi 👋",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "User",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Discover your unique beauty!",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun WeatherDisplayCard(
    cityName: String,
    temperature: Double,
    description: String,
    humidity: Int,
    feelsLike: Double,
    isFahrenheit: Boolean,
    onUnitToggle: (Boolean) -> Unit,
    onChangeLocation: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            // City Name with Change Location Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cityName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                TextButton(
                    onClick = onChangeLocation,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Change",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Weather Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = getWeatherEmoji(description), fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Temperature
                val displayTemp = if (isFahrenheit) {
                    celsiusToFahrenheit(temperature).toInt()
                } else {
                    temperature.toInt()
                }

                Text(
                    text = "$displayTemp",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                // Unit Toggle
                Column {
                    Text(
                        text = "°F",
                        fontSize = 16.sp,
                        fontWeight = if (isFahrenheit) FontWeight.Bold else FontWeight.Medium,
                        color = if (isFahrenheit) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { onUnitToggle(true) }
                    )
                    Text(
                        text = "|",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "°C",
                        fontSize = 16.sp,
                        fontWeight = if (!isFahrenheit) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isFahrenheit) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { onUnitToggle(false) }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Weather Details
                Column {
                    Text(
                        text = "Humidity: $humidity%",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    val feelsLikeDisplay = if (isFahrenheit) {
                        "${celsiusToFahrenheit(feelsLike).toInt()}°F"
                    } else {
                        "${feelsLike.toInt()}°C"
                    }
                    Text(
                        text = "Feels: $feelsLikeDisplay",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Description
            Text(
                text = description.replaceFirstChar { it.uppercase() },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
fun VideoCardPlaceHolder(context: ComponentActivity) {
    val weatherVideos = listOf(
        Triple("Foundation", "1 min", "https://www.youtube.com/shorts/VWQWI333vqI"),
        Triple("Blush Application", "1 min", "https://www.youtube.com/shorts/EuvGTZ1UwBA"),
        Triple("Contour Tips", "1 min", "https://www.youtube.com/shorts/nNcEqP1nHiw"),
        Triple("Eye Makeup", "1 min", "https://www.youtube.com/shorts/8SrXsfQMlyQ"),
        Triple("Lipstick Guide", "1 min", "https://www.youtube.com/shorts/mIzYqIKwTJE")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(weatherVideos.size) { index ->
            val (topic, duration, videoUrl) = weatherVideos[index]
            Card(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
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
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⏱️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = duration,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Play button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Topic text
                        Text(
                            text = topic,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Home",
                    fontSize = 10.sp,
                    color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Eco, contentDescription = "Products") },
            label = {
                Text(
                    "Products",
                    fontSize = 10.sp,
                    color = if (selectedTab == 1) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
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
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "AR Scan",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    "AR Scan",
                    fontSize = 10.sp,
                    color = if (selectedTab == 2) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = if (selectedTab == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Cart",
                    fontSize = 10.sp,
                    color = if (selectedTab == 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (selectedTab == 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    "Profile",
                    fontSize = 10.sp,
                    color = if (selectedTab == 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) }
        )
    }
}