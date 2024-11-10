package com.example.airbusperformancecalculator

class CalculationResults(private val calculation: Calculation) {
    fun calculateAll(data: Map<String, Any>): Map<String, Any> {
        val v2 = calculation.calculateV2(data)
        val aircraftDetail = data["info"] as Info
        val flightDetails = data["flightDetails"] as Map<*, *>
        val cg = flightDetails["CG"]
        val v1 = calculation.calculateV1(v2, aircraftDetail.speeds.VSpeeds.v1factor)
        val vr = calculation.calculateVR(v2, aircraftDetail.speeds.VSpeeds.vrfactor)
        val trimInfo = aircraftDetail.trim
        val myTrim = calculation.calculateTrim(
            trimInfo.minCG.toDouble(),
            trimInfo.maxCG.toDouble(),
            trimInfo.max,
            trimInfo.min,
            cg.toString().toDouble(),
            trimInfo.interpolation.toDouble()
        )
        val flexTemp = calculation.calculateFlexTemp(data)

        return mapOf(
            "V1" to v1,
            "VR" to vr,
            "V2" to v2,
            "Trim" to myTrim,
            "FlexTemp" to flexTemp
        )
    }
}
