package com.imecatro.demosales.navigation.clients

sealed class ClientsDestinations(val route: String) {
    object List : ClientsDestinations("GetListClientsRoute")
    object Add : ClientsDestinations("AddClientsRoute")
    object Edit : ClientsDestinations("EditClientsRoute")
    object Details : ClientsDestinations("DetailsClientsRoute")
}

