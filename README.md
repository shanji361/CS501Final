# InstaGlow | Beauty App | Android (Kotlin + Jetpack Compose)

<p align="center">
  <img src="path/to/app-icon.png" alt="InstaGlow Logo" width="120"/>
</p>

InstaGlow is a comprehensive beauty application built with **Kotlin** and **Jetpack Compose** that solves three major problems in makeup shopping: overwhelming product choices, lack of inclusivity in shade matching, and fragmented user experience. The app integrates Firebase Authentication, live weather forecasts, shade-based product recommendations, and a complete shopping experience—all in one unified platform.

## 📱 Demo & Documentation

- **[Final Report](https://docs.google.com/document/d/1Y5LPNKT9Fe1EuAae6JxFHHaAXolV5gTWHw3fuWV7v9s/edit?usp=sharing)** - Comprehensive technical documentation
- **[Demo One (11/4/2025)](https://docs.google.com/presentation/d/1nwfEWkiNuIehxy6FuLXlfblTZpB4IcLc32x3zp_sFdA/edit?usp=sharing)** - Initial prototype walkthrough
- **[Demo Two (12/2/2025)](https://docs.google.com/presentation/d/1w7bB2ansTAv_P7HBIOqV54Jg_sPSMdFu4DX21h6yV-E/edit?usp=sharing)** - Feature-complete demo
- **[Demo

## ✨ Key Features

### 🎨 Core Functionality
- **Shade Matching System**: Select from 8 skin tones to get personalized foundation, blush, and lip liner recommendations from a local makeup database
- **Product Catalog**: Browse 1000+ makeup products from the Makeup API with advanced filtering by brand and product type
- **Smart Shopping Cart**: Shade-aware cart system that treats the same product in different shades as separate items
- **Store Finder**: Google Maps integration to locate nearby beauty retailers for in-person shopping
- **Notes & Favorites**: Save makeup looks with photos, track favorite products from both API and local databases

### 👤 User Experience
- **Firebase Authentication**: Secure login and registration with email/password
- **Edit Profile**: Update display name with Firebase Auth persistence
- **Weather Integration**: Personalized weather-based product recommendations on home screen
- **YouTube Tutorials**: Embedded makeup tutorial videos with error handling

### ♿ Accessibility
- **Dark Mode**: Full dark theme support across all screens with proper color contrast
- **Font Size Scaling**: Three size options (Small, Medium, Large) that scale typography app-wide
- **Color Blind Mode**: Protanopia and Deuteranopia filters for better color perception
- **Reduce Animations**: Toggle to disable motion for users sensitive to animations
- **Persistent Settings**: All preferences saved via DataStore and survive app restarts

## 🏗️ Architecture



## 📂 Project Structure
```
app/src/main/java/com/example/beautyapp/
│
├── MainActivity.kt                      # App entry point, navigation setup
│
├── data/                                # Data models & entities
│   ├── CartItem.kt                      # Cart with productId, quantity, selectedShade
│   ├── Product.kt                       # API product model
│   ├── MakeupProduct.kt                 # Local database product model
│   ├── Note.kt                          # User notes with images
│   ├── Settings.kt                      # Accessibility settings model
│   ├── Shade.kt                         # Skin shade data model
│   ├── weather/Weather.kt               # Weather data models
│   └── database/
│       ├── AppDatabase.kt               # Room database instance
│       ├── MakeupDatabase.kt            # Local shade recommendations DB
│       ├── ProductDao.kt                # Product queries
│       ├── ShadeDao.kt                  # Shade queries
│       ├── NoteDao.kt                   # Notes CRUD operations
│       └── LikedProductDao.kt           # Favorites persistence
│
├── network/                             # API service interfaces
│   ├── MakeupApiService.kt              # Makeup API (Retrofit)
│   ├── WeatherApiService.kt             # OpenWeatherMap API
│   └── StoreAPIService.kt               # Google Places API
│
├── viewmodel/                           # State management
│   ├── MainViewModel.kt                 # Products, cart, likes, notes
│   ├── WeatherViewModel.kt              # Weather data & location
│   ├── SettingsViewModel.kt             # Accessibility preferences
│   └── ShadeProductViewModel.kt         # Shade recommendations
│
├── ui/
│   ├── components/                      # Reusable UI components
│   │   ├── BottomNavBar.kt              # Custom bottom navigation
│   │   ├── ProductCard.kt               # Product display card
│   │   ├── CartItemCard.kt              # Cart item with shade selector
│   │   ├── SettingsDialog.kt            # Accessibility settings modal
│   │   ├── EditProfileDialog.kt         # Display name editor
│   │   ├── FilterBottomSheet.kt         # Product filtering
│   │   └── ShadeProductCard.kt          # Shade recommendation card
│   │
│   ├── screens/                         # Full-page screens
│   │   ├── LoginScreen.kt               # Firebase authentication
│   │   ├── SignUpScreen.kt              # User registration
│   │   ├── WeatherScreen.kt             # Home with weather + YouTube
│   │   ├── ProductsScreen.kt            # Product catalog with filters
│   │   ├── ProductDetailScreen.kt       # Single product view
│   │   ├── ShadeProductScreen.kt        # Shade matching interface
│   │   ├── CartScreen.kt                # Shopping cart
│   │   ├── ProfileScreen.kt             # User profile & favorites
│   │   └── StoreFinderScreen.kt         # Google Maps store locator
│   │
│   └── theme/
│       ├── Theme.kt                     # MaterialTheme with dark mode & font scaling
│       ├── Color.kt                     # LightColorScheme & DarkColorScheme
│       └── Typography.kt                # Font scaling implementation
│
└── utils/
    └── ColorUtils.kt                    # Hex color parsing utilities
```

## 🛠️ Technical Highlights

### State Management
- **StateFlow**: Reactive state updates with `collectAsState()` in Composables
- **ViewModel Scoping**: Proper lifecycle management prevents memory leaks
- **Single Source of Truth**: All UI state flows through ViewModels

### Database Architecture
- **Room Database**: Local persistence for products, shades, notes, likes, cart
- **DAO Pattern**: Clean separation of database queries
- **Type Converters**: Custom converters for complex types (e.g., `List<ProductColor>`)

### API Integration
- **Retrofit + Moshi/Gson**: Declarative HTTP client with automatic JSON parsing
- **Coroutines**: Asynchronous network calls with proper error handling
- **Repository Pattern**: Abstraction layer between ViewModels and data sources

### Accessibility Implementation
- **Dynamic Typography**: Font scaling affects all text app-wide via `MaterialTheme.typography`
- **Color Scheme Switching**: `MaterialTheme.colorScheme` ensures proper contrast in dark mode
- **DataStore Preferences**: Persistent settings storage replacing SharedPreferences

## 🚀 Setup Instructions

### Prerequisites
- **Android Studio**: Iguana (2023.2.1) or later
- **JDK**: 17 or higher
- **Android SDK**: API 24+ (Android 7.0)
- **Firebase Project**: [Create one here](https://console.firebase.google.com/)
- **API Keys**:
  - [OpenWeatherMap API](https://openweathermap.org/api)
  - [Google Maps API](https://console.cloud.google.com/)

### Installation

1. **Clone the repository**
```bash
   git clone https://github.com/shanji361/CS501Final.git
   cd CS501Final
```

2. **Add Firebase Configuration**
   - Download `google-services.json` from Firebase Console
   - Place in `app/` directory

3. **Add API Keys**
   
   Create `local.properties` in project root:
```properties
   WEATHER_API_KEY=your_openweather_api_key
   MAPS_API_KEY=your_google_maps_api_key
```

   Add to `AndroidManifest.xml`:
```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="${MAPS_API_KEY}" />
```

4. **Sync Gradle**
```
   File → Sync Project with Gradle Files
```

5. **Run the app**
   - Connect Android device or start emulator (API 24+)
   - Click **Run** 

##  Dependencies
```gradle
// UI & Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.activity:activity-compose:1.8.0")
implementation("io.coil-kt:coil-compose:2.5.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.5")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")

// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Database
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
kapt("androidx.room:room-compiler:2.6.1")

// Preferences
implementation("androidx.datastore:datastore-preferences:1.0.0")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// YouTube Player
implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")

// Google Maps
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.0.1")
```

## 🔗 APIs Used

| API | Purpose | Endpoint |
|-----|---------|----------|
| **Makeup API** | Product catalog (1000+ items) | `https://makeup-api.herokuapp.com/api/v1/products.json` |
| **OpenWeatherMap** | Live weather data | `https://api.openweathermap.org/data/2.5/weather` |
| **Google Places** | Nearby store search | `https://maps.googleapis.com/maps/api/place/nearbysearch/json` |

##  Future Enhancements

- [ ] Social sharing of makeup looks
- [ ] AI-powered shade matching via camera
- [ ] Push notifications for product restocks
- [ ] Multi-language support
- [ ] Wishlist with price tracking


## 📄 License

This project was created as part of CS501 Mobile Application Development course.

## 🙏 Acknowledgments

- [Makeup API](https://makeup-api.herokuapp.com/) for product data
- [OpenWeatherMap](https://openweathermap.org/) for weather integration
- [Material Design 3](https://m3.material.io/) for design guidelines
- [Firebase](https://firebase.google.com/) for authentication infrastructure

---
