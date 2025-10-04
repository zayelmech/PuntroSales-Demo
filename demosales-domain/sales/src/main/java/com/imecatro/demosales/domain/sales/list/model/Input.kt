package com.imecatro.demosales.domain.sales.list.model


typealias Ids = Input.() -> Unit

data class Input(
    var ids: List<Long> = emptyList()
)