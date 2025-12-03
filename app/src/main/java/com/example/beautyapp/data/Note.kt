package com.example.beautyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String,  //new - unique ID for each note
    val title: String,  //new - note title
    val content: String,  //new - note content (150 char max)
    val imagePath: String? = null,  //new - path to image in sandbox storage
    val timestamp: Long = System.currentTimeMillis()  //new - when note was created
)