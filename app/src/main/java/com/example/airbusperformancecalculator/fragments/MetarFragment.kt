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
    private lateinit var iCAOText : EditText
    private lateinit var fetchButton : Button
    private lateinit var mETARText : TextView
    private lateinit var tAFText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.fragment_metar, container, false)
        iCAOText = view.findViewById(R.id.editTextICAO)
        fetchButton = view.findViewById(R.id.fetchButton)
        mETARText = view.findViewById(R.id.textMETAR)
        tAFText = view.findViewById(R.id.textTAF)

        fetchButton.setOnClickListener {
            val iCAO = iCAOText.text.toString().trim()
            if (iCAO.isNotEmpty()) {
                fetchWeatherData(iCAO)
            }
        }
        return view
    }
    private fun fetchWeatherData(iCAO: String) {
        weatherService.fetchWeatherData(iCAO, object : WeatherCallback {
            override fun onSuccess(response: String) {
                requireActivity().runOnUiThread{
                    try {
                        val jsonObject = JSONObject(response)
                        val mETARData = jsonObject.optString("metar", "No METAR data available")
                        val tAFData = jsonObject.optString("taf", "No TAF data available")

                        mETARText.text = getString(R.string.METAR_Info, mETARData)
                        tAFText.text = getString(R.string.TAF_Info, tAFData)
                    } catch (_: Exception) { mETARText.text = getString(R.string.Failed_METAR)
                        tAFText.text = getString(R.string.TAF_Error)
                    }
                }
            }

            override fun onFailure(errorMessage: String) {
                requireActivity().runOnUiThread{
                    mETARText.text = getString(R.string.Failed_METAR)
                    tAFText.text = getString(R.string.Failed_TAF)
                }
            }
        })
    }
}
