package com.example.trailmapapp

import com.google.android.gms.maps.model.LatLng

// Data class representing a hiking trail with polyline coordinates
data class Trail(
    val id: String,
    val name: String,
    val description: String,
    val coordinates: List<LatLng>,
    var color: Int = 0xFF2196F3.toInt(), // Default blue color
    var width: Float = 10f // Default width
)

// Data class representing a park or area of interest with polygon coordinates
data class ParkArea(
    val id: String,
    val name: String,
    val description: String,
    val coordinates: List<LatLng>,
    var fillColor: Int = 0x3300FF00.toInt(), // Default semi-transparent green
    var strokeColor: Int = 0xFF00FF00.toInt(), // Default green border
    var strokeWidth: Float = 5f // Default border width
)