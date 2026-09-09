package com.imecatro.demosales.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.imecatro.demosales.domain.products.repository.CatalogRepository
import com.imecatro.demosales.domain.products.usecases.WebCatalogDomainModel
import com.imecatro.demosales.firebase.dtos.WebCatalogDto
import com.imecatro.demosales.firebase.mapper.toData
import com.imecatro.demosales.firebase.mapper.toDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.encode
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject

private const val TAG = "CatalogRepositoryImpl"

/**
 * Implementation of [CatalogRepository] that manages catalog data using Firebase Storage.
 *
 * This repository handles the conversion of domain models to data transfer objects (DTOs),
 * performs image processing (compression and deduplication), and manages JSON catalog uploads.
 *
 * @property context The application context used for content resolution.
 * @property firebaseAuth Firebase Authentication instance to identify the current user.
 * @property firebaseStorage Firebase Storage instance for file management.
 */
class CatalogRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val remoteConfig: FirebaseRemoteConfig,
    private val firebaseFirestore: FirebaseFirestore
) : CatalogRepository {

    val catalogId get() = firebaseAuth.currentUser?.uid ?: throw Throwable("User has not uid")

    /**
     * Uploads an image to Firebase Storage with deduplication and size optimization.
     *
     * The process involves:
     * 1. Decoding the image from the provided [imageUri] (supports both content URIs and file paths).
     * 2. Iteratively compressing the bitmap to WebP format, reducing quality (starting at 80%)
     *    until the resulting byte array is under 1MB.
     * 3. Generating a SHA-256 hash of the final compressed image data to use as a unique filename.
     * 4. Checking if an image with the same hash already exists in the user's storage folder
     *    to avoid redundant uploads.
     * 5. Uploading the data only if it doesn't already exist.
     *
     * All heavy CPU operations (decoding, compression, hashing) are executed on [Dispatchers.Default].
     *
     * @param imageUri The URI or file path of the image to upload.
     * @return The download URL of the image in Firebase Storage, or an empty string if an error occurs.
     */
    override suspend fun uploadImage(imageUri: String): String {
        try {
            val data = withContext(Dispatchers.Default) {
                val uri = imageUri.toUri()
                val bitmap = if (uri.scheme == "content") {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                } else {
                    BitmapFactory.decodeFile(uri.path ?: imageUri)
                } ?: throw IllegalArgumentException("Could not decode bitmap from URI: $imageUri")

                var quality = 80
                var resultData: ByteArray
                val baos = ByteArrayOutputStream()

                do {
                    baos.reset()
                    // Compress to WebP format. Reduce quality iteratively if size > 1MB.
                    bitmap.compress(Bitmap.CompressFormat.WEBP, quality, baos)
                    resultData = baos.toByteArray()
                    quality -= 10
                } while (resultData.size > 1024 * 1024 && quality > 0)

                resultData
            }

            val hash = withContext(Dispatchers.Default) {
                MessageDigest.getInstance("SHA-256")
                    .digest(data)
                    .joinToString("") { "%02x".format(it) }
            }

            val imageName = "$hash.webp"
            val storageRef =
                firebaseStorage.reference.child("catalogs/$catalogId/images/$imageName")

            try {
                // Avoid uploading if the file already exists (deduplication)
                storageRef.metadata.await()
            } catch (e: Exception) {
                // Log.e(TAG, "uploadImage: ", e)
                storageRef.putBytes(data).await()
            }

            return storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Log.e(TAG, "uploadImage: ", e)
            return ""
        }
    }

    private val json = Json { encodeDefaults = true }

    /**
     * Uploads the catalog domain model as a JSON file to Firebase Storage.
     *
     * Converts the [catalog] to a [WebCatalogDto], serializes it to JSON, and uploads it
     * to a specific path for the user. Finally, it generates a URL for the web catalog viewer.
     *
     * @param catalog The domain model representing the catalog.
     * @return A URL pointing to the web-based catalog viewer with the encoded JSON URL.
     */
    override suspend fun uploadJsonCatalog(catalog: WebCatalogDomainModel): String {

        // map catalog to serialized dtos
        val catalogSerializable = catalog.toData(catalogId)

        // 4. Serialize to JSON and upload to the server
        val jsonCatalog = json.encodeToString(catalogSerializable)

        // Path: /catalogs/{catalogId}/{file}
        val storageRef = firebaseStorage.reference.child("catalogs/$catalogId/catalog.json")
        val data = jsonCatalog.toByteArray(Charsets.UTF_8)

        storageRef.putBytes(data).await()
        val jsonUrl = storageRef.downloadUrl.await().toString()


        return jsonUrl
    }

    override suspend fun getCatalogWebViewerUrl(): String {
        val uid = firebaseAuth.currentUser?.uid ?: return ""
        val publicKey = sha256(uid)
        val baseUrl = remoteConfig.getString("baseurl_catalog").ifEmpty {
            "https://www.imecatro.com/catalog/?url="
        }
        val safeJsonUrl = "https://apps.imecatro.com/c/$publicKey"
        val base64 = Base64.encodeToString(
            safeJsonUrl.toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        return baseUrl + base64
    }

    override suspend fun createsFinalUrl(jsonCatalogUrl: String): String {
//        // 5. Encode the JSON URL for the web viewer query parameter
//        val encodedUrl = withContext(Dispatchers.IO) {
//            URLEncoder.encode(jsonCatalogUrl, StandardCharsets.UTF_8.toString())
//        }

        // 6. Return the final web catalog URL
        val baseUrl = remoteConfig.getString("baseurl_catalog").ifEmpty {
            "https://www.imecatro.com/catalog/?url="
        }

        val publicKey = sha256(catalogId)

        val data = hashMapOf(
            "publicKey" to publicKey,
            "ownerUid" to catalogId,
            "firebaseUrl" to jsonCatalogUrl,
            "enabled" to true,
            "cacheTtlSeconds" to 300,
            "ownerApp" to "puntrosales-android",
            "updatedAt" to FieldValue.serverTimestamp(),
            "disabledAt" to null
        )

        firebaseFirestore
            .collection("catalog_public_routes")
            .document(publicKey)
            .set(data, SetOptions.merge())
            .await()

        val base64 = Base64.encodeToString(
            "https://apps.imecatro.com/c/$publicKey".toByteArray(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        return baseUrl + base64
    }

    override suspend fun previewUrl(jsonCatalogUrl: String): String {
        // 6. Return the final web catalog URL
        val previewBaseUrl = "https://www.imecatro.com/catalog/?catalog="

        return previewBaseUrl + withContext(Dispatchers.IO) {
            URLEncoder.encode(jsonCatalogUrl, StandardCharsets.UTF_8.toString())
        }
    }

    override suspend fun isCatalogPublished(): Boolean {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return false
            val publicKey = sha256(uid)
            val document = firebaseFirestore
                .collection("catalog_public_routes")
                .document(publicKey)
                .get()
                .await()
            document.exists() && document.getBoolean("enabled") == true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getPublishedCatalog(): WebCatalogDomainModel? {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return null
            val publicKey = sha256(uid)

            // 1. Check if the catalog is enabled in Firestore
            val document = firebaseFirestore
                .collection("catalog_public_routes")
                .document(publicKey)
                .get()
                .await()

            if (!document.exists() || document.getBoolean("enabled") != true) {
                return null
            }

            // 2. Fetch the catalog JSON from Storage
            val storageRef = firebaseStorage.reference.child("catalogs/$uid/catalog.json")
            val bytes = storageRef.getBytes(10 * 1024 * 1024).await() // 10MB limit
            val jsonString = String(bytes, Charsets.UTF_8)
            val dto = json.decodeFromString<WebCatalogDto>(jsonString)
            dto.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "getPublishedCatalog: Error fetching catalog", e)
            null
        }
    }

    override suspend fun unpublishCatalog() {
        val uid = firebaseAuth.currentUser?.uid
            ?: throw IllegalStateException("User is not authenticated")
        val publicKey = sha256(uid)
        firebaseFirestore
            .collection("catalog_public_routes")
            .document(publicKey)
            .update("enabled", false, "disabledAt", FieldValue.serverTimestamp())
            .await()
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }
}
