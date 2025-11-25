package com.example.trailmapapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

// Display the map with overlays and customization options
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MapViewModel = viewModel()) {
    // Collect state from ViewModel
    val currentTrail by viewModel.currentTrail.collectAsState()
    val currentParkArea by viewModel.currentParkArea.collectAsState()
    val showInfoDialog by viewModel.showInfoDialog.collectAsState()
    val dialogTitle by viewModel.dialogTitle.collectAsState()
    val dialogContent by viewModel.dialogContent.collectAsState()

    // Camera position for the map (centered on Central Park)
    val centralPark = LatLng(40.778687, -73.968157)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centralPark, 13f)
    }

    // Local state for customization panel visibility
    var showCustomizationPanel by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trail Map Explorer") }
            )
        },
        floatingActionButton = {
            Button(
                onClick = { showCustomizationPanel = !showCustomizationPanel }
            ) {
                Text("Customize")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Map with overlays
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Draw polyline for hiking trail
                currentTrail?.let { trail ->
                    Polyline(
                        points = trail.coordinates,
                        color = Color(trail.color),
                        width = trail.width,
                        clickable = true,
                        onClick = {
                            viewModel.showOverlayInfo(trail.name, trail.description)
                            true
                        }
                    )
                }

                // Draw polygon for park area
                currentParkArea?.let { park ->
                    Polygon(
                        points = park.coordinates,
                        fillColor = Color(park.fillColor),
                        strokeColor = Color(park.strokeColor),
                        strokeWidth = park.strokeWidth,
                        clickable = true,
                        onClick = {
                            viewModel.showOverlayInfo(park.name, park.description)
                            true
                        }
                    )
                }
            }

            // Customization panel
            if (showCustomizationPanel) {
                CustomizationPanel(
                    viewModel = viewModel,
                    currentTrail = currentTrail,
                    currentParkArea = currentParkArea,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }

            // Info dialog
            if (showInfoDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.hideInfoDialog() },
                    title = { Text(dialogTitle) },
                    text = { Text(dialogContent) },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.hideInfoDialog() }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

// Color picker component for selecting overlay colors
@Composable
fun ColorPicker(
    selectedColor: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        0xFF2196F3.toInt() to "Blue",
        0xFF4CAF50.toInt() to "Green",
        0xFFF44336.toInt() to "Red",
        0xFFFF9800.toInt() to "Orange",
        0xFF9C27B0.toInt() to "Purple"
    )

    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { (color, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = selectedColor == color,
                    onClick = { onColorSelected(color) }
                )
                Text(
                    text = label,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

// Customization panel for modifying overlay appearance
@Composable
fun CustomizationPanel(
    viewModel: MapViewModel,
    currentTrail: Trail?,
    currentParkArea: ParkArea?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(0.8f)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Customize Overlays",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Trail customization section
            Text("Trail Settings", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            currentTrail?.let { trail ->
                // Trail color picker
                Text("Trail Color", fontSize = 14.sp)
                ColorPicker(
                    selectedColor = trail.color,
                    onColorSelected = { viewModel.updateTrailColor(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Trail width slider
                Text("Trail Width: ${"%.1f".format(trail.width)}", fontSize = 14.sp)
                Slider(
                    value = trail.width,
                    onValueChange = { viewModel.updateTrailWidth(it) },
                    valueRange = 5f..20f,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Park area customization section
            Text("Park Area Settings", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            currentParkArea?.let { park ->
                // Park fill color picker
                Text("Fill Color", fontSize = 14.sp)
                ColorPicker(
                    selectedColor = park.fillColor,
                    onColorSelected = { viewModel.updateParkFillColor(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Park stroke color picker
                Text("Border Color", fontSize = 14.sp)
                ColorPicker(
                    selectedColor = park.strokeColor,
                    onColorSelected = { viewModel.updateParkStrokeColor(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Park stroke width slider
                Text("Border Width: ${"%.1f".format(park.strokeWidth)}", fontSize = 14.sp)
                Slider(
                    value = park.strokeWidth,
                    onValueChange = { viewModel.updateParkStrokeWidth(it) },
                    valueRange = 1f..10f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}