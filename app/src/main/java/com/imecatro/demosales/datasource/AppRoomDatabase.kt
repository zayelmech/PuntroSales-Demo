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
import com.imecatro.products.data.datasource.CategoriesDao
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.model.CategoryRoomEntity
import com.imecatro.products.data.model.ProductRoomEntity
import com.imecatro.products.data.model.StockRoomEntity

@Database(
    entities = [ProductRoomEntity::class,
        SaleDataRoomModel::class,
        OrderDataRoomModel::class,
        ClientRoomEntity::class,
        StockRoomEntity::class,
        CategoryRoomEntity::class],
    version = 10
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun productsRoomDao(): ProductsDao

    abstract fun categoriesRoomDao(): CategoriesDao
    abstract fun salesRoomDao(): SalesRoomDao

    abstract fun ordersRoomDao(): OrdersRoomDao

    abstract fun clientsRoomDao(): ClientsDao

    companion object {

        private var productsDao: ProductsDao? = null
        private var salesDao: SalesRoomDao? = null
        private var ordersDao: OrdersRoomDao? = null
        private var clientsDao: ClientsDao? = null

        private var categoriesDao: CategoriesDao? = null

        fun initDatabase(context: Context): AppRoomDatabase {
            val db = Room.databaseBuilder(
                context,
                AppRoomDatabase::class.java,
                "puntrosales_demo_database"
            )
                .addMigrations(MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                .build()

            productsDao = db.productsRoomDao()
            salesDao = db.salesRoomDao()
            ordersDao = db.ordersRoomDao()
            clientsDao = db.clientsRoomDao()
            categoriesDao = db.categoriesRoomDao()

            return db
        }

        fun isActive(): Boolean {
            return productsDao?.let { true } ?: false
        }

    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Round to, say, 6 decimals (adjust to your domain needs)
        db.execSQL(
            """
            UPDATE products_table
            SET price = ROUND(price, 6)
        """.trimIndent()
        )

        db.execSQL(
            """
            UPDATE order_table
            SET productPrice = ROUND(productPrice, 6)
        """.trimIndent()
        )
    }
}
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE sales_table ADD COLUMN extra REAL NOT NULL DEFAULT 0.0")
    }
}


val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Evitar conflictos por FKs durante recreate
        db.execSQL("PRAGMA foreign_keys=OFF")

        // Renombrar tabla vieja
        db.execSQL("ALTER TABLE `sales_table` RENAME TO `sales_table_old`")

        // Esquema nuevo: totals_* son NULLABLE y SIN DEFAULT (porque totals es @Embedded nullable)
        db.execSQL("""
      CREATE TABLE IF NOT EXISTS `sales_table` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        `clientId` INTEGER,
        `creationDateMillis` INTEGER NOT NULL,
        `status` TEXT NOT NULL,
        `note` TEXT NOT NULL,
        `totals_subtotal` REAL,
        `totals_discount` REAL,
        `totals_extra` REAL,
        `totals_total` REAL,
        `client_name_at_sale` TEXT,
        `client_address_at_sale` TEXT
      )
    """.trimIndent())

        // Detectar si la tabla vieja tenía columna 'extra'
        val hasExtra = db.query("PRAGMA table_info(`sales_table_old`)").use { c ->
            var found = false
            val nameIdx = c.getColumnIndex("name")
            while (c.moveToNext()) {
                if (nameIdx >= 0 && c.getString(nameIdx) == "extra") { found = true; break }
            }
            found
        }
        val extraExpr = if (hasExtra) "COALESCE(s.extra, 0.0)" else "0.0"

        // Copiar datos calculando totales
        db.execSQL("""
      INSERT INTO `sales_table` (
        id, clientId, creationDateMillis, status, note,
        totals_subtotal, totals_discount, totals_extra, totals_total,
        client_name_at_sale, client_address_at_sale
      )
      SELECT
        s.id,
        s.clientId,
        s.creationDateMillis,
        s.status,
        s.note,
        /* subtotal */ COALESCE((SELECT SUM(o.qty * o.productPrice)
                                  FROM order_table o
                                  WHERE o.sale_id = s.id), 0.0),
        /* discount */ 0.0,
        /* extra    */ $extraExpr,
        /* total    */ COALESCE((SELECT SUM(o.qty * o.productPrice)
                                  FROM order_table o
                                  WHERE o.sale_id = s.id), 0.0) + $extraExpr,
        /* snapshot antiguos: NULL */
        NULL,
        NULL
      FROM `sales_table_old` s
    """.trimIndent())

        // Limpiar vieja y recrear índice
        db.execSQL("DROP TABLE `sales_table_old`")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_sales_table_clientId` ON `sales_table` (`clientId`)")

        db.execSQL("PRAGMA foreign_keys=ON")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1) Crear categories_table si no existe (shape exacto)
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS categories_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
            """.trimIndent()
        )

        // 🔴 FALTABA: crear el índice que Room espera (no unique)
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_categories_table_name 
            ON categories_table(name)
            """.trimIndent()
        )

        // 2) Crear nueva tabla products con category_id + FK
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS products_table_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                currency TEXT NOT NULL,
                unit TEXT NOT NULL,
                stock REAL NOT NULL,
                details TEXT NOT NULL,
                imageUri TEXT NOT NULL,
                category_id INTEGER,
                FOREIGN KEY(category_id) REFERENCES categories_table(id) 
                    ON UPDATE NO ACTION 
                    ON DELETE SET NULL
            )
            """.trimIndent()
        )

        // 3) Copiar datos desde la products_table vieja (category_id = NULL)
        db.execSQL(
            """
            INSERT INTO products_table_new
            (id, name, price, currency, unit, stock, details, imageUri, category_id)
            SELECT id, name, price, currency, unit, stock, details, imageUri, NULL
            FROM products_table
            """.trimIndent()
        )

        // 4) Reemplazar tabla
        db.execSQL("DROP TABLE products_table")
        db.execSQL("ALTER TABLE products_table_new RENAME TO products_table")

        // 5) Índice esperado por Room en products_table.category_id
        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_products_table_category_id 
            ON products_table(category_id)
            """.trimIndent()
        )
    }
}