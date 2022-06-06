/*
 *
 *  * Copyright (c) 2022 Razeware LLC
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in
 *  * all copies or substantial portions of the Software.
 *  *
 *  * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 *  * distribute, sublicense, create a derivative work, and/or sell copies of the
 *  * Software in any work that is designed, intended, or marketed for pedagogical or
 *  * instructional purposes related to programming, coding, application development,
 *  * or information technology.  Permission for such use, copying, modification,
 *  * merger, publication, distribution, sublicensing, creation of derivative works,
 *  * or sale is expressly withheld.
 *  *
 *  * This project and source code may use libraries or frameworks that are
 *  * released under various Open-Source licenses. Use of those libraries and
 *  * frameworks are governed by their own individual licenses.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  * THE SOFTWARE.
 *
 */

package com.android.composegeomarker.presentation.screens

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.android.composegeomarker.R
import com.android.composegeomarker.permissions.PermissionAction
import com.android.composegeomarker.permissions.PermissionDialog
import com.android.composegeomarker.presentation.GeoMarkerViewModel
import com.android.composegeomarker.presentation.composables.CustomInfoWindow
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun MapScreenContent(
    snackbarHostState: SnackbarHostState,
    geoMarkerViewModel: GeoMarkerViewModel
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  var showMap by rememberSaveable {
    mutableStateOf(false)
  }

  val currentLocation by geoMarkerViewModel.currentLatLng.collectAsState()

  PermissionDialog(
      context = context,
      permission = Manifest.permission.ACCESS_FINE_LOCATION,
      permissionRationale = stringResource(id = R.string.permission_location_rationale),
      snackbarHostState = snackbarHostState) { permissionAction ->
    when (permissionAction) {
      is PermissionAction.PermissionDenied -> {
        showMap = false
      }
      is PermissionAction.PermissionGranted -> {
        showMap = true
        scope.launch {
          snackbarHostState.showSnackbar("Location permission granted!")
        }
      }
    }
  }
  if (showMap && currentLocation.latitude > 0.00) {
    MapComposable(context, currentLocation)
  }

}

@Composable
fun MapComposable(context: Context, location: LatLng) {
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(location, 16f)
  }
  val infoWindowState = rememberMarkerState(position = location)

  val mapUiSettings by remember {
    mutableStateOf(
        MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false
        )
    )
  }
  val mapProperties by remember {
    mutableStateOf(
        MapProperties(
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
        )
    )
  }
  GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraPositionState,
      properties = mapProperties,
      uiSettings = mapUiSettings,
  ) {
    Marker(
        state = MarkerState(position = location),
    )

    MarkerInfoWindow(
        state = infoWindowState,
        title = "My location",
        snippet = "Location custom info window",
        content = {
          CustomInfoWindow(title = it.title, description = it.snippet)
        }
    )

    Circle(
        center = location,
        fillColor = Color.Green,
        strokeColor = Color.Green,
        radius = 105.00
    )

  }
}