package com.example.airbusperformancecalculator.models


class InputValidator {
    fun validateInput(aircraft: Aircraft, flaps: String, gw: String, cg: String, qnh: String, temperature: String, elevation: String): ValidationResult {
        try {
            if (listOf(flaps, gw, cg, qnh, temperature, elevation)
                    .any { it.isBlank() }) {
                return ValidationResult.Error("All fields must be filled")
            }

            val gwValue = gw.toIntOrNull()
            if (gwValue == null || gwValue < aircraft.info.weight.empty.toInt() || gwValue > aircraft.info.weight.maxTakeoff.toInt()) {
                return ValidationResult.Error("Invalid Gross Weight: Must be between ${aircraft.info.weight.empty.toInt()} and ${aircraft.info.weight.maxTakeoff.toInt()}")
            }

            val cgValue = cg.toDoubleOrNull()
            if (cgValue == null || cgValue < aircraft.info.trim.minCG.toInt() || cgValue > aircraft.info.trim.maxCG.toInt()) {
                return ValidationResult.Error("Invalid CG: Must be between ${aircraft.info.trim.minCG.toInt()} and ${aircraft.info.trim.maxCG.toInt()}")
            }

            val qnhValue = qnh.toIntOrNull()
            if (qnhValue == null || qnhValue < 900 || qnhValue > 1100) {
                return ValidationResult.Error("Invalid QNH: Must be between 900 and 1100")
            }

            val tempValue = temperature.toIntOrNull()
            if (tempValue == null || tempValue < -50 || tempValue > 60) {
                return ValidationResult.Error("Invalid Temperature: Must be between -50°C and 60°C")
            }

            val elevValue = elevation.toIntOrNull()
            if (elevValue == null || elevValue < -1000 || elevValue > 14000) {
                return ValidationResult.Error("Invalid Elevation: Must be between -1000 and 14000 feet")
            }

            if (!listOf("1+F","1", "2", "3").contains(flaps)) {
                return ValidationResult.Error("Invalid Flaps setting: Must be 1, 2, or 3")
            }


            return ValidationResult.Success
        } catch (e: Exception) {
            return ValidationResult.Error("Invalid input: ${e.message}")
        }
    }
}
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}