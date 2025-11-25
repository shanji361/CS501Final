package com.example.beautyapp.data.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

//this file creates the shade table
@Entity(tableName = "shades")
data class Shade(
    @PrimaryKey val shade_id: Int,
    val hex_code: String,
    val undertone: String,
    val description: String
)
