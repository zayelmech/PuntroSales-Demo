package com.imecatro.demosales.domain.core.architecture.usecase

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

abstract class BackgroundUseCase<INPUT, OUTPUT>(coroutineContext: CoroutineDispatcher) {

    public suspend fun execute(input: INPUT): Result<OUTPUT> {
        return try {
            withContext(coroutineContext) {
                val result = doInBackground(input)
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    abstract suspend fun doInBackground(input: INPUT): OUTPUT

}