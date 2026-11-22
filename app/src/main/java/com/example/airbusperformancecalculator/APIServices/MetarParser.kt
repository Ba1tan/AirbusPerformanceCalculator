package com.example.airbusperformancecalculator.APIServices

import com.example.airbusperformancecalculator.models.DecodedWeather
import kotlin.math.roundToInt

object MetarParser {
    // Matches wind: e.g. 15003MPS, 24012KT, VRB02MPS, 18015G25KT, 00000KT
    private val windPattern = Regex("""\b(\d{3}|VRB)(\d{2,3})(?:G(\d{2,3}))?(KT|MPS|KMH)\b""", RegexOption.IGNORE_CASE)

    // Matches variable wind direction range: e.g. 110V190
    private val windVarRangePattern = Regex("""\b(\d{3})V(\d{3})\b""", RegexOption.IGNORE_CASE)

    // Matches temperature and dew point:
    // Handles 14/08, M04/M12, MO02/MO2, MO02/\nMO2, 15///, 00/M02, etc.
    // Supports 'O' / 'o' typos common in regional/CIS teleprinter feeds
    private val tempDewPattern = Regex(
        """\b(M[Oo0]?|[Oo0])?([0-9Oo]{1,2})\s*/(?:\s*(M[Oo0]?|[Oo0])?([0-9Oo]{1,2}|/{1,2}))?\b"""
    )

    // Matches QNH: e.g. Q1017, Q1013, Q1O17
    private val qnhPattern = Regex("""\bQ([0-9Oo]{4})\b""", RegexOption.IGNORE_CASE)

    // Matches US/North American Altimeter: e.g. A2992, A3012
    private val altimeterPattern = Regex("""\bA([0-9Oo]{4})\b""", RegexOption.IGNORE_CASE)

    fun parse(icao: String, metar: String, taf: String): DecodedWeather {
        var temperature: Int? = null
        var qnh: Int? = null
        var windString: String? = null
        var windDirection: Int? = null
        var windSpeed: Int? = null
        var windUnit: String? = null
        var isWindVariable = false

        // 1. Wind Parsing
        windPattern.find(metar)?.let { match ->
            val dirStr = match.groupValues[1].uppercase()
            val speedVal = match.groupValues[2].toIntOrNull() ?: 0
            val gustStr = match.groupValues[3].ifEmpty { null }
            val unitStr = match.groupValues[4].uppercase()

            windUnit = unitStr
            windSpeed = speedVal

            if (dirStr == "VRB") {
                isWindVariable = true
            } else {
                windDirection = dirStr.toIntOrNull()
            }

            // Variable range (e.g. 110V190)
            val varRange = windVarRangePattern.find(metar)?.value

            // Format friendly wind representation
            windString = if (dirStr == "000" && speedVal == 0) {
                "Calm (000/0 $unitStr)"
            } else {
                val dirDisplay = if (isWindVariable) "VRB" else "${dirStr}°"
                val gustDisplay = if (gustStr != null) "G$gustStr" else ""
                val varDisplay = if (varRange != null) " ($varRange)" else ""

                // Convert MPS to KT equivalent for pilot convenience
                val equivDisplay = if (unitStr == "MPS") {
                    val ktEquiv = (speedVal * 1.94384).roundToInt()
                    val gustKtEquiv = gustStr?.toIntOrNull()?.let { "G${(it * 1.94384).roundToInt()}" } ?: ""
                    " (~${ktEquiv}${gustKtEquiv} KT)"
                } else {
                    ""
                }

                "$dirDisplay @ ${speedVal}${gustDisplay} $unitStr$equivDisplay$varDisplay"
            }
        }

        // 2. Temperature Parsing
        tempDewPattern.find(metar)?.let { match ->
            val prefix = match.groupValues[1].uppercase()
            val rawDigits = match.groupValues[2].replace('O', '0').replace('o', '0')
            val tempVal = rawDigits.toIntOrNull()
            if (tempVal != null) {
                val isMinus = prefix.startsWith("M")
                temperature = if (isMinus) -tempVal else tempVal
            }
        }

        // 3. QNH / Altimeter Parsing
        qnhPattern.find(metar)?.let { match ->
            val rawQnh = match.groupValues[1].replace('O', '0').replace('o', '0')
            qnh = rawQnh.toIntOrNull()
        } ?: run {
            altimeterPattern.find(metar)?.let { match ->
                val rawAltimeter = match.groupValues[1].replace('O', '0').replace('o', '0')
                val inchesHgHundredths = rawAltimeter.toDoubleOrNull()
                if (inchesHgHundredths != null) {
                    val inHg = inchesHgHundredths / 100.0
                    qnh = (inHg * 33.8639).roundToInt()
                }
            }
        }

        return DecodedWeather(
            icao = icao.uppercase(),
            rawMetar = metar,
            rawTaf = taf,
            temperatureCelsius = temperature,
            qnhHpa = qnh,
            windString = windString,
            windDirection = windDirection,
            windSpeed = windSpeed,
            windUnit = windUnit,
            isWindVariable = isWindVariable
        )
    }
}
