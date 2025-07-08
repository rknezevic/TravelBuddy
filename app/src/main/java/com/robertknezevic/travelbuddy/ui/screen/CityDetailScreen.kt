package com.robertknezevic.travelbuddy.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.Manifest
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.annotation.SuppressLint

@Composable
fun CityDetailScreen(
    navController: NavController,
    cityName: String?,
    cityCountry: String?,
    cityPopulation: String?,
    cityLatitude: Double?,
    cityLongitude: Double?
) {
    val context = LocalContext.current
    var distance by remember { mutableStateOf<Double?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var permissionRequested by remember { mutableStateOf(false) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                if (cityLatitude != null && cityLongitude != null) {
                    isLoadingLocation = true
                    getCurrentLocation(
                        context = context,
                        fusedLocationProviderClient = fusedLocationProviderClient,
                        onGetCurrentLocationSuccess = { userLocation ->
                            distance = calculateDistance(
                                userLocation.first, userLocation.second,
                                cityLatitude, cityLongitude
                            )
                            isLoadingLocation = false
                        },
                        onGetCurrentLocationFailed = { exception ->
                            locationError = "Location error: ${exception.message}"
                            isLoadingLocation = false
                        }
                    )
                }
            }
            else -> {
                locationError = "Location permission denied"
                isLoadingLocation = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionRequested) {
            permissionRequested = true
            when {
                areLocationPermissionsGranted(context) -> {
                    if (cityLatitude != null && cityLongitude != null) {
                        isLoadingLocation = true
                        getCurrentLocation(
                            context = context,
                            fusedLocationProviderClient = fusedLocationProviderClient,
                            onGetCurrentLocationSuccess = { userLocation ->
                                distance = calculateDistance(
                                    userLocation.first, userLocation.second,
                                    cityLatitude, cityLongitude
                                )
                                isLoadingLocation = false
                            },
                            onGetCurrentLocationFailed = { exception ->
                                locationError = "Location error: ${exception.message}"
                                isLoadingLocation = false
                            }
                        )
                    }
                }
                else -> {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top=30.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "City Details",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFADD8E6), shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "$cityName",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Country: $cityCountry",
                fontSize = 18.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Population: $cityPopulation",
                fontSize = 18.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoadingLocation -> {
                    Text(
                        text = "Distance: Getting location...",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
                distance != null -> {
                    Text(
                        text = "Distance: ${String.format("%.1f", distance)} km",
                        fontSize = 18.sp,
                        color = Color.DarkGray
                    )
                }
                locationError != null -> {
                    Text(
                        text = "Distance: $locationError",
                        fontSize = 18.sp,
                        color = Color.Red
                    )
                }
                cityLatitude == null || cityLongitude == null -> {
                    Text(
                        text = "Distance: City coordinates not available",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
                else -> {
                    Text(
                        text = "Distance: Calculating...",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("comments/$cityName") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Comments")
        }
    }
}

fun areLocationPermissionsGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
}

/**
 * @param onGetCurrentLocationSuccess Callback function invoked when the current location is successfully retrieved.
 *        It provides a Pair representing latitude and longitude.
 * @param onGetCurrentLocationFailed Callback function invoked when an error occurs while retrieving the current location.
 *        It provides the Exception that occurred.
 * @param priority Indicates the desired accuracy of the location retrieval. Default is high accuracy.
 *        If set to false, it uses balanced power accuracy.
 */
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    onGetCurrentLocationSuccess: (Pair<Double, Double>) -> Unit,
    onGetCurrentLocationFailed: (Exception) -> Unit,
    priority: Boolean = true
) {
    val accuracy = if (priority) Priority.PRIORITY_HIGH_ACCURACY
    else Priority.PRIORITY_BALANCED_POWER_ACCURACY

    if (areLocationPermissionsGranted(context)) {
        fusedLocationProviderClient.getCurrentLocation(
            accuracy, CancellationTokenSource().token,
        ).addOnSuccessListener { location ->
            location?.let {
                onGetCurrentLocationSuccess(Pair(it.latitude, it.longitude))
            } ?: run {
                onGetCurrentLocationFailed(Exception("Location is null"))
            }
        }.addOnFailureListener { exception ->
            onGetCurrentLocationFailed(exception)
        }
    } else {
        onGetCurrentLocationFailed(Exception("Location permissions not granted"))
    }
}
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {

    val earthRadius = 6371.0 //kilometers

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = earthRadius * c

    return distance
}