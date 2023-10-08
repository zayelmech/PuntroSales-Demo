package com.imecatro.domain.clients.exceptions

import com.imecatro.domain.clients.architecture.DomainException

sealed class ClientsDomainException : DomainException() {
    object ClientFieldIsEmpty : ClientsDomainException()
    object EmailClientFieldIsEmpty : ClientsDomainException()
    object PhoneClientFieldIsEmpty : ClientsDomainException()
    object IdError : ClientsDomainException()
}
