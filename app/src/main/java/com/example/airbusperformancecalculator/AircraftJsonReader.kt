package com.example.airbusperformancecalculator

import android.content.Context
import com.google.gson.Gson

class AircraftJsonReader {
    val gson = Gson()

    fun readJsonFromAssets(context: Context, fileName: String): String {
        return context.resources.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}