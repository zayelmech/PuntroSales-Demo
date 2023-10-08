package com.oxxogas.gaxposx.domain.login.architecture

interface BackgroundInterface<INPUT,OUTPUT> {
    suspend fun execute(input: INPUT): Result<OUTPUT>
}