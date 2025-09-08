package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.SaleDomainModel

class UpdateSaleClientUseCase(
    private val repository: AddSaleRepository
) {

    suspend operator fun invoke(saleId: Long, params: Client.() -> Unit) {
        val client = Client().apply(params)
        val saleClient =
            SaleDomainModel.Client(id = client.id, name = client.name, address = client.address)

        println("Client: on sale $saleId c -> domain"+ saleClient )
        repository.updateClientOnSale(saleId = saleId, sale = saleClient)

    }

    data class Client(
        var id: Long = 0,
        var name: String = "-",
        var address: String = "-",
    )
}