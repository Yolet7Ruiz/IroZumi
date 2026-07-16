package com.irozumi.data.local

import android.content.Context
import androidx.room.Room

/**
 * DatabaseProvider sigue el patrón Singleton para asegurar una única instancia
 * y respeta SOLID al centralizar la creación de la DB fuera de la UI.
 */
object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "irozumi_database"
            ).build()
            instance = newInstance
            newInstance
        }
    }
}
