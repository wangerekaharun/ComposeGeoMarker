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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.composegeomarker.presentation.GeoMarkerViewModel
import com.android.composegeomarker.presentation.composables.GeoMarkerButton
import com.android.composegeomarker.presentation.composables.GeoMarkerTopBar
import com.android.composegeomarker.presentation.composables.SaveGeoPoint
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@ExperimentalMaterial3Api
@Composable
fun GeoMarkerScreen(
    geoMarkerViewModel: GeoMarkerViewModel
) {
  var areaPoints = mutableListOf<LatLng>()
  var drawPolygon by remember {
    mutableStateOf(false)
  }

  val currentLocation by geoMarkerViewModel.currentLatLng.collectAsState()

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(currentLocation, 16f)
  }

  var showSavePoint by remember {
    mutableStateOf(false)
  }

  var clickedLocation by remember {
    mutableStateOf(LatLng(0.0, 0.0))
  }

  Scaffold(
      topBar = { GeoMarkerTopBar() },
      content = { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
          GoogleMap(
              modifier = Modifier.fillMaxSize(),
              cameraPositionState = cameraPositionState,
              onMapClick = {
                if (!drawPolygon) {
                  showSavePoint = true
                  clickedLocation = it
                }

              }
          ) {
            // 1
            if (drawPolygon && areaPoints.isNotEmpty()) {
              // 2
              areaPoints.forEach {
                Marker(state = MarkerState(position = it))
              }

              // 3
              Polygon(
                  points = areaPoints,
                  fillColor = Color.Blue,
                  strokeColor = Color.Blue
              )
            }
            // 4
            if (showSavePoint) {
              Marker(state = MarkerState(position = clickedLocation))
            }

          }

          if (showSavePoint) {
            SaveGeoPoint(latLng = clickedLocation) {
              showSavePoint = it.hideSavePointUi
              areaPoints.add(it.point)
            }
          } else {
            if (areaPoints.isEmpty()) {
              Text(
                  modifier = Modifier
                      .fillMaxWidth(),
                  color = Color.Blue,
                  text = "Click any point on the map to mark it.",
                  textAlign = TextAlign.Center,
                  fontWeight = FontWeight.Bold
              )
            }
          }


          GeoMarkerButton(
              modifier = Modifier
                  .padding(start = 10.dp, end = 10.dp, bottom = 16.dp)
                  .align(Alignment.BottomCenter),
              drawPolygon = drawPolygon,
              areaPoints = areaPoints
          ) { drawPolygonCallback ->
            drawPolygon = drawPolygonCallback
            if (!drawPolygonCallback) {
              areaPoints = mutableListOf()
            }
          }
        }
      }
  )


}