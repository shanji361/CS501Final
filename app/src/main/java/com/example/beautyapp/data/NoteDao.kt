package com.example.beautyapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")  //new - get all notes, newest first
    fun getAllNotes(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)  //new - insert or update note
    suspend fun insertNote(note: Note)

    @Delete  //new - delete a note
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE id = :noteId")  //new - delete by ID
    suspend fun deleteNoteById(noteId: String)
}