package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagDbModel (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
){
    companion object {
        val DEFAULT_TAGS = listOf(
            TagDbModel(1, "Family"),
            TagDbModel(2, "Company"),
            TagDbModel(3, "Emergency"),
            TagDbModel(4, "Friend")
        )
        val DEFAULT_TAG = DEFAULT_TAGS[0]

    }
}