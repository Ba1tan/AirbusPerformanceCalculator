package com.example.airbusperformancecalculator.APIServices

import com.example.airbusperformancecalculator.interfaces.WeatherCallback
import com.example.airbusperformancecalculator.models.DecodedWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MetarService {
    private val client = OkHttpClient()

    suspend fun fetchWeather(icao: String): Result<DecodedWeather> = withContext(Dispatchers.IO) {
        val cleanIcao = icao.trim().uppercase()
        val url = "https://metartaf.ru/$cleanIcao.json"
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Server returned error code: ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(IOException("Empty response body"))
            val json = JSONObject(body)
            val metar = json.optString("metar", "No METAR data available")
            val taf = json.optString("taf", "No TAF data available")

            val decoded = MetarParser.parse(cleanIcao, metar, taf)
            Result.success(decoded)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun fetchWeatherData(icao: String, callback: WeatherCallback) {
        val url = "https://metartaf.ru/${icao.trim().uppercase()}.json"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
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
