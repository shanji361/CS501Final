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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.beautyapp.data.StoreFinderUiState
import com.example.beautyapp.data.StoreLocation
import com.example.beautyapp.viewmodel.StoreFinderViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFinderScreen(
    productName: String,
    productBrand: String?,
    onBack: () -> Unit,
    viewModel: StoreFinderViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // state to track if the app has location permission.
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        hasLocationPermission = isGranted

        if (isGranted) {
            // if permission is granted, immediately search for stores.
            viewModel.onLocationPermissionGranted(context, productName, productBrand)
        } else {
            // if denied, update the UI to show a message.
            viewModel.onLocationPermissionDenied()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Nearby Stores", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (uiState.showMap && uiState.userLocation != null) {
            MapScreen(
                paddingValues = paddingValues,
                productName = productName,
                productBrand = productBrand,
                uiState = uiState,
                onMapReady = viewModel::onMapReady,
                context = context
            )
        } else {
            InitialScreen(
                paddingValues = paddingValues,
                productName = productName,
                productBrand = productBrand,
                uiState = uiState,
                hasLocationPermission = hasLocationPermission,
                onSearchClick = {
                    Log.d("StoreFinder", "Button clicked, hasPermission: $hasLocationPermission")
                    if (hasLocationPermission) {
                        viewModel.searchForStores(context, productName, productBrand)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    }
                },
                onOpenMapsWithoutLocation = {
                    Log.d("StoreFinder", "Search without location clicked")
                    openInGoogleMapsApp(context, productName, productBrand, null)
                }
            )
        }
    }
}

@Composable
private fun MapScreen(
    paddingValues: PaddingValues,
    productName: String,
    productBrand: String?,
    uiState: StoreFinderUiState,
    onMapReady: (GoogleMap) -> Unit,
    context: Context
) {
    Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", tint = Color(0xFFF472B6), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Found ${uiState.nearbyStores.size} stores nearby",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${productBrand ?: ""} ${productName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        GoogleMapViewWithStores(
            modifier = Modifier.fillMaxWidth().weight(1f),
            userLocation = uiState.userLocation!!,
            stores = uiState.nearbyStores,
            onMapReady = onMapReady
        )

        Button(
            onClick = { openInGoogleMapsApp(context, productName, productBrand, uiState.userLocation) },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF472B6)),
            shape = RoundedCornerShape(50)
        ) {
            Text("See list of stores in Google Maps", color = Color.White)
        }
    }
}

@Composable
private fun InitialScreen(
    paddingValues: PaddingValues,
    productName: String,
    productBrand: String?,
    uiState: StoreFinderUiState,
    hasLocationPermission: Boolean, // Added this parameter
    onSearchClick: () -> Unit,
    onOpenMapsWithoutLocation: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Searching for:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text(productName, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                productBrand?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location", modifier = Modifier.size(64.dp), tint = Color(0xFFF472B6))
        Spacer(Modifier.height(16.dp))
        Text("We'll show you nearby stores on a map", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        uiState.errorMessage?.let { error ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                Text(error, modifier = Modifier.padding(16.dp), color = Color(0xFFC62828), style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF472B6)),
            shape = RoundedCornerShape(50),
            enabled = !uiState.isSearching
        ) {
            if (uiState.isSearching) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Show Nearby Stores", color = Color.White)
            }
        }

        TextButton(onClick = onOpenMapsWithoutLocation) {
            Text("Open in Google Maps without location", color = Color(0xFFF472B6))
        }
    }
}

@Composable
fun GoogleMapViewWithStores(
    modifier: Modifier = Modifier,
    userLocation: LatLng,
    stores: List<StoreLocation>,
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
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AndroidView(factory = { mapView }, modifier = modifier) { map ->
        map.getMapAsync { googleMap ->
            googleMap.apply {
                moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13f))
                addMarker(MarkerOptions().position(userLocation).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                stores.forEach { store ->
                    addMarker(MarkerOptions().position(store.location).title(store.name).snippet(store.address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
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

private fun openInGoogleMapsApp(context: Context, productName: String?, productBrand: String?, userLocation: LatLng?) {
    val searchQuery = buildString {
        if (!productBrand.isNullOrBlank()) {
            append(productBrand)
            append(" ")
        }
        append(productName ?: "beauty product")
        append(" store near me")
    }
    Log.d("StoreFinder", "Opening Google Maps with query: $searchQuery")

    try {
        val gmmIntentUri = if (userLocation != null) {
            "geo:${userLocation.latitude},${userLocation.longitude}?q=${Uri.encode(searchQuery)}".toUri()
        } else {
            "geo:0,0?q=${Uri.encode(searchQuery)}".toUri()
        }

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).setPackage("com.google.android.apps.maps").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            openInBrowser(context, searchQuery)
        }
    } catch (e: Exception) {
        Log.e("StoreFinder", "Error opening maps: ${e.message}", e)
        Toast.makeText(context, "Unable to open Google Maps.", Toast.LENGTH_LONG).show()
    }
}

private fun openInBrowser(context: Context, searchQuery: String) {
    try {
        val browserUri = "https://www.google.com/maps/search/?api=1&query=${Uri.encode(searchQuery)}".toUri()
        val browserIntent = Intent(Intent.ACTION_VIEW, browserUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    } catch (e: Exception) {
        Log.e("StoreFinder", "Browser also failed: ${e.message}")
        Toast.makeText(context, "Unable to open maps. Please try manually.", Toast.LENGTH_SHORT).show()
    }
}
