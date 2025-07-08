package com.robertknezevic.travelbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robertknezevic.travelbuddy.data.api.RetrofitService
import com.robertknezevic.travelbuddy.data.model.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class CityViewModel : ViewModel() {
    private val retrofitService: RetrofitService = RetrofitService.create()

    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val searchQuery = MutableStateFlow("")

    private suspend fun fetchCities(namePrefix: String) {
        try {
            val response = retrofitService.getCities(namePrefix)
            val filteredCities = response.data
                .filter { it.population > 0 }
                .sortedByDescending { it.population }
                .take(10)
            _cities.value = filteredCities
            _errorMessage.value = if (filteredCities.isEmpty()) {
                "No cities found with the name $namePrefix"
            } else {
                null
            }
        } catch (e: Exception) {
            _errorMessage.value = "Error fetching cities: ${e.message}"
        }
    }

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(600)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchCities(query)
                }
        }
    }
}