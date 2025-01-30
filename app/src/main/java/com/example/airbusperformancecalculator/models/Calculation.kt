package com.example.airbusperformancecalculator.models

import android.util.Log
import kotlin.collections.get
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.let
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.text.toDouble
import kotlin.text.toInt
import kotlin.toString

class Calculation
{
    fun calculateV2(data: Map<String, Any>): Int {
        val flightDetails = data["flightDetails"] as Map<*, *>
        val aircraftInfo = data["info"] as Info
        val origm = (flightDetails["grossWeight"]).toString().toDouble() / 1000.0
        val aircmass = aircraftInfo.weight.empty.toInt()
        var mass = round(origm)
        if (mass == 0.0) mass = 64.3

        val flapConfig = flightDetails["flapsConfiguration"] as String
        var fPos = 1

        when (flapConfig) {
            "1" -> fPos = 1
            "1+F" -> fPos = 1
            "2" -> fPos = 2
            "3" -> fPos = 3
        }
        Log.d("Value", "$fPos")

        val airportData = data["airport"] as Map<*, *>
        val alt = airportData["elevation"] as Int
        var staticV2: Double = 0.0
        Log.d("Value", "$mass")
        if(fPos == 1)
        {
            staticV2 = round((aircraftInfo.speeds.VSpeeds.`1`[round5down(mass).toString()]?: 0).toDouble())
            if (staticV2 == 0.0)
            {
                staticV2 = round((aircraftInfo.speeds.VSpeeds.`1`[round10down(mass).toString()] ?: 0).toDouble())
            }
        }
        if(fPos == 2)
        {
            staticV2 = round((aircraftInfo.speeds.VSpeeds.`2`[round5down(mass).toString()]
                ?: 0).toDouble())
            if (staticV2 == 0.0)
            {
                staticV2 = round((aircraftInfo.speeds.VSpeeds.`2`[round10down(mass).toString()]
                    ?: 0).toDouble())
            }
        }
        if(fPos == 3) {
            staticV2 = round(
                (aircraftInfo.speeds.VSpeeds.`3`[round5down(mass).toString()] ?: 0).toDouble()
            )
            if (staticV2 == 0.0) {
                staticV2 = round(
                    (aircraftInfo.speeds.VSpeeds.`3`[round10down(mass).toString()] ?: 0).toDouble()
                )
            }
        }
        Log.d("Value", "$staticV2")
        Log.d("Value", "${round5down(mass)}")


        if (aircmass / 1000 < 60) {
            staticV2 += f2corr(fPos, alt)
            staticV2 = (staticV2 - 1) + (mass - 40) * 18 / 40
        }

        flightDetails["runwayCondition"]?.let {
            if (it == "WET") staticV2 += 4
            if (it == "LOW") staticV2 += 6
        }
        Log.d("Value", "$staticV2")

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
        var flexTemp = (data["airport"] as Map<*, *>)["temperature"] as Int? ?: 15
        val plusTemp = aircraftInfo.temperatureFactor
        val qnhHp = (data["flightDetails"] as Map<*, *>)["QNH"] as Int
        var qnhInHg = convertQnhHgToHp(qnhHp.toDouble())
        qnhInHg /= 33.8639

        val qnhDifference = qnhInHg - 29.92

        if (qnhDifference >= 0) {
            flexTemp += floor(qnhDifference / 0.3).toInt()
        }

        if (qnhDifference < 0) {
            flexTemp -= floor(abs(qnhDifference) / 0.1).toInt()
        }

        if ((data["flightDetails"] as Map<*, *>)["antiIce"] == "ON") {
            flexTemp -= 2
        }

        if ((data["flightDetails"] as Map<*, *>)["airConditioning"] == "OFF") {
            flexTemp -= 3
        }

        flexTemp = round(flexTemp.toDouble()).toInt() + plusTemp

        return flexTemp
    }
    private fun convertQnhHgToHp(qnhHg: Double): Double {
        return qnhHg / 0.99
    }
    fun round5down(x: Double): Int {
        return (floor(x / 5) * 5).toInt()
    }

    fun round10down(x: Double): Int {
        return (floor(x / 10) * 10).toInt()
    }

    fun f2corr(f: Int, a: Int): Int {
        return if (f == 2) (abs(a * 2e-4)).toInt() else 0
    }

    fun calculateTrim(
        keyInicial: Double,
        keyMax: Double,
        valorInicial: Double,
        maxVal: Double,
        point: Double,
        speed: Double = 1.0
    ): String {
        val velocidadNormalizada = max(0.0, min(2.0, speed))

        val proporcion = if (velocidadNormalizada == 0.0) {
            0.0
        } else {
            (point - keyInicial) / (keyMax - keyInicial).pow(
                (1 / speed.pow(speed)) / velocidadNormalizada
            )
        }

        val valorCalculado = valorInicial + proporcion * (maxVal - valorInicial)
        val valorFinal = (valorCalculado * 10).roundToInt() / 10.0

        return if (valorFinal >= 0) {
            "UP${valorFinal.toString().replace("+", "")}"
        } else {
            "DN${valorFinal.toString().replace("-", "")}"
        }
    }

}
