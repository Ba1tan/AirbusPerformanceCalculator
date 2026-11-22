package com.example.airbusperformancecalculator.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.airbusperformancecalculator.APIServices.MetarService
import com.example.airbusperformancecalculator.models.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AirbusViewModel(application: Application) : AndroidViewModel(application) {

    private val factory = AirbusFactory()
    private val calculation = Calculation()
    private val validator = InputValidator()
    private val metarService = MetarService()

    private val _selectedAircraft = MutableStateFlow<Aircraft?>(null)
    val selectedAircraft: StateFlow<Aircraft?> = _selectedAircraft.asStateFlow()

    private val _calculationResult = MutableStateFlow<TakeoffResult?>(null)
    val calculationResult: StateFlow<TakeoffResult?> = _calculationResult.asStateFlow()

    private val _weatherState = MutableStateFlow<DecodedWeather?>(null)
    val weatherState: StateFlow<DecodedWeather?> = _weatherState.asStateFlow()

    private val _isWeatherLoading = MutableStateFlow(false)
    val isWeatherLoading: StateFlow<Boolean> = _isWeatherLoading.asStateFlow()

    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError: StateFlow<String?> = _weatherError.asStateFlow()

    private val _appliedWeather = MutableSharedFlow<DecodedWeather>(extraBufferCapacity = 1)
    val appliedWeather: SharedFlow<DecodedWeather> = _appliedWeather.asSharedFlow()

    init {
        selectAircraft("A320-214")
    }

    fun selectAircraft(type: String) {
        val aircraft = factory.create(getApplication(), type)
        _selectedAircraft.value = aircraft
    }

    fun calculateTakeoff(
        flaps: String,
        gw: String,
        cg: String,
        qnh: String,
        temp: String,
        elevation: String,
        antiIce: String,
        airCond: String,
        runwayCond: String
    ): ValidationResult {
        val aircraft = _selectedAircraft.value ?: return ValidationResult.Error("No aircraft selected")
        val validation = validator.validateInput(
            aircraft = aircraft,
            flaps = flaps,
            gw = gw,
            cg = cg,
            qnh = qnh,
            temperature = temp,
            elevation = elevation
        )

        if (validation is ValidationResult.Success) {
            val input = TakeoffInput(
                aircraft = aircraft,
                flaps = flaps.trim().uppercase(),
                grossWeightKg = gw.toInt(),
                centerOfGravity = cg.toDouble(),
                qnhHpa = qnh.toInt(),
                temperatureCelsius = temp.toInt(),
                antiIce = antiIce.trim().uppercase(),
                airConditioning = airCond.trim().uppercase(),
                runwayCondition = runwayCond.trim().uppercase(),
                runwayElevationFt = elevation.toInt()
            )
            val result = calculation.calculate(input)
            _calculationResult.value = result
        }

        return validation
    }

    fun fetchWeather(icao: String) {
        if (icao.isBlank()) return
        viewModelScope.launch {
            _isWeatherLoading.value = true
            _weatherError.value = null
            val result = metarService.fetchWeather(icao)
            result.onSuccess { decoded ->
                _weatherState.value = decoded
            }.onFailure { err ->
                _weatherError.value = err.message ?: "Failed to fetch METAR data"
            }
            _isWeatherLoading.value = false
        }
    }

    fun applyWeatherToCalculator(weather: DecodedWeather) {
        _appliedWeather.tryEmit(weather)
    }
}
