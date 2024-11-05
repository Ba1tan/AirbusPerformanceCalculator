package com.example.airbusperformancecalculator
import okhttp3.*
import java.io.IOException


class MetarService {
    private val client = OkHttpClient()

    fun fetchWeatherData(icao: String, callback: WeatherCallback) {
        val url = "https://metartaf.ru/$icao.json"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        callback.onSuccess(responseBody)
                    } else {
                        callback.onFailure("Empty response body")
                    }
                } else {
                    callback.onFailure("Error: ${response.code}")
                }
            }
        })
    }
}