package com.se122.interactivelearning.ui.screens.meeting

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MeetingSharedViewModel @Inject constructor(): ViewModel() {
    private val _cameraState = MutableStateFlow(true)
    val cameraState = _cameraState

    private val _microState = MutableStateFlow(true)
    val microState = _microState

    fun toogleCamera() {
        _cameraState.value = !_cameraState.value
    }

    fun toogleMic() {
        _microState.value = !_microState.value

    }
}