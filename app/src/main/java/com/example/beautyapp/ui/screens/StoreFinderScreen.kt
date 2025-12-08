/*
 * StoreFinderScreen.kt
 *
 * Store Finder screen with Google Maps integration
 * Features:
 * - Request location permission
 * - Show user's location on map
 * - Display nearby beauty stores with markers
 * - Open Google Maps for directions (with error handling)
 */

package com.example.beautyapp.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.StoreFinderUiState
import com.example.beautyapp.data.StoreLocation
import com.example.beautyapp.viewmodel.StoreFinderViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFinderScreen(
    product: Product,
    onBack: () -> Unit,
    viewModel: StoreFinderViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Check if location permission is granted
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        hasLocationPermission = granted

        if (granted) {
            viewModel.onLocationPermissionGranted(context, product)
        } else {
            viewModel.onLocationPermissionDenied()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Find Nearby Stores",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (uiState.showMap && uiState.userLocation != null) {
            MapScreen(
                paddingValues = paddingValues,
                product = product,
                uiState = uiState,
                onMapReady = viewModel::onMapReady,
                context = context
            )
        } else {
            InitialScreen(
                paddingValues = paddingValues,
                product = product,
                uiState = uiState,
                hasLocationPermission = hasLocationPermission,
                onSearchClick = {
                    Log.d("StoreFinder", "Button clicked, hasPermission: $hasLocationPermission")
                    if (hasLocationPermission) {
                        viewModel.searchForStores(context, product)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                onOpenMapsWithoutLocation = {
                    Log.d("StoreFinder", "Search without location clicked")
                    openInGoogleMapsApp(context, product, null)
                }
            )
        }
    }
}

@Composable
private fun MapScreen(
    paddingValues: PaddingValues,
    product: Product,
    uiState: StoreFinderUiState,
    onMapReady: (GoogleMap) -> Unit,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFFF472B6),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Found ${uiState.nearbyStores.size} stores nearby, tap on a marker to see the address",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${product.brand ?: ""} ${product.name ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        GoogleMapViewWithStores(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userLocation = uiState.userLocation!!,
            stores = uiState.nearbyStores,
            product = product,
            onMapReady = onMapReady
        )

        Button(
            onClick = {
                // FIXED: Better error handling for Google Maps
                openInGoogleMapsApp(context, product, uiState.userLocation!!)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF472B6)
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "See list of stores in Google Maps",
                color = Color.White
            )
        }
    }
}

@Composable
private fun InitialScreen(
    paddingValues: PaddingValues,
    product: Product,
    uiState: StoreFinderUiState,
    hasLocationPermission: Boolean,
    onSearchClick: () -> Unit,
    onOpenMapsWithoutLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Searching for:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.name ?: "Product",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                product.brand?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFF472B6)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We'll show you nearby stores on a map",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFFC62828),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSearchClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF472B6)
            ),
            shape = RoundedCornerShape(50),
            enabled = !uiState.isSearching
        ) {
            if (uiState.isSearching) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Show Nearby Stores",
                    color = Color.White
                )
            }
        }

        TextButton(onClick = onOpenMapsWithoutLocation) {
            Text(
                text = "Open in Google Maps without location",
                color = Color(0xFFF472B6)
            )
        }
    }
}

@Composable
fun GoogleMapViewWithStores(
    modifier: Modifier = Modifier,
    userLocation: LatLng,
    stores: List<StoreLocation>,
    product: Product,
    onMapReady: (GoogleMap) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    ) { map ->
        map.getMapAsync { googleMap ->
            googleMap.apply {
                moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13f))

                addMarker(
                    MarkerOptions()
                        .position(userLocation)
                        .title("You are here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                )

                stores.forEach { store ->
                    addMarker(
                        MarkerOptions()
                            .position(store.location)
                            .title(store.name)
                            .snippet(store.address)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    )
                }

                try {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                } catch (e: SecurityException) {
                    Log.e("GoogleMap", "Location permission error: ${e.message}")
                }

                uiSettings.isZoomControlsEnabled = true
                uiSettings.isCompassEnabled = true
            }
            onMapReady(googleMap)
        }
    }
}

// FIXED: Better error handling for opening Google Maps
private fun openInGoogleMapsApp(context: Context, product: Product, userLocation: LatLng?) {
    val searchQuery = buildString {
        if (product.brand != null) {
            append(product.brand)
            append(" ")
        }
        append(product.name ?: "beauty product")
        append(" store near me")
    }

    Log.d("StoreFinder", "Opening Google Maps with query: $searchQuery")

    try {
        val gmmIntentUri = if (userLocation != null) {
            Uri.parse("geo:${userLocation.latitude},${userLocation.longitude}?q=${Uri.encode(searchQuery)}")
        } else {
            Uri.parse("geo:0,0?q=${Uri.encode(searchQuery)}")
        }

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        // FIXED: Add FLAG_ACTIVITY_NEW_TASK to prevent ANR
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            Log.d("StoreFinder", "Opening Google Maps app")
            try {
                context.startActivity(mapIntent)
            } catch (e: Exception) {
                // If Maps app fails, try browser
                Log.e("StoreFinder", "Maps app failed, trying browser: ${e.message}")
                openInBrowser(context, searchQuery)
            }
        } else {
            // No Maps app, open in browser
            Log.d("StoreFinder", "No Maps app found, opening browser")
            openInBrowser(context, searchQuery)
        }
    } catch (e: Exception) {
        Log.e("StoreFinder", "Error opening maps: ${e.message}", e)
        // Show toast to user
        Toast.makeText(
            context,
            "Unable to open Google Maps. Try searching directly in Maps app.",
            Toast.LENGTH_LONG
        ).show()
    }
}

// Helper function to open in browser if Maps fails
private fun openInBrowser(context: Context, searchQuery: String) {
    try {
        val browserUri = Uri.parse(
            "https://www.google.com/maps/search/?api=1&query=${Uri.encode(searchQuery)}"
        )
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    } catch (e: Exception) {
        Log.e("StoreFinder", "Browser also failed: ${e.message}")
        Toast.makeText(
            context,
            "Unable to open maps. Please try manually.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
