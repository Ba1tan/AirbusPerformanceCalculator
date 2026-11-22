package com.example.airbusperformancecalculator.models

import android.content.Context
import android.util.Log
import com.example.airbusperformancecalculator.adapters.AircraftJsonReader

class AirbusFactory {
    private val jsonReader = AircraftJsonReader()
    private val cache = mutableMapOf<String, Aircraft>()

    private val aircraftFiles = mapOf(
        "A320-214" to "A320-214.json",
        "A320-251N" to "A320-251N.json",
        "A321-211" to "A321-211.json",
        "A319-132" to "A319-132.json",
        "A330-243" to "A330-243.json"
    )

    fun create(context: Context, type: String): Aircraft? {
        cache[type]?.let { return it }

        val fileName = aircraftFiles[type] ?: return null
        return try {
            val jsonString = jsonReader.readJsonFromAssets(context, fileName)
            val aircraft = jsonReader.gson.fromJson(jsonString, Aircraft::class.java)
            if (aircraft != null) {
                cache[type] = aircraft
            }
            aircraft
        } catch (e: Exception) {
            Log.e("AirbusFactory", "Error loading aircraft $type from $fileName", e)
            null
        }
    }
}
