package com.example.airbusperformancecalculator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.airbusperformancecalculator.MainActivity
import com.example.airbusperformancecalculator.R
import com.example.airbusperformancecalculator.databinding.FragmentMetarBinding
import com.example.airbusperformancecalculator.viewmodels.AirbusViewModel
import kotlinx.coroutines.launch

class MetarFragment : Fragment() {

    private var _binding: FragmentMetarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AirbusViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMetarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fetchButton.setOnClickListener {
            val icao = binding.editTextICAO.text.toString().trim()
            if (icao.isNotEmpty()) {
                viewModel.fetchWeather(icao)
            } else {
                Toast.makeText(requireContext(), "Please enter an ICAO code", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnApplyWeather.setOnClickListener {
            val weather = viewModel.weatherState.value
            if (weather != null) {
                viewModel.applyWeatherToCalculator(weather)
                Toast.makeText(requireContext(), getString(R.string.weather_applied), Toast.LENGTH_SHORT).show()
                (activity as? MainActivity)?.navigateToCalculator()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isWeatherLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.fetchButton.isEnabled = !isLoading
                    }
                }

                launch {
                    viewModel.weatherError.collect { error ->
                        if (error != null) {
                            binding.textError.visibility = View.VISIBLE
                            binding.textError.text = error
                            binding.cardDecoded.visibility = View.GONE
                        } else {
                            binding.textError.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.weatherState.collect { weather ->
                        if (weather != null) {
                            binding.textMETAR.text = weather.rawMetar
                            binding.textTAF.text = weather.rawTaf

                            binding.textDecodedWind.text = weather.windString ?: "N/A"
                            binding.textDecodedTemp.text = weather.temperatureCelsius?.let { "$it°C" } ?: "N/A"
                            binding.textDecodedQnh.text = weather.qnhHpa?.let { "$it hPa" } ?: "N/A"

                            binding.cardDecoded.visibility = View.VISIBLE
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
