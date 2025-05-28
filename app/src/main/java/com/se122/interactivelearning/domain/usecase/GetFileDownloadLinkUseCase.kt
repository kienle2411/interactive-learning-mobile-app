package com.se122.interactivelearning.domain.usecase

import com.se122.interactivelearning.data.remote.api.ApiResult
import com.se122.interactivelearning.domain.repository.FileRepository
import javax.inject.Inject

class GetFileDownloadLinkUseCase @Inject constructor(
    private val repository: FileRepository
) {
    suspend operator fun invoke(id: String): ApiResult<String> {
        return repository.download(id)
    }
}