package com.imecatro.demosales.domain.core.architecture.usecase

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import kotlinx.coroutines.withContext

abstract class BackgroundUseCase<INPUT, OUTPUT>(private val coroutineContext: CoroutineProvider) {

    public suspend fun execute(input: INPUT): Result<OUTPUT> {
        return try {
            withContext(coroutineContext.io) {
                val result = doInBackground(input)
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    abstract suspend fun doInBackground(input: INPUT): OUTPUT

}