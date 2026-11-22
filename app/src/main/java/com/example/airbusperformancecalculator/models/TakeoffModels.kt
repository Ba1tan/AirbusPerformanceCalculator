package com.example.airbusperformancecalculator.models

data class TakeoffInput(
    val aircraft: Aircraft,
    val flaps: String,
    val grossWeightKg: Int,
    val centerOfGravity: Double,
    val qnhHpa: Int,
    val temperatureCelsius: Int,
    val antiIce: String,
    val airConditioning: String,
    val runwayCondition: String,
    val runwayElevationFt: Int
)

data class TakeoffResult(
    val v1: Int,
    val vr: Int,
    val v2: Int,
    val thsTrim: String,
    val flexTemp: Int
)

data class DecodedWeather(
    val icao: String,
    val rawMetar: String,
    val rawTaf: String,
    val temperatureCelsius: Int?,
    val qnhHpa: Int?,
    val windString: String?,
    val windDirection: Int? = null,
    val windSpeed: Int? = null,
    val windUnit: String? = null,
    val isWindVariable: Boolean = false
)
