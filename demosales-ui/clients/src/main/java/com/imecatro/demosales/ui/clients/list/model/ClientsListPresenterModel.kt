package com.imecatro.demosales.ui.clients.list.model

/**
 * Model for clients list presenter
 */
data class ClientsListPresenterModel(
    val clients: List<ClientUiModel> = listOf(),
    val isFetchingClients: Boolean = false,
    val clientsFiltered: List<ClientUiModel> = listOf(),
    val idsSelected: List<Long> = listOf(),
    val allSelected: Boolean = false,
    val enableSelection: Boolean = false,
    val errors: String? = null
)
