package com.example.listaarticulos.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ArticuloDao {

    @Query("SELECT * FROM articulo ORDER BY comprado")
    fun findAll(): List<Articulo>

    @Query("SELECT COUNT(*) FROM articulo")
    fun contar(): Int

    @Insert
    fun insertar(articulo: Articulo): Long

    @Update
    fun actualizar(articulo: Articulo)

    @Delete
    fun eliminar(articulo: Articulo)
}