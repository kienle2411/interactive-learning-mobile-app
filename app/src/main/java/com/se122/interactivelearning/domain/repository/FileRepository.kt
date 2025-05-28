package com.se122.interactivelearning.domain.repository

import com.se122.interactivelearning.data.remote.api.ApiResult

interface FileRepository {
    suspend fun download(id: String): ApiResult<String>
}