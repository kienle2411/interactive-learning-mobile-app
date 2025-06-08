package com.se122.interactivelearning.common.events

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutEventBus @Inject constructor() {
    private val _logoutFlow = MutableSharedFlow<Unit>(replay = 0)
    val logoutFlow: SharedFlow<Unit> = _logoutFlow

    suspend fun sendLogoutEvent() {
        _logoutFlow.emit(Unit)
    }
}