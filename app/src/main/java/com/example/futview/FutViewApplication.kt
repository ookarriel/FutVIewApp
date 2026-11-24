package com.example.futview

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings

/** Habilita la persistencia offline de Firestore para toda la app. */
class FutViewApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.firestore.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
            setLocalCacheSettings(
                persistentCacheSettings { setSizeBytes(CACHE_SIZE_BYTES) }
            )
        }
    }

    private companion object {
        const val CACHE_SIZE_BYTES: Long = 64L * 1024L * 1024L // 64 MB
    }
}
