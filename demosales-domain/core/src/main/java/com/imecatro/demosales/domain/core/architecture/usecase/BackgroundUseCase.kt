package com.imecatro.demosales.domain.core.architecture.usecase

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.exception.DomainException
import com.imecatro.demosales.domain.core.architecture.exception.UnknownDomainException
import kotlinx.coroutines.withContext

abstract class BackgroundUseCase<INPUT,OUTPUT>(private val coroutineContext: CoroutineProvider) : UseCase<INPUT,OUTPUT> {

    public override suspend fun execute(input: INPUT): Result<OUTPUT> {
        return try {
            withContext(coroutineContext.io) {
                val result = doInBackground(input)
                Result.success(result)
            }
        } catch (e: DomainException) {
            Result.failure(e)
        } catch (t : Throwable){
          Result.failure(UnknownDomainException(t.message?:"Unknown error"))
        }
    }
    abstract suspend fun doInBackground(input: INPUT): OUTPUT
}