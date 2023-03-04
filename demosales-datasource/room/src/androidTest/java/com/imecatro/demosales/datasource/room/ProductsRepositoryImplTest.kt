package com.imecatro.demosales.datasource.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imecatro.products.data.datasource.ProductsDao
import com.imecatro.products.data.model.ProductRoomEntity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProductsRepositoryImplTest {

    private lateinit var productsDao: ProductsDao
    private lateinit var db: ProductsRoomDatabase

    @Before
    fun createDb() {

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ProductsRoomDatabase::class.java).build()
        productsDao = db.productsRoomDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
     fun addProduct()  {
    val product = ProductRoomEntity(
        name = "Onion",
         price = 1f,
         currency=  "USD",
         unit = "pz" ,
         details= "details here",
        imageUri=  "someurihere")

        productsDao.addProduct(product)
        val byName = productsDao.getProductDetailsById(1)
        assertEquals(byName.name,"Onion")
        val set = setOf("a","b","c")
        set.minus("a")
    }

//    @Test
//    fun getAllProducts() {
//    }

//    @Test
//    fun deleteProductById() {
//    }
//
//    @Test
//    fun updateProduct() {
//    }
//
//    @Test
//    fun getProductDetailsById() {
//    }
}