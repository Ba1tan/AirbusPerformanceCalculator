package com.example.airbusperformancecalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.airbusperformancecalculator.APIServices.MetarService
import com.example.airbusperformancecalculator.R
import com.example.airbusperformancecalculator.interfaces.WeatherCallback
import org.json.JSONObject

class MetarFragment : Fragment() {
    private val weatherService = MetarService()
    private lateinit var icaoText : EditText
    private lateinit var fetchButton : Button
    private lateinit var metarText : TextView
    private lateinit var tafText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.fragment_metar, container, false)
        icaoText = view.findViewById(R.id.editTextICAO)
        fetchButton = view.findViewById(R.id.fetchButton)
        metarText = view.findViewById(R.id.textMETAR)
        tafText = view.findViewById(R.id.textTAF)

        fetchButton.setOnClickListener {
            val icao = icaoText.text.toString().trim()
            if (icao.isNotEmpty()) {
                fetchWeatherData(icao)
            }
        }
        return view
    }
    private fun fetchWeatherData(icao: String) {
        weatherService.fetchWeatherData(icao, object : WeatherCallback {
            override fun onSuccess(response: String) {
                requireActivity().runOnUiThread{
                    try {
                        val jsonObject = JSONObject(response)
                        val metarData = jsonObject.optString("metar", "No METAR data available")
                        val tafData = jsonObject.optString("taf", "No TAF data available")

                        metarText.text = "METAR Data:\n$metarData"
                        tafText.text = "TAF Data:\n$tafData"
                    } catch (e: Exception) {
                        metarText.text = "Error parsing METAR data."
                        tafText.text = "Error parsing TAF data."
                    }
                }
            }

            override fun onFailure(errorMessage: String) {
                requireActivity().runOnUiThread{
                    metarText.text = "Failed to fetch METAR data."
                    tafText.text = "Failed to fetch TAF data."
                }
            }
        })
    }
}
