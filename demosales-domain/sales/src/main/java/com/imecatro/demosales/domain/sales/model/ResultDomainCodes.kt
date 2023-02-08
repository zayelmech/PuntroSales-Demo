package com.imecatro.demosales.domain.sales.model

enum class ResultDomainCodes(val code: Int) {
    SUCCESS(200),
    ERROR(400),
    UNEXPECTED(300)
}