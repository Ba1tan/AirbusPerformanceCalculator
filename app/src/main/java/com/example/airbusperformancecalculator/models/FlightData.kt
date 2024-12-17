package com.example.airbusperformancecalculator.models

class FlightData(private val aircraft: Aircraft, private val inputData: UserInput) {
    fun createDataMap(): Map<String, Any> {
        return mapOf(
            "type" to aircraft.type,
            "info" to aircraft.info,
            "flightDetails" to mapOf(
                "flapsConfiguration" to inputData.flaps,
                "grossWeight" to inputData.grossWeight,
                "QNH" to inputData.qnh,
                "runwayCondition" to inputData.runawayCond,
                "antiIce" to inputData.antiIce,
                "airConditioning" to inputData.airCond,
                "CG" to inputData.centerGravity,
            ),
            "airport" to mapOf(
                "elevation" to inputData.runawayElev,
                "temperature" to inputData.temperature
            )
        )
    }
}
