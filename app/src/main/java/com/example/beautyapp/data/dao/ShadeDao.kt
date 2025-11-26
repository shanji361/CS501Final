package com.example.beautyapp.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.beautyapp.data.entities.Shade

// this dao file, writes the QUERY and interacts with SQLite db, to get data from our shades table
// gets one shade
@Dao
interface ShadeDao{
    @Query("SELECT * FROM shades")
    fun getAllShades(): List<Shade>

    @Query ("SELECT * FROM shades WHERE shade_id=:id")
    fun getShadeById(id:Int): Shade?

}