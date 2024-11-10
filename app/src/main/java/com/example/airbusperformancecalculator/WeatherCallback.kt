package com.example.airbusperformancecalculator

interface WeatherCallback {
    fun onSuccess(response: String)
    fun onFailure(errorMessage: String)
}