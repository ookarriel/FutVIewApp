package com.example.futview

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings

/** Configura la caché offline de Firestore y el user agent de osmdroid (OpenStreetMap). */
class FutViewApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // osmdroid exige un user agent identificable para descargar tiles de OSM.
        org.osmdroid.config.Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = getDir("osmdroid", MODE_PRIVATE)
            osmdroidTileCache = getDir("osmdroid_tiles", MODE_PRIVATE)
        }
        // Persistencia offline: activada por defecto; aquí solo acotamos el tamaño de caché.
        // Importante: no mezclar con isPersistenceEnabled/setCacheSizeBytes (deprecados y excluyentes).
        Firebase.firestore.firestoreSettings = firestoreSettings {
            setLocalCacheSettings(
                persistentCacheSettings { setSizeBytes(CACHE_SIZE_BYTES) }
            )
        }
    }

    private companion object {
        const val CACHE_SIZE_BYTES: Long = 64L * 1024L * 1024L // 64 MB
    }
}
