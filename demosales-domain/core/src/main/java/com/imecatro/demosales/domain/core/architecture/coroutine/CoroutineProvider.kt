package com.imecatro.demosales.domain.core.architecture.coroutine

import kotlin.coroutines.CoroutineContext

/**
 * Since we work with coroutine specific use cases
 *
 */
interface CoroutineProvider {
    val io: CoroutineContext
    val main: CoroutineContext
}