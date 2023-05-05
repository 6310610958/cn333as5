package com.example.phonebook.database

import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.domain.model.TagModel


class DbMapper {
    // Create list of PhoneBookModels by pairing each note with a color
    fun mapPhones(
        phoneDbModels: List<PhoneBookDbModel>,
        colorDbModels: Map<Long, ColorDbModel>,
        tagDbModels: Map<Long, TagDbModel>
    ): List<PhoneBookModel> = phoneDbModels.map {
        val colorDbModel = colorDbModels[it.colorId]
            ?: throw RuntimeException("Color for colorId: ${it.colorId} was not found. Make sure that all colors are passed to this method")

        val tagDbModel = tagDbModels[it.tagId]
            ?: throw RuntimeException("Tag for tagId: ${it.tagId} was not found.")

        mapPhone(it, colorDbModel, tagDbModel)
    }

    // convert NoteDbModel to PhoneBookModel
    fun mapPhone(phoneDbModel: PhoneBookDbModel, colorDbModel: ColorDbModel, tagDbModel: TagDbModel): PhoneBookModel {
        val color = mapColor(colorDbModel)
        val tag = mapTag(tagDbModel)
        return with(phoneDbModel) { PhoneBookModel(id, name, phone, color, tag) }
    }

    fun mapColors(colorDbModels: List<ColorDbModel>): List<ColorModel> =
        colorDbModels.map { mapColor(it) }

    fun mapColor(colorDbModel: ColorDbModel): ColorModel =
        with(colorDbModel) { ColorModel(id, name, hex) }

    fun mapTags(tagDbModels: List<TagDbModel>): List<TagModel> =
        tagDbModels.map { mapTag(it) }

    fun mapTag(tagDbModel: TagDbModel): TagModel =
        with(tagDbModel) { TagModel(id, name) }

    fun mapDbPhone(phonebook: PhoneBookModel): PhoneBookDbModel =
        with(phonebook) {
            if (id == NEW_PHONE_ID)
                PhoneBookDbModel(
                    name = name,
                    phone = phone,
                    colorId = color.id,
                    tagId = tag.id,
                    isInTrash = false
                )
            else
                PhoneBookDbModel(id, name, phone, color.id, tag.id, false )
        }
}