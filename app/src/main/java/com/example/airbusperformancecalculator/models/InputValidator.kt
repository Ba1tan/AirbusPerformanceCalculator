package com.example.airbusperformancecalculator.models

class InputValidator {
    fun validateInput(
        aircraft: Aircraft,
        flaps: String,
        gw: String,
        cg: String,
        qnh: String,
        temperature: String,
        elevation: String
    ): ValidationResult {
        try {
            if (listOf(flaps, gw, cg, qnh, temperature, elevation).any { it.isBlank() }) {
                return ValidationResult.Error("All fields must be filled")
            }

            val gwValue = gw.toIntOrNull()
            val emptyWeight = aircraft.info.weight.empty.toIntOrNull() ?: 40000
            val maxTakeoffWeight = aircraft.info.weight.maxTakeoff.toIntOrNull() ?: 80000
            if (gwValue == null || gwValue < emptyWeight || gwValue > maxTakeoffWeight) {
                return ValidationResult.Error("Invalid Gross Weight: Must be between $emptyWeight and $maxTakeoffWeight kg")
            }

            val cgValue = cg.toDoubleOrNull()
            val minCg = aircraft.info.trim.minCG
            val maxCg = aircraft.info.trim.maxCG
            if (cgValue == null || cgValue < minCg || cgValue > maxCg) {
                return ValidationResult.Error("Invalid CG: Must be between $minCg% and $maxCg% MAC")
            }

            val qnhValue = qnh.toIntOrNull()
            if (qnhValue == null || qnhValue < 900 || qnhValue > 1100) {
                return ValidationResult.Error("Invalid QNH: Must be between 900 and 1100 hPa")
            }

            val tempValue = temperature.toIntOrNull()
            if (tempValue == null || tempValue < -50 || tempValue > 60) {
                return ValidationResult.Error("Invalid Temperature: Must be between -50°C and 60°C")
            }

            val elevValue = elevation.toIntOrNull()
            if (elevValue == null || elevValue < -1000 || elevValue > 14000) {
                return ValidationResult.Error("Invalid Elevation: Must be between -1000 and 14000 ft")
            }

            val cleanFlaps = flaps.trim().uppercase()
            if (!listOf("1+F", "1", "2", "3").contains(cleanFlaps)) {
                return ValidationResult.Error("Invalid Flaps setting: Select 1+F, 2, or 3")
            }

            return ValidationResult.Success
        } catch (e: Exception) {
            return ValidationResult.Error("Invalid input: ${e.message}")
        }
    }
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
