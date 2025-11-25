package com.example.trailmapapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Manage map state and overlay data
class MapViewModel : ViewModel() {

    // State for current trail
    private val _currentTrail = MutableStateFlow<Trail?>(null)
    val currentTrail: StateFlow<Trail?> = _currentTrail.asStateFlow()

    // State for current park area
    private val _currentParkArea = MutableStateFlow<ParkArea?>(null)
    val currentParkArea: StateFlow<ParkArea?> = _currentParkArea.asStateFlow()

    // State for showing info dialog
    private val _showInfoDialog = MutableStateFlow(false)
    val showInfoDialog: StateFlow<Boolean> = _showInfoDialog.asStateFlow()

    // State for dialog title
    private val _dialogTitle = MutableStateFlow("")
    val dialogTitle: StateFlow<String> = _dialogTitle.asStateFlow()

    // State for dialog content
    private val _dialogContent = MutableStateFlow("")
    val dialogContent: StateFlow<String> = _dialogContent.asStateFlow()

    init {
        loadSampleData()
    }

    private fun loadSampleData() {
        viewModelScope.launch {
            // Sample hiking trail coordinates (Central Park, NYC)
            val trailCoordinates = listOf(
                LatLng(40.768697, -73.981818),
                LatLng(40.768102, -73.971523),
                LatLng(40.782865, -73.965355),
                LatLng(40.782083, -73.971222),
                LatLng(40.778687, -73.981238)
            )

            _currentTrail.value = Trail(
                id = "trail_1",
                name = "Central Park Loop Trail",
                description = "A beautiful 6.1 mile loop trail around Central Park. Perfect for walking, running, and cycling.",
                coordinates = trailCoordinates,
                color = 0xFF2196F3.toInt(),
                width = 10f
            )

            // Sample park area coordinates
            val parkCoordinates = listOf(
                LatLng(40.768102, -73.981818),
                LatLng(40.768102, -73.971523),
                LatLng(40.782865, -73.971523),
                LatLng(40.782865, -73.981818)
            )

            _currentParkArea.value = ParkArea(
                id = "park_1",
                name = "Central Park",
                description = "Central Park is an urban park in New York City located between the Upper West and Upper East Sides of Manhattan.",
                coordinates = parkCoordinates,
                fillColor = 0x3300FF00.toInt(),
                strokeColor = 0xFF00FF00.toInt(),
                strokeWidth = 5f
            )
        }
    }

    // Update trail color
    fun updateTrailColor(color: Int) {
        _currentTrail.value = _currentTrail.value?.copy(color = color)
    }

    // Update trail width
    fun updateTrailWidth(width: Float) {
        _currentTrail.value = _currentTrail.value?.copy(width = width)
    }

    // Update park fill color
    fun updateParkFillColor(color: Int) {
        _currentParkArea.value = _currentParkArea.value?.copy(fillColor = color)
    }

    // Update park stroke color
    fun updateParkStrokeColor(color: Int) {
        _currentParkArea.value = _currentParkArea.value?.copy(strokeColor = color)
    }

    // Update park stroke width
    fun updateParkStrokeWidth(width: Float) {
        _currentParkArea.value = _currentParkArea.value?.copy(strokeWidth = width)
    }

    // Show info dialog when overlay is clicked
    fun showOverlayInfo(title: String, content: String) {
        _dialogTitle.value = title
        _dialogContent.value = content
        _showInfoDialog.value = true
    }

    // Hide info dialog
    fun hideInfoDialog() {
        _showInfoDialog.value = false
    }
}