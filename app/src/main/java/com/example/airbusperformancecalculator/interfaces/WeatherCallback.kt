package com.example.airbusperformancecalculator.interfaces

interface WeatherCallback {
    fun onSuccess(response: String)
    fun onFailure(errorMessage: String)
}