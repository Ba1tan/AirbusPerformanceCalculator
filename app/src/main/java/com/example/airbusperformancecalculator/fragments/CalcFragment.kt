package com.example.airbusperformancecalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.airbusperformancecalculator.databinding.FragmentCalcBinding
import com.example.airbusperformancecalculator.models.ValidationResult
import com.example.airbusperformancecalculator.viewmodels.AirbusViewModel
import kotlinx.coroutines.launch

class CalcFragment : Fragment() {

    private var _binding: FragmentCalcBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AirbusViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAircraftSelector()
        setupCalculateButton()
        observeViewModel()
    }

    private fun setupAircraftSelector() {
        binding.inputTV.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val selectedAircraft = parent.getItemAtPosition(position) as String
            viewModel.selectAircraft(selectedAircraft)
            Toast.makeText(requireContext(), "Selected: $selectedAircraft", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCalculateButton() {
        binding.calculateButton.setOnClickListener {
            val validation = viewModel.calculateTakeoff(
                flaps = binding.editTextFlaps.text.toString(),
                gw = binding.editTextGW.text.toString(),
                cg = binding.editTextCG.text.toString(),
                qnh = binding.editTextQNH.text.toString(),
                temp = binding.editTextTemperature.text.toString(),
                elevation = binding.editTextRunawayElev.text.toString(),
                antiIce = binding.inputAI.text.toString(),
                airCond = binding.inputAC.text.toString(),
                runwayCond = binding.inputRNW.text.toString()
            )

            if (validation is ValidationResult.Error) {
                Toast.makeText(requireContext(), validation.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedAircraft.collect { aircraft ->
                        if (aircraft != null) {
                            binding.textView1.text = aircraft.type
                            // Update defaults if needed for the model
                            val emptyWeight = aircraft.info.weight.empty.toIntOrNull() ?: 42000
                            val mtow = aircraft.info.weight.maxTakeoff.toIntOrNull() ?: 78000
                            val midWeight = ((emptyWeight + mtow) / 2)
                            val currentGw = binding.editTextGW.text.toString().toIntOrNull()
                            if (currentGw == null || currentGw < emptyWeight || currentGw > mtow) {
                                binding.editTextGW.setText(midWeight.toString())
                            }
                        }
                    }
                }

                launch {
                    viewModel.calculationResult.collect { result ->
                        if (result != null) {
                            binding.textV1.text = result.v1.toString()
                            binding.textVR.text = result.vr.toString()
                            binding.textV2.text = result.v2.toString()
                            binding.textTHS.text = result.thsTrim
                            binding.textFLEX.text = "${result.flexTemp}°C"
                        }
                    }
                }

                launch {
                    viewModel.appliedWeather.collect { weather ->
                        weather.temperatureCelsius?.let { temp ->
                            binding.editTextTemperature.setText(temp.toString())
                        }
                        weather.qnhHpa?.let { qnh ->
                            binding.editTextQNH.setText(qnh.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
