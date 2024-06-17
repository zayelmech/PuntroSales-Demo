package com.imecatro.demosales.datasource.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.clients.model.ClientRoomEntity
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.model.OrderDataRoomModel
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.model.ProductRoomEntity

@Database(
    entities = [ProductRoomEntity::class, SaleDataRoomModel::class, OrderDataRoomModel::class, ClientRoomEntity::class],
    version = 4
)
abstract class ProductsRoomDatabase : RoomDatabase() {
    abstract fun productsRoomDao(): ProductsDao
    abstract fun salesRoomDao(): SalesRoomDao

    abstract fun ordersRoomDao(): OrdersRoomDao

    abstract fun clientsRoomDao(): ClientsDao

    companion object {

        private var productsDao: ProductsDao? = null
        private var salesDao: SalesRoomDao? = null
        private var ordersDao: OrdersRoomDao? = null
        private var clientsDao: ClientsDao? = null

        fun initDatabase(context: Context): ProductsRoomDatabase {
            val db = Room.databaseBuilder(
                context,
                ProductsRoomDatabase::class.java,
                "puntrosales_demo_database"
            ).build()

            productsDao = db.productsRoomDao()
            salesDao = db.salesRoomDao()
            ordersDao = db.ordersRoomDao()
            clientsDao = db.clientsRoomDao()

            return db
        }

        fun isActive(): Boolean {
            return productsDao?.let { true } ?: false
        }

    }
}