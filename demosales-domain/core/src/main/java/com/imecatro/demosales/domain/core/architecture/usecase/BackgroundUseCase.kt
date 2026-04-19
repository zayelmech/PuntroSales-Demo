package com.imecatro.demosales.domain.core.architecture.usecase

import com.imecatro.demosales.domain.core.architecture.coroutine.CoroutineProvider
import com.imecatro.demosales.domain.core.architecture.exception.DomainException
import com.imecatro.demosales.domain.core.architecture.exception.UnknownDomainException
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

/**
 * Performs the given [action] on the encapsulated value if this instance represents [any] whether is success or failure.
 * Use it carefully, for stopping loading states
 *
 * Returns the original `Result` unchanged.
 */
@OptIn(ExperimentalContracts::class)
@SinceKotlin("1.3")
public inline fun <T> Result<T>.onAny(action: () -> Unit): Result<T> {
    contract {
        callsInPlace(action, InvocationKind.AT_MOST_ONCE)
    }
    if (isSuccess || isFailure) action()
    return this
}