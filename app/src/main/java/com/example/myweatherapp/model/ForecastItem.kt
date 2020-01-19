package com.example.myweatherapp.model

data class ForecastItem (val latitude: Double, val longitude: Double, val timezone: String, val hourly: Hourly,val daily:Daily) {
}