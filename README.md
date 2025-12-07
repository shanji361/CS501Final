# InstaGlow | Beauty App | Android (Kotlin + Jetpack Compose)

This is a Kotlin/Android beauty app built with Jetpack Compose. It integrates **Firebase Authentication**, a **live weather forecast** (from the OpenWeatherMap API), product browsing (from the Makeup API), and a local shopping cart.

## Demo One 11/4/2025
Watch our full app demo, including video walkthroughs, by clicking the [link here](https://docs.google.com/presentation/d/1nwfEWkiNuIehxy6FuLXlfblTZpB4IcLc32x3zp_sFdA/edit?usp=sharing)
## Features

* **Firebase Authentication**: Full login and sign-up screen flow.
* **Weather Home Screen**: The main "home" screen (`WeatherScreen.kt`) greets the logged-in user and displays a live weather card for their location.
* **YouTube Video Carousel**: The home screen also features a horizontal scrolling list of embedded YouTube video thumbnails.
* **Product Browsing**: A multi-column grid (`ProductsScreen.kt`) displaying makeup products fetched from the Makeup API.
* **Product Filtering**: A filter sheet (`FilterBottomSheet.kt`) allows users to filter products.
* **Product Details**: A dedicated screen (`ProductDetailScreen.kt`) shows more information for a selected item.
* **Favorites (Likes)**: Users can "like" products, which are tracked in the `MainViewModel`.
* **Shopping Cart**: A functional cart (`CartScreen.kt`) with quantity controls and total calculation.
* **Profile Screen**: A dedicated screen (`ProfileScreen.kt`) for user information and liked items.
* **Bottom Navigation**: A custom `BottomNavBar.kt` with an elevated, central "AI Scan" button.

## Demo Two 12/2/2025
Click the [link here](https://docs.google.com/presentation/d/1w7bB2ansTAv_P7HBIOqV54Jg_sPSMdFu4DX21h6yV-E/edit?usp=sharing) for our Demo Two Google Slides

## Core Technical Features

This project demonstrates several key Android development concepts:

* **Jetpack Navigation**: The app uses a `NavHost` in `MainActivity.kt` to manage navigation between multiple functional screens, including `LoginScreen`, `SignUpScreen`, and the main multi-screen `BeautyApp` composable.
* **ViewModel & StateFlow**: State management is handled by two `ViewModel`s (`MainViewModel` and `WeatherViewModel`). Both use `StateFlow` to expose UI state (e.g., `productState` and `weatherState`), which is collected reactively in composables using `collectAsState()`.
* **External API Calls**: The app successfully calls two different external APIs using Retrofit:
    1.  The **Makeup API** to fetch product data in `MainViewModel`.
    2.  The **OpenWeatherMap API** to fetch live weather data in `WeatherViewModel`.


## Project Structure

* **`app/src/main/java/com/example/beautyapp/`**
    * `MainActivity.kt`: Main entry point, handles navigation
    * **`data/`**
        * `Product.kt`: Product data model
        * `CartItem.kt`: Cart item model with productId, quantity, and selectedShade
        * `UserPreferences.kt`: User settings data model
        * **`weather/Weather.kt`**: Weather data models
        * **`database/`**
            * `AppDatabase.kt`: Room database instance
            * `CartDao.kt`: DAO for cart operations
            * `CartEntity.kt`: Room entity for cart items
    * **`network/`**
        * `MakeupApiService.kt`: Retrofit service for Makeup API
        * `WeatherApiService.kt`: Retrofit service for Weather API
        * `GooglePlacesApiService.kt`: Retrofit service for Google Places API
    * **`repository/`**
        * `CartRepository.kt`: Repository for cart data management
        * `ProductRepository.kt`: Repository for product data
        * `WeatherRepository.kt`: Repository for weather data
    * **`viewmodel/`**
        * `MainViewModel.kt`: ViewModel for products, cart, likes
        * `WeatherViewModel.kt`: ViewModel for fetching weather
        * `CartViewModel.kt`: ViewModel for cart operations
        * `SettingsViewModel.kt`: ViewModel for user settings
    * **`ui/`**
        * **`components/`**
            * `BottomNavBar.kt`: Custom bottom navigation bar
            * `FilterBottomSheet.kt`: Composable for filtering products
            * `ProductCard.kt`: Card for displaying a single product
            * `CartItemCard.kt`: Card for cart items with shade selection
            * `ShadeSelector.kt`: Component for selecting product shades
        * **`screens/`**
            * `CartScreen.kt`: Shopping cart screen with shade-based item separation
            * `LoginScreen.kt`: Firebase authentication login screen
            * `ProductDetailScreen.kt`: Product detail view with shade selection
            * `ProductsScreen.kt`: Product grid screen
            * `ProfileScreen.kt`: User profile and favorites
            * `SignUpScreen.kt`: Firebase user registration screen
            * `WeatherScreen.kt`: Main "Home" screen with weather integration
            * `SettingsScreen.kt`: User settings and preferences
            * `AccessibilityScreen.kt`: Accessibility settings
            * `StoreFinder.kt`: Google Maps store finder with Places API
            * `VideoScreen.kt`: YouTube video integration screen
        * **`theme/`**
            * `Theme.kt`: Material theme with dark mode support
            * `Color.kt`: Color definitions with brand colors
            * `Typography.kt`: Typography definitions
    * **`utils/`**
        * `ColorUtils.kt`: Utilities for color matching and conversion
        * `SettingsManager.kt`: Settings persistence manager
        * `PreferencesDataStore.kt`: DataStore for settings persistence
        * `FirebaseAuthHelper.kt`: Firebase authentication helper functions

### Prerequisites

* Android Studio (Iguana or later)
* JDK 17 or higher
* Android SDK (API 24+)
* **Firebase Project**: You must create a Firebase project, enable Authentication, and add your `google-services.json` file to the `app/` directory.
* **API Keys**: You need to add API keys for [OpenWeatherMap](https://openweathermap.org/api) to fetch weather.

### Setup Instructions

1.  **Clone/Open Project**: Open the project in Android Studio.
2.  **Add `google-services.json`**: Download this file from your Firebase project settings and place it in the `app/` folder.
3.  **Add API Keys**: Add your OpenWeatherMap API key where required (e.g., in `WeatherViewModel.kt` or `local.properties`). Add Google Maps API key under `AndroidManifest.xml`.
4.  **Sync Gradle**: Click "Sync Project with Gradle Files".
5.  **Run the app**: Connect an Android device or start an emulator and click "Run".

## Key Dependencies

* **Jetpack Compose**: Modern UI toolkit
* **Material3**: Material Design 3 components
* **Firebase Authentication**: For user login and registration
* **Retrofit/Moshi & Gson**: HTTP client for API calls and JSON parsing
* **Coil**: Image loading library
* **Coroutines & ViewModel**: For asynchronous operations and state management
* **Android YouTube Player**: For embedding YouTube videos

## APIs Used

* **Makeup API**: `https://makeup-api.herokuapp.com/api/v1/products.json`
* **OpenWeatherMap API**: For fetching live weather data.
* **Google Maps API & Places Web API**: For find nearby stores feature.
