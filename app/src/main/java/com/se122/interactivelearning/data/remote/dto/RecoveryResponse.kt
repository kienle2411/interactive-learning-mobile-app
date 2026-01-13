package com.se122.interactivelearning.data.remote.dto

data class RecoveryResponse(
    val status: String,
    val statusCode: Int,
    val data: RecoveryData
)

data class RecoveryData(
    val status: Int,
    val id: String,
    val message: String
)
