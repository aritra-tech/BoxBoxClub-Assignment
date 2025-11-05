package com.aritradas.boxboxclubassignment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.domain.usecase.GetRaceDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class for Detail UI state
 */
sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val race: Race) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

/**
 * ViewModel for Race Detail screen
 * Uses Use Case for business logic
 */
class DetailViewModel(
    private val getRaceDetailsUseCase: GetRaceDetailsUseCase,
    private val raceId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    
    init {
        loadRaceDetails(raceId)
    }
    
    /**
     * Loads race details by ID
     */
    private fun loadRaceDetails(raceId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            
            getRaceDetailsUseCase(raceId).fold(
                onSuccess = { race ->
                    if (race != null) {
                        _uiState.value = DetailUiState.Success(race)
                    } else {
                        _uiState.value = DetailUiState.Error("Race not found")
                    }
                },
                onError = { exception, message ->
                    _uiState.value = DetailUiState.Error(
                        message ?: exception.message ?: "Failed to load race details"
                    )
                },
                onLoading = {
                    _uiState.value = DetailUiState.Loading
                }
            )
        }
    }
    
    /**
     * Retry loading race details
     */
    fun retry() {
        loadRaceDetails(raceId)
    }
}

