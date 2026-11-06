package com.imecatro.demosales.products

import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.imecatro.demosales.datasource.AppRoomDatabase
import com.imecatro.products.data.repository.ProductsRepositoryImpl
import com.imecatro.products.data.paging.productSearchKey
import com.imecatro.products.ui.list.mappers.toProductUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

/**
 * Repeatable data-to-UI-model baseline, not an application startup/frame benchmark.
 * All synthetic records live in this test APK and a disposable, isolated database.
 */
@RunWith(AndroidJUnit4::class)
class ProductsLoadingBaselineTest {
    @Test
    fun recordLegacyLoadingBaseline() = runBlocking(Dispatchers.IO) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (size in listOf(1_000, 10_000, 50_000)) {
            val name = "products-performance-$size.db"
            context.deleteDatabase(name)
            val database = Room.databaseBuilder(context, AppRoomDatabase::class.java, name).build()
            try {
                seedProducts(database, size)
                val repository = ProductsRepositoryImpl(database.productsRoomDao(), database.categoriesRoomDao())
                suspend fun load() = repository.getAllProducts().first()
                    .sortedBy { it.id }
                    .toProductUiModel()
                    .map { it.copy(isSelected = false) }
                repeat(2) { assertEquals(size, load().size) }
                val samples = (1..9).map {
                    val start = SystemClock.elapsedRealtimeNanos()
                    assertEquals(size, load().size)
                    (SystemClock.elapsedRealtimeNanos() - start) / 1_000_000.0
                }
                reportMetric("legacy_initial_models", size, size, samples)
            } finally {
                database.close()
                context.deleteDatabase(name)
            }
        }
    }
}

internal fun seedProducts(database: AppRoomDatabase, size: Int) {
    val db = database.openHelper.writableDatabase
    db.beginTransaction()
    try {
        db.execSQL("INSERT INTO categories_table(id,name) VALUES(1,'Food'),(2,'Other')")
        val statement = db.compileStatement("""
            INSERT INTO products_table(id,name,price,currency,unit,stock,details,imageUri,category_id,barcode,nameKey,searchName)
            VALUES(?, ?, ?, 'MXN', 'pz', ?, ?, '', ?, ?, ?, ?)
        """.trimIndent())
        try {
            for (id in 1..size) {
                statement.bindLong(1, id.toLong())
                val name = "Product %06d".format(Locale.ROOT, size - id)
                statement.bindString(2, name)
                statement.bindDouble(3, (id % 113) / 10.0)
                statement.bindDouble(4, (id % 71 - 5).toDouble())
                statement.bindString(5, "Fixture description ".repeat(8))
                if (id % 3 == 0) statement.bindNull(6) else statement.bindLong(6, (id % 2 + 1).toLong())
                statement.bindString(7, "TEST$id")
                statement.bindString(8, name.lowercase())
                statement.bindString(9, productSearchKey(name))
                statement.executeInsert()
            }
        } finally {
            statement.close()
        }
        db.setTransactionSuccessful()
    } finally {
        db.endTransaction()
    }
}

internal fun reportMetric(metric: String, size: Int, loaded: Int, samples: List<Double>) {
    val sorted = samples.sorted()
    val json = """{"metric":"$metric","products":$size,"loadedModels":$loaded,"samplesMs":$samples,"medianMs":${sorted[sorted.size / 2]},"p95Ms":${sorted.last()},"api":${Build.VERSION.SDK_INT},"device":"${Build.MODEL}","build":"huaweiDebug","images":false}"""
    Log.i("ProductsPerformance", json)
    InstrumentationRegistry.getInstrumentation().sendStatus(0, android.os.Bundle().apply {
        putString("stream", "\nPRODUCTS_METRIC $json\n")
    })
}
