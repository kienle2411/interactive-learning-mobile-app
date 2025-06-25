package com.se122.interactivelearning.ui.screens.auth.recovery_password

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.RecoveryRequest
import com.se122.interactivelearning.data.remote.dto.RecoveryResponse
import com.se122.interactivelearning.domain.usecase.auth.SendRecoveryEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoveryPasswordViewModel @Inject constructor(
    private val sendRecoveryEmailUseCase: SendRecoveryEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ApiResult<RecoveryResponse>?>(null)
    val uiState: StateFlow<ApiResult<RecoveryResponse>?> = _uiState

    fun sendRecoveryEmail(email: String) {
        viewModelScope.launch {
            val result = sendRecoveryEmailUseCase(RecoveryRequest(email))
            _uiState.value = result
        }
    }
}