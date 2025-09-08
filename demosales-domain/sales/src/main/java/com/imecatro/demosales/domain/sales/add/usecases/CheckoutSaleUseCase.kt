package com.imecatro.demosales.domain.sales.add.usecases

import com.imecatro.demosales.domain.sales.add.repository.AddSaleRepository
import com.imecatro.demosales.domain.sales.model.OrderStatus
import com.imecatro.demosales.domain.sales.model.SaleDomainModel
import kotlinx.coroutines.flow.first

class CheckoutSaleUseCase(
    private val addSaleRepository: AddSaleRepository

) {

    suspend operator fun invoke(id: Long, sale: CheckoutDomainModel.() -> Unit) {
        val currentSal = addSaleRepository.getCartFlow(id).first()
        val c = CheckoutDomainModel().apply(sale)
        addSaleRepository.saveSale(
            currentSal.copy(
                status = if (c.tickedPaid) OrderStatus.COMPLETED else OrderStatus.PENDING,
                clientName = c.client.name?:"",
                clientAddress = c.client.address?:"",
                clientId = c.client.id?:0,
                note = c.note,
                date = c.date,
                totals = c.totals
            )
        )
    }
}

data class CheckoutDomainModel(
    var note: String = "",
    var date: String = "",
    var totals: SaleDomainModel.Costs = SaleDomainModel.Costs(),

    var client : SaleDomainModel.Client = SaleDomainModel.Client(),
    var tickedPaid: Boolean = false
)
