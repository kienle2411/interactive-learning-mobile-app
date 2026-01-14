package com.se122.interactivelearning.data.repository

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.data.remote.api.FileApi
import com.se122.interactivelearning.data.remote.api.safeApiCall
import com.se122.interactivelearning.domain.repository.FileRepository
import javax.inject.Inject

class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi
): FileRepository {
    override suspend fun download(id: String): ApiResult<String> {
        return safeApiCall {
            fileApi.download(id)
        }
    }
}