package com.se122.interactivelearning.ui.screens.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se122.interactivelearning.common.ViewState
import com.se122.interactivelearning.common.ViewState.*
import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.dto.LoginResponse
import com.se122.interactivelearning.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private val _loginState = MutableStateFlow<ViewState<LoginResponse>>(ViewState.Idle)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = ViewState.Loading
            when (val result = loginUseCase(username, password)) {
                is ApiResult.Success -> {
                    _loginState.value = Success(result.data)
                }
                is ApiResult.Error -> {
                    Log.i("LoginViewModel", "Error: $result")
                    val msg = (result.message + "\n" + result.errors?.first()) ?: "Unknown error"
                    _loginState.value = Error(msg)
                }
                is ApiResult.Exception -> {
                    val msg = "Unknown error"
                    _loginState.value = Error(msg)
                }
            }
        }
    }
}