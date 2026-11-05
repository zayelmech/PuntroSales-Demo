package com.imecatro.demosales.domain.products.repository

import androidx.paging.PagingData
import com.imecatro.demosales.domain.products.model.ProductListItem
import com.imecatro.demosales.domain.products.model.ProductsQuery
import kotlinx.coroutines.flow.Flow

/** Separate read contract so existing full-catalog consumers keep their behavior. */
interface ProductsPagingRepository {
    fun getProductsPaged(query: ProductsQuery): Flow<PagingData<ProductListItem>>
    fun observeCount(query: ProductsQuery): Flow<Int>
    suspend fun getMatchingIds(query: ProductsQuery): List<Long>
}
