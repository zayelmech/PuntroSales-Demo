package com.imecatro.demosales.domain.core.architecture.usecase

interface UseCase<in INPUT, out OUTPUT>{

    suspend fun execute(input: INPUT) : Result<OUTPUT>

}