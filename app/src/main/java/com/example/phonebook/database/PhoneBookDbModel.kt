package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneBookDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "color_id") val colorId: Long,
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    companion object {
        val DEFAULT_PHONES = listOf(
            PhoneBookDbModel(1, "Police", "191", 12, 3,  false),
            PhoneBookDbModel(2, "Ambulance", "1669", 12, 3, false),
            PhoneBookDbModel(3, "Fire Incident", "199", 12, 3, false),
        )
    }
}
