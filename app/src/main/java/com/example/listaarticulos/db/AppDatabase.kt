package com.example.listaarticulos.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Articulo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articuloDao(): ArticuloDao

    companion object {
        @Volatile
        private var BASE_DATOS: AppDatabase? = null

        fun getInstance(contexto: Context): AppDatabase {
            return BASE_DATOS ?: synchronized(this) {
                Room.databaseBuilder(
                    contexto.applicationContext,
                    AppDatabase::class.java,
                    "articulos.bd"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it }
            }
        }
    }
}