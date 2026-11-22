package com.example.airbusperformancecalculator.models

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

class Calculation {

    fun calculate(input: TakeoffInput): TakeoffResult {
        val aircraft = input.aircraft
        val info = aircraft.info
        val massTonnes = input.grossWeightKg / 1000.0

        val fPos = when (input.flaps) {
            "1", "1+F" -> 1
            "2" -> 2
            "3" -> 3
            else -> 1
        }

        val speedMap: Map<String, Int> = when (fPos) {
            1 -> info.speeds.VSpeeds.`1`
            2 -> info.speeds.VSpeeds.`2`
            3 -> info.speeds.VSpeeds.`3`
            else -> info.speeds.VSpeeds.`1`
        }

        var staticV2 = interpolateSpeed(speedMap, massTonnes)

        val emptyMassTonnes = (info.weight.empty.toDoubleOrNull() ?: 42000.0) / 1000.0
        if (emptyMassTonnes < 60) {
            staticV2 += f2corr(fPos, input.runwayElevationFt)
            staticV2 = (staticV2 - 1) + (massTonnes - 40) * 18 / 40
        }

        when (input.runwayCondition.uppercase()) {
            "WET" -> staticV2 += 4
            "LOW" -> staticV2 += 6
        }

        val v2 = ceil(staticV2).toInt()
        val v1 = calculateV1(v2, info.speeds.VSpeeds.v1factor)
        val vr = calculateVR(v2, info.speeds.VSpeeds.vrfactor)

        val trimInfo = info.trim
        val trimString = calculateTrim(
            minCg = trimInfo.minCG.toDouble(),
            maxCg = trimInfo.maxCG.toDouble(),
            minTrim = trimInfo.max,
            maxTrim = trimInfo.min,
            currentCg = input.centerOfGravity,
            interpolationFactor = trimInfo.interpolation.toDouble()
        )

        val flexTemp = computeFlexTemp(
            baseAirportTemp = input.temperatureCelsius,
            tempFactor = info.temperatureFactor,
            qnhHpa = input.qnhHpa,
            antiIce = input.antiIce,
            airConditioning = input.airConditioning
        )

        return TakeoffResult(
            v1 = v1,
            vr = vr,
            v2 = v2,
            thsTrim = trimString,
            flexTemp = flexTemp
        )
    }

    private fun interpolateSpeed(speedMap: Map<String, Int>, massTonnes: Double): Double {
        if (speedMap.isEmpty()) return 135.0
        val sortedEntries = speedMap.mapNotNull { (k, v) ->
            k.toIntOrNull()?.let { it to v.toDouble() }
        }.sortedBy { it.first }

        if (sortedEntries.isEmpty()) return 135.0
        if (massTonnes <= sortedEntries.first().first) return sortedEntries.first().second
        if (massTonnes >= sortedEntries.last().first) return sortedEntries.last().second

        for (i in 0 until sortedEntries.size - 1) {
            val (w1, s1) = sortedEntries[i]
            val (w2, s2) = sortedEntries[i + 1]
            if (massTonnes in w1.toDouble()..w2.toDouble()) {
                val ratio = (massTonnes - w1) / (w2 - w1)
                return s1 + ratio * (s2 - s1)
            }
        }
        return sortedEntries.last().second
    }

    private fun computeFlexTemp(
        baseAirportTemp: Int,
        tempFactor: Int,
        qnhHpa: Int,
        antiIce: String,
        airConditioning: String
    ): Int {
        var flexTemp = baseAirportTemp
        var qnhInHg = (qnhHpa / 0.99) / 33.8639
        val qnhDifference = qnhInHg - 29.92

        if (qnhDifference >= 0) {
            flexTemp += floor(qnhDifference / 0.3).toInt()
        } else {
            flexTemp -= floor(abs(qnhDifference) / 0.1).toInt()
        }

        if (antiIce == "ON") {
            flexTemp -= 2
        }

        if (airConditioning == "OFF") {
            flexTemp -= 3
        }

        return round(flexTemp.toDouble()).toInt() + tempFactor
    }

    fun calculateV2(data: Map<String, Any>): Int {
        val flightDetails = data["flightDetails"] as Map<*, *>
        val aircraftInfo = data["info"] as Info
        val origm = (flightDetails["grossWeight"]).toString().toDouble() / 1000.0
        val aircmass = aircraftInfo.weight.empty.toInt()
        var mass = round(origm)
        if (mass == 0.0) mass = 64.3

        val flapConfig = flightDetails["flapsConfiguration"] as String
        val fPos = when (flapConfig) {
            "1", "1+F" -> 1
            "2" -> 2
            "3" -> 3
            else -> 1
        }

        val airportData = data["airport"] as Map<*, *>
        val alt = airportData["elevation"] as Int

        val speedMap = when (fPos) {
            1 -> aircraftInfo.speeds.VSpeeds.`1`
            2 -> aircraftInfo.speeds.VSpeeds.`2`
            3 -> aircraftInfo.speeds.VSpeeds.`3`
            else -> aircraftInfo.speeds.VSpeeds.`1`
        }

        var staticV2 = interpolateSpeed(speedMap, mass)

        if (aircmass / 1000 < 60) {
            staticV2 += f2corr(fPos, alt)
            staticV2 = (staticV2 - 1) + (mass - 40) * 18 / 40
        }

        flightDetails["runwayCondition"]?.let {
            if (it == "WET") staticV2 += 4
            if (it == "LOW") staticV2 += 6
        }

        return ceil(staticV2).toInt()
    }

    fun calculateV1(v2: Int, factor: Int? = null): Int {
        var v1 = v2 - 5
        factor?.let { v1 -= it }
        return v1
    }

    fun calculateVR(v2: Int, factor: Int? = null): Int {
        var vr = v2 - 4
        factor?.let { vr -= it }
        return vr
    }

    fun calculateFlexTemp(data: Map<String, Any>): Int {
        val aircraftInfo = data["info"] as Info
        val airport = data["airport"] as Map<*, *>
        val flightDetails = data["flightDetails"] as Map<*, *>

        val temp = airport["temperature"] as? Int ?: 15
        val qnh = flightDetails["QNH"] as? Int ?: 1013
        val antiIce = flightDetails["antiIce"] as? String ?: "OFF"
        val airCond = flightDetails["airConditioning"] as? String ?: "ON"

        return computeFlexTemp(temp, aircraftInfo.temperatureFactor, qnh, antiIce, airCond)
    }

    fun round5down(x: Double): Int = (floor(x / 5) * 5).toInt()

    fun round10down(x: Double): Int = (floor(x / 10) * 10).toInt()

    fun f2corr(f: Int, a: Int): Int = if (f == 2) (abs(a * 2e-4)).toInt() else 0

    fun calculateTrim(
        minCg: Double,
        maxCg: Double,
        minTrim: Double,
        maxTrim: Double,
        currentCg: Double,
        interpolationFactor: Double = 1.0
    ): String {
        val normalizedFactor = max(0.01, min(2.0, interpolationFactor))
        val proportion = if (maxCg == minCg) {
            0.0
        } else {
            (currentCg - minCg) / (maxCg - minCg).pow((1.0 / normalizedFactor.pow(normalizedFactor)) / normalizedFactor)
        }

        val calculatedTrim = minTrim + proportion * (maxTrim - minTrim)
        val finalTrim = (calculatedTrim * 10).roundToInt() / 10.0

        return if (finalTrim >= 0) {
            "UP ${finalTrim}"
        } else {
            "DN ${abs(finalTrim)}"
        }
    }
}
