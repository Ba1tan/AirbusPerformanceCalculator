package com.example.airbusperformancecalculator

import android.content.Context
import com.google.gson.Gson

class AirbusFactory() {
    private val jsonReader = AircraftJsonReader()
    private val A320_FILE = "A320-214.json"
    private val A321_FILE = "A321-211.json"
    private val A319_FILE = "A319-132.json"
    private val A320_NEO_FILE = "A320-251N.json"

    private fun createAircraftFromJson(context: Context, filePath: String): Aircraft? {
        val jsonString = jsonReader.readJsonFromAssets(context, filePath)
        val aircraft = jsonReader.gson.fromJson(jsonString, Aircraft::class.java)
        return aircraft
    }
    fun create(context: Context, type : String) : Aircraft?
    {
        return when(type){
            "A320-214" -> createAircraftFromJson(context, A320_FILE)
            "A320-251N" -> createAircraftFromJson(context, A320_NEO_FILE)
            "A321-211" -> createAircraftFromJson(context, A321_FILE)
            "A319-132" -> createAircraftFromJson(context, A319_FILE)
            else -> throw IllegalArgumentException("Unknown model")
        }

    }



}