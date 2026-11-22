package com.example.airbusperformancecalculator

import com.example.airbusperformancecalculator.models.*
import org.junit.Assert.*
import org.junit.Test

class CalculationTest {

    private val calculation = Calculation()

    @Test
    fun testTrimPitchFormat() {
        val trimUp = calculation.calculateTrim(
            minCg = 17.0,
            maxCg = 40.0,
            minTrim = 3.8,
            maxTrim = -2.5,
            currentCg = 25.0,
            interpolationFactor = 1.0
        )
        assertTrue("Trim should start with UP or DN", trimUp.startsWith("UP") || trimUp.startsWith("DN"))
    }

    @Test
    fun testTakeoffCalculationA320() {
        val mockAircraft = Aircraft(
            type = "A320-214",
            info = Info(
                manufacturer = "Airbus",
                model = "A320",
                engines = emptyList(),
                dimensions = Dimensions("37.57 m", "35.80 m", "11.76 m", "3.70 m", "122.4 m²"),
                weight = Weight("42600", "78000", "64500", "64300"),
                fuelCapacity = FuelCapacity("24210 L", "12220 L"),
                flapConfigurations = emptyList(),
                features = Features(listOf("Engine"), true),
                temperatureFactor = 48,
                trim = Trim(1, 40, 17, 3.8, -2.5),
                speeds = Speeds(
                    flapsRetr = emptyMap(),
                    slatsRetr = emptyMap(),
                    VSpeeds = VSpeeds(
                        v1factor = 5,
                        vrfactor = 4,
                        `1` = mapOf("50" to 128, "60" to 138, "70" to 149),
                        `2` = mapOf("50" to 125, "60" to 135, "70" to 145),
                        `3` = mapOf("50" to 122, "60" to 132, "70" to 142)
                    )
                )
            )
        )

        val input = TakeoffInput(
            aircraft = mockAircraft,
            flaps = "1+F",
            grossWeightKg = 64000,
            centerOfGravity = 25.0,
            qnhHpa = 1013,
            temperatureCelsius = 15,
            antiIce = "OFF",
            airConditioning = "ON",
            runwayCondition = "DRY",
            runwayElevationFt = 0
        )

        val result = calculation.calculate(input)

        assertTrue("V2 should be positive", result.v2 > 100)
        assertTrue("V1 <= VR <= V2", result.v1 <= result.vr && result.vr <= result.v2)
        assertTrue("Flex temp should be realistic", result.flexTemp in 30..80)
        assertTrue("Trim should have UP or DN", result.thsTrim.contains("UP") || result.thsTrim.contains("DN"))
    }
}
