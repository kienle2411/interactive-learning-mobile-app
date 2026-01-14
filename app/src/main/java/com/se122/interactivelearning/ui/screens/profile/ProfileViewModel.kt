package com.se122.interactivelearning.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.ProfileResponse
import com.se122.interactivelearning.domain.usecase.profile.GetProfileUseCase
import com.se122.interactivelearning.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val tokenManager: TokenManager
): ViewModel() {
    private val _profile = MutableStateFlow<ViewState<ProfileResponse>>(ViewState.Idle)
    val profile = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            loadProfile()
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profile.value = ViewState.Loading
            when (val result = getProfileUseCase.invoke()) {
                is ApiResult.Success -> {
                    _profile.value = ViewState.Success(result.data)
                }
                is ApiResult.Error -> {
                    Log.i("ProfileViewModel", result.toString())
                    val msg = (result.message + " " + if (result.errors != null && result.errors.isNotEmpty()) result.errors.first().toString() else "")
                    _profile.value = ViewState.Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _profile.value = ViewState.Error(msg)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
            loadProfile()
        }
    }
}