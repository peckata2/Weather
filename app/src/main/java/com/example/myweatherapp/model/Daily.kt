package com.example.myweatherapp.model

data class Daily (val summary: String,val icon: String,val data: List<DailyData>){
}