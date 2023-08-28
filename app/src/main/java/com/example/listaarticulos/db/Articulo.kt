package com.example.listaarticulos.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Articulo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var nombre: String,
    var comprado: Boolean
)