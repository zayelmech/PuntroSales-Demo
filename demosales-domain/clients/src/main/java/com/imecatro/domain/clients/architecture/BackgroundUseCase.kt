package com.imecatro.domain.clients.architecture

import com.imecatro.domain.clients.exceptions.ClientsDomainException
import com.oxxogas.gaxposx.domain.login.architecture.BackgroundInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.lang.Exception


abstract class BackgroundUseCase<INPUT, OUTPUT>(
    private val coroutineDispatcher: CoroutineDispatcher
) : BackgroundInterface<INPUT, OUTPUT> {

    override suspend fun execute(input: INPUT): Result<OUTPUT> {
        return try {
            val result = withContext(coroutineDispatcher) {
                executeInBackground(input)
            }
            Result.success(result)
        } catch (error: ClientsDomainException) {
            Result.failure(error)
        }
        catch (error: Exception) {
            Result.failure(error)
        } catch (error: Throwable) {
            Result.failure(error)
        }
    }

    abstract suspend fun executeInBackground(input: INPUT): OUTPUT

}