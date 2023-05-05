package com.example.phonebook.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TagDao {
    // get all tag
    @Query("SELECT * FROM TagDbModel")
    fun getAll(): LiveData<List<TagDbModel>>

    // get all tag with synchronous
    @Query("SELECT * FROM TagDbModel")
    fun getAllSync(): List<TagDbModel>

    // insert
    @Insert
    fun insertAll(vararg tagDbModels: TagDbModel)
}