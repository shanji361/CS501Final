package com.example.beautyapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LikedProduct::class, Note::class],  //new - added Note::class to entities
    version = 2,  //new - increment from 1 to 2 because we're adding a new table
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun likedProductDao(): LikedProductDao
    abstract fun noteDao(): NoteDao  //new - provide access to NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "beauty_app_database"
                )
                    .fallbackToDestructiveMigration()  //new - handle database version upgrade (will delete old data but that's okay for development)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}