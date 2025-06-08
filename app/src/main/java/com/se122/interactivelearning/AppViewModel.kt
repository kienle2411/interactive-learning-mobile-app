package com.se122.interactivelearning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.events.LogoutEventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val logoutEventBus: LogoutEventBus
): ViewModel() {
    private val _logoutTrigger = MutableStateFlow(false)
    val logoutTrigger: StateFlow<Boolean> = _logoutTrigger

    init {
        viewModelScope.launch {
            logoutEventBus.logoutFlow.collect {
                _logoutTrigger.value = true
            }
        }
    }

    fun resetLogoutState() {
        viewModelScope.launch {
            _logoutTrigger.value = false
        }
    }
}