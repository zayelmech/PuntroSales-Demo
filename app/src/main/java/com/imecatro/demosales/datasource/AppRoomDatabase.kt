package com.imecatro.demosales.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.imecatro.demosales.data.clients.datasource.ClientsDao
import com.imecatro.demosales.data.clients.model.ClientRoomEntity
import com.imecatro.demosales.data.sales.datasource.OrdersRoomDao
import com.imecatro.demosales.data.sales.datasource.SalesRoomDao
import com.imecatro.demosales.data.sales.model.OrderDataRoomModel
import com.imecatro.demosales.data.sales.model.SaleDataRoomModel
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.model.ProductRoomEntity
import com.imecatro.products.data.model.StockRoomEntity

@Database(
    entities = [ProductRoomEntity::class, SaleDataRoomModel::class, OrderDataRoomModel::class, ClientRoomEntity::class, StockRoomEntity::class],
    version = 8
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun productsRoomDao(): ProductsDao
    abstract fun salesRoomDao(): SalesRoomDao

    abstract fun ordersRoomDao(): OrdersRoomDao

    abstract fun clientsRoomDao(): ClientsDao

    companion object {

        private var productsDao: ProductsDao? = null
        private var salesDao: SalesRoomDao? = null
        private var ordersDao: OrdersRoomDao? = null
        private var clientsDao: ClientsDao? = null

        fun initDatabase(context: Context): AppRoomDatabase {
            val db = Room.databaseBuilder(
                context,
                AppRoomDatabase::class.java,
                "puntrosales_demo_database"
            )
                .addMigrations( MIGRATION_6_7, MIGRATION_7_8)
                .build()

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
//
//val MIGRATION_5_6 = object : Migration(5, 6) {
//    override fun migrate(db: SupportSQLiteDatabase) {
//        // Round to, say, 6 decimals (adjust to your domain needs)
//        db.execSQL("""
//            UPDATE stock_table
//            SET amount = ROUND(amount, 6)
//        """.trimIndent())
//
//        db.execSQL("""
//            UPDATE order_table
//            SET qty = ROUND(qty, 6)
//        """.trimIndent())
//    }
//}

val MIGRATION_6_7 = object : Migration(6,7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Round to, say, 6 decimals (adjust to your domain needs)
        db.execSQL("""
            UPDATE products_table
            SET price = ROUND(price, 6)
        """.trimIndent())

        db.execSQL("""
            UPDATE order_table
            SET productPrice = ROUND(productPrice, 6)
        """.trimIndent())
    }
}
val MIGRATION_7_8 = object : Migration(7,8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE sales_table ADD COLUMN extra REAL NOT NULL DEFAULT 0.0")
    }
}

val MIGRATION_8_9 = object : Migration(8,9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Round to, say, 6 decimals (adjust to your domain needs)
        db.execSQL("ALTER TABLE sales_table ADD COLUMN name REAL NOT NULL DEFAULT 0.0")

    }
}
