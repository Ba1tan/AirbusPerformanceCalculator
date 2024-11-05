package com.example.airbusperformancecalculator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
private val calculation = Calculation()

class CalcFragment : Fragment() {
    private lateinit var userFlaps : EditText
    private lateinit var userGW : EditText
    private lateinit var userQNH : EditText
    private lateinit var userCG : EditText
    private lateinit var userTemperature : EditText
    private lateinit var userAntiIce : EditText
    private lateinit var userAirCond : EditText
    private lateinit var userRunawayCond : EditText
    private lateinit var userElevation : EditText
    private lateinit var resV2 : TextView
    private lateinit var resV1 : TextView
    private lateinit var resVR : TextView
    private lateinit var resTrim : TextView
    private lateinit var resFLEX : TextView
    private lateinit var aircraftType : TextView
    private lateinit var aircraft : Aircraft

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        val view : View = inflater.inflate(R.layout.fragment_calc, container, false)
        userFlaps = view.findViewById(R.id.editTextFlaps)
        userGW = view.findViewById(R.id.editTextGW)
        userQNH = view.findViewById(R.id.editTextQNH)
        userCG = view.findViewById(R.id.editTextCG)
        userTemperature = view.findViewById(R.id.editTextTemperature)
        userAntiIce = view.findViewById(R.id.editTextAntiIce)
        userRunawayCond = view.findViewById(R.id.editTextRunawayCond)
        userElevation = view.findViewById(R.id.editTextRunawayElev)
        userAirCond = view.findViewById(R.id.editTextAirCond)
        resV2 = view.findViewById(R.id.textV2)
        resV1 = view.findViewById(R.id.textV1)
        resVR = view.findViewById(R.id.textVR)
        resFLEX = view.findViewById(R.id.textFLEX)
        resTrim = view.findViewById(R.id.textTHS)
        aircraftType = view.findViewById(R.id.textView1)
        val textInputLayout : TextInputLayout = view.findViewById(R.id.inputLayout)
        val autoCompleteTextView : AutoCompleteTextView = view.findViewById(R.id.inputTV)
        val calculateButton = view.findViewById<Button>(R.id.calculateButton)


        autoCompleteTextView.onItemClickListener= AdapterView.OnItemClickListener{
                parent, view, position, id -> val selectedItem = parent.getItemAtPosition(position) as String
            aircraftType.text = selectedItem
            Toast.makeText(requireContext(), "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            var aFactory = AirbusFactory()
            aircraft = aFactory.create(requireContext(), selectedItem)!!
        }
        calculateButton.setOnClickListener(){
            val inputData = UserInput(
                userFlaps.text.toString(),
                userGW.text.toString().toInt(),
                userCG.text.toString().toDouble(),
                userQNH.text.toString().toInt(),
                userTemperature.text.toString().toInt(),
                userAntiIce.text.toString(),
                userAirCond.text.toString(),
                userRunawayCond.text.toString(),
                userElevation.text.toString().toInt()
            )
            val flightData = FlightData(aircraft, inputData).createDataMap()
            val results = CalculationResults(calculation).calculateAll(flightData)

            resV1.text = results["V1"].toString()
            resVR.text = results["VR"].toString()
            resV2.text = results["V2"].toString()
            resTrim.text = results["Trim"].toString()
            resFLEX.text = results["FlexTemp"].toString()
        }
        return view
    }
}