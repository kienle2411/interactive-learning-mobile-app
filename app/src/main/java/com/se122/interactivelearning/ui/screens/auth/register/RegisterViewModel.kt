package com.se122.interactivelearning.ui.screens.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.RegisterRequest
import com.se122.interactivelearning.data.remote.dto.RegisterResponse
import com.se122.interactivelearning.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<ViewState<RegisterResponse?>>(ViewState.Idle)
    val registerState: StateFlow<ViewState<RegisterResponse?>> = _registerState.asStateFlow()

    fun register(
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        email: String,
        phone: String,
        gender: String
    ) {
        viewModelScope.launch {
            _registerState.value = ViewState.Loading

            val request = RegisterRequest(
                username = username,
                password = password,
                firstName = firstName,
                lastName = lastName,
                dateOfBirth = dateOfBirth,
                email = email,
                phone = phone,
                gender = gender
            )

            Log.d("RegisterViewModel", "Register request: $request")

            when (val result = registerUseCase(request)) {
                is ApiResult.Success -> {
                    _registerState.value = ViewState.Success(result.data)
                }

                is ApiResult.Error -> {
                    _registerState.value = ViewState.Error(result.message)
                }

                is ApiResult.Exception -> {
                    _registerState.value = ViewState.Error(result.e.message ?: "Unexpected error")
                }
            }
        }
    }
}