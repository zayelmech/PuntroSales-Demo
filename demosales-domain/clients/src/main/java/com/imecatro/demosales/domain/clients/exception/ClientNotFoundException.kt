package com.imecatro.demosales.domain.clients.exception


private const val CLIENT_NOT_FOUND_EXCEPTION = "Client not found by id"

data class ClientNotFoundException(val msj: String = CLIENT_NOT_FOUND_EXCEPTION) : Exception(msj)
