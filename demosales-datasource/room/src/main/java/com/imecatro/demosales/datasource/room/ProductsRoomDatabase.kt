package com.imecatro.demosales.datasource.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.imecatro.demosales.datasource.room.entities.ProductRoomEntity

@Database(entities = [ProductRoomEntity::class], version = 1)
abstract class ProductsRoomDatabase : RoomDatabase() {
    abstract fun productsRoomDao(): ProductsDao


    companion object {

        var globalDao: ProductsDao? = null

        fun initDatabase(context: Context): ProductsRoomDatabase {
            val db = Room.databaseBuilder(
                context,
                ProductsRoomDatabase::class.java,
                "puntrosales_demo_database"
            ).build()

            globalDao = db.productsRoomDao()

            return db
        }

        fun isActive(): Boolean {
            return globalDao?.let { true } ?: false
        }

    }
}