package com.aritradas.boxboxclubassignment.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aritradas.boxboxclubassignment.data.model.Driver
import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.data.model.Session
import com.aritradas.boxboxclubassignment.domain.usecase.GetDriversUseCase
import com.aritradas.boxboxclubassignment.domain.usecase.GetUpcomingRaceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Sealed class for UI state - better than data class with nullable error
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(
        val drivers: List<Driver>,
        val currentDriverIndex: Int = 0,
        val upcomingRace: Race? = null,
        val nextSession: Session? = null
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

/**
 * ViewModel for Home screen
 * Uses Use Cases for business logic (clean architecture)
 */
class HomeViewModel(
    private val getDriversUseCase: GetDriversUseCase,
    private val getUpcomingRaceUseCase: GetUpcomingRaceUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * Loads all data for the home screen
     */
    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            // Load drivers
            loadDrivers()
            
            // Load upcoming race
            loadUpcomingRace()
        }
    }
    
    /**
     * Loads drivers using use case
     */
    private suspend fun loadDrivers() {
        getDriversUseCase.getDriversByPosition(1).fold(
            onSuccess = { drivers ->
                // If no position 1 drivers, show top 3 drivers as fallback
                val driversToShow = drivers.ifEmpty {
                    getDriversUseCase().fold(
                        onSuccess = { allDrivers ->
                            allDrivers.sortedBy { it.position }.take(3)
                        },
                        onError = { _, _ -> emptyList() },
                        onLoading = { emptyList() }
                    )
                }
                
                _uiState.update { currentState ->
                    when (currentState) {
                        is HomeUiState.Success -> {
                            currentState.copy(drivers = driversToShow)
                        }
                        else -> {
                            HomeUiState.Success(
                                drivers = driversToShow,
                                currentDriverIndex = 0
                            )
                        }
                    }
                }
            },
            onError = { exception, message ->
                _uiState.value = HomeUiState.Error(
                    message ?: exception.message ?: "Failed to load drivers"
                )
            },
            onLoading = {
                // Keep loading state
            }
        )
    }
    
    /**
     * Loads upcoming race and next session
     */
    private suspend fun loadUpcomingRace() {
        getUpcomingRaceUseCase().fold(
            onSuccess = { race ->
                _uiState.update { currentState ->
                    when (currentState) {
                        is HomeUiState.Success -> {
                            currentState.copy(upcomingRace = race)
                        }
                        else -> {
                            HomeUiState.Success(
                                drivers = emptyList(),
                                upcomingRace = race
                            )
                        }
                    }
                }
                
                // Load next session if race exists
                race?.let { 
                    loadNextSession(it)
                }
            },
            onError = { exception, message ->
                // Don't update error if we already have drivers loaded
                _uiState.update { currentState ->
                    // Keep success state
                    currentState as? HomeUiState.Success
                        ?: HomeUiState.Error(
                            message ?: exception.message ?: "Failed to load race schedule"
                        )
                }
            },
            onLoading = {
                // Keep current state
            }
        )
    }
    
    /**
     * Loads the next upcoming session for a race
     */
    private suspend fun loadNextSession(race: Race) {
        getUpcomingRaceUseCase.getNextSession(race).fold(
            onSuccess = { session ->
                _uiState.update { currentState ->
                    if (currentState is HomeUiState.Success) {
                        currentState.copy(nextSession = session)
                    } else {
                        currentState
                    }
                }
            },
            onError = { _, _ ->
                // Silently fail - session is optional
            },
            onLoading = {
                // Keep current state
            }
        )
    }
    
    /**
     * Updates the current driver index for slider
     */
    fun updateCurrentDriverIndex(index: Int) {
        _uiState.update { currentState ->
            if (currentState is HomeUiState.Success) {
                currentState.copy(currentDriverIndex = index)
            } else {
                currentState
            }
        }
    }
    
    /**
     * Gets the current driver
     */
    fun getCurrentDriver(): Driver? {
        return when (val state = _uiState.value) {
            is HomeUiState.Success -> {
                state.drivers.getOrNull(state.currentDriverIndex)
            }
            else -> null
        }
    }
    
    /**
     * Retry loading data
     */
    fun retry() {
        loadData()
    }
}

