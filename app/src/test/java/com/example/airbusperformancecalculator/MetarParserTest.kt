package com.example.airbusperformancecalculator

import com.example.airbusperformancecalculator.APIServices.MetarParser
import org.junit.Assert.*
import org.junit.Test

class MetarParserTest {

    @Test
    fun testParseUaccKazakhstanMetarWithMpsAndMoTypos() {
        val rawMetar = """
            2026-11-22 02:00:00
            METAR UACC 220200Z 15003MPS 9000 NSC MO02/
            MO2 Q1017 NOSIG RMK QFE732/0976
        """.trimIndent()
        val taf = ""
        val decoded = MetarParser.parse("UACC", rawMetar, taf)

        assertEquals("UACC", decoded.icao)
        assertEquals(150, decoded.windDirection)
        assertEquals(3, decoded.windSpeed)
        assertEquals("MPS", decoded.windUnit)
        assertFalse(decoded.isWindVariable)
        assertTrue(decoded.windString!!.startsWith("150° @ 3 MPS"))
        assertEquals(-2, decoded.temperatureCelsius)
        assertEquals(1017, decoded.qnhHpa)
    }

    @Test
    fun testParseStandardEuropeanMetar() {
        val metar = "EGLL 220650Z 24012KT 9999 FEW025 14/08 Q1018 NOSIG"
        val taf = "EGLL 220500Z 2206/2312 24015KT 9999 BKN020"
        val decoded = MetarParser.parse("EGLL", metar, taf)

        assertEquals("EGLL", decoded.icao)
        assertEquals(240, decoded.windDirection)
        assertEquals(12, decoded.windSpeed)
        assertEquals("KT", decoded.windUnit)
        assertEquals("240° @ 12 KT", decoded.windString)
        assertEquals(14, decoded.temperatureCelsius)
        assertEquals(1018, decoded.qnhHpa)
    }

    @Test
    fun testParseNegativeTemperatureAndUsAltimeter() {
        val metar = "KJFK 220651Z 31015G25KT 10SM CLR M04/M12 A3012 RMK AO2"
        val taf = ""
        val decoded = MetarParser.parse("KJFK", metar, taf)

        assertEquals("KJFK", decoded.icao)
        assertEquals(310, decoded.windDirection)
        assertEquals(15, decoded.windSpeed)
        assertEquals("KT", decoded.windUnit)
        assertEquals("310° @ 15G25 KT", decoded.windString)
        assertEquals(-4, decoded.temperatureCelsius)
        // 30.12 * 33.8639 = 1019.98 -> 1020 hPa
        assertEquals(1020, decoded.qnhHpa)
    }
}
