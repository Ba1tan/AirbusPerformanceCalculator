package com.example.airbusperformancecalculator.fragments

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
import com.example.airbusperformancecalculator.models.AirbusFactory
import com.example.airbusperformancecalculator.models.Calculation
import com.example.airbusperformancecalculator.models.CalculationResults
import com.example.airbusperformancecalculator.R
import com.example.airbusperformancecalculator.models.Aircraft
import com.example.airbusperformancecalculator.models.FlightData
import com.example.airbusperformancecalculator.models.UserInput
import com.example.airbusperformancecalculator.models.InputValidator
import com.example.airbusperformancecalculator.models.ValidationResult

private val calculation = Calculation()
private val inputValidator = InputValidator()

class CalcFragment : Fragment() {
    private lateinit var userFlaps : EditText
    private lateinit var userGW : EditText
    private lateinit var userQNH : EditText
    private lateinit var userCG : EditText
    private lateinit var userTemperature : EditText
    private lateinit var userAntiIce : AutoCompleteTextView
    private lateinit var userAirCond : AutoCompleteTextView
    private lateinit var userRunawayCond : AutoCompleteTextView
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
        userAntiIce = view.findViewById(R.id.inputAI)
        userRunawayCond = view.findViewById(R.id.inputRNW)
        userElevation = view.findViewById(R.id.editTextRunawayElev)
        userAirCond = view.findViewById(R.id.inputAC)
        resV2 = view.findViewById(R.id.textV2)
        resV1 = view.findViewById(R.id.textV1)
        resVR = view.findViewById(R.id.textVR)
        resFLEX = view.findViewById(R.id.textFLEX)
        resTrim = view.findViewById(R.id.textTHS)
        aircraftType = view.findViewById(R.id.textView1)
        var aFactory = AirbusFactory()
        var selectedAircraft = "A320-214"
        aircraftType.text = selectedAircraft
        aircraft = aFactory.create(requireContext(), selectedAircraft)!!
        //val textInputLayout : TextInputLayout = view.findViewById(R.id.inputLayout)
        val autoCompleteTextViewAircraft : AutoCompleteTextView = view.findViewById(R.id.inputTV)
        val calculateButton = view.findViewById<Button>(R.id.calculateButton)


        autoCompleteTextViewAircraft.onItemClickListener = AdapterView.OnItemClickListener{
                parent, view, position, id -> selectedAircraft = parent.getItemAtPosition(position) as String
            aircraftType.text = selectedAircraft
            Toast.makeText(requireContext(), "Selected: $selectedAircraft", Toast.LENGTH_SHORT).show()
            aircraft = aFactory.create(requireContext(), selectedAircraft)!!
        }
        calculateButton.setOnClickListener{
            val validationResult = inputValidator.validateInput(
                aircraft,
                userFlaps.text.toString(),
                userGW.text.toString(),
                userCG.text.toString(),
                userQNH.text.toString(),
                userTemperature.text.toString(),
                userElevation.text.toString()
            )

            when (validationResult) {
                is ValidationResult.Success -> {
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
                is ValidationResult.Error -> {
                    Toast.makeText(requireContext(), validationResult.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        return view
    }
}