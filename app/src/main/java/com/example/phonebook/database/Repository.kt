package com.example.phonebook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.domain.model.TagModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val phoneDao: PhoneBookDao,
    private val colorDao: ColorDao,
    private val tagDao: TagDao,
    private val dbMapper: DbMapper
) {


    private val phonesNotInTrashLiveData: MutableLiveData<List<PhoneBookModel>> by lazy {
        MutableLiveData<List<PhoneBookModel>>()
    }

    fun getAllPhonesNotInTrash(): LiveData<List<PhoneBookModel>> = phonesNotInTrashLiveData


    private val phonesInTrashLiveData: MutableLiveData<List<PhoneBookModel>> by lazy {
        MutableLiveData<List<PhoneBookModel>>()
    }

    fun getAllPhonesInTrash(): LiveData<List<PhoneBookModel>> = phonesInTrashLiveData

    init {
        initDatabase(this::updatePhonesLiveData)
    }

    /**
     * Populates database with colors if it is empty.
     */
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // Prepopulate colors
            val colors = ColorDbModel.DEFAULT_COLORS.toTypedArray()
            val dbColors = colorDao.getAllSync()
            val tags = TagDbModel.DEFAULT_TAGS.toTypedArray()
            val dbTags = tagDao.getAllSync()
            if (dbColors.isNullOrEmpty()) {
                colorDao.insertAll(*colors)
            }
            if (dbTags.isNullOrEmpty()) {
                tagDao.insertAll(*tags)
            }

            val phones = PhoneBookDbModel.DEFAULT_PHONES.toTypedArray()
            val dbPhones = phoneDao.getAllSync()
            if (dbPhones.isNullOrEmpty()) {
                phoneDao.insertAll(*phones)
            }

            postInitAction.invoke()
        }
    }


    private fun getAllPhonesDependingOnTrashStateSync(inTrash: Boolean): List<PhoneBookModel> {
        val colorDbModels: Map<Long, ColorDbModel> = colorDao.getAllSync().map { it.id to it }.toMap()
        val tagDbModels: Map<Long, TagDbModel> = tagDao.getAllSync().map { it.id to it }.toMap()
        val dbPhones: List<PhoneBookDbModel> =
            phoneDao.getAllSync().filter { it.isInTrash == inTrash }
        return dbMapper.mapPhones(dbPhones, colorDbModels, tagDbModels)
    }

    fun insertPhone(phone: PhoneBookModel) {
        phoneDao.insert(dbMapper.mapDbPhone(phone))
        updatePhonesLiveData()
    }

    fun deletePhones(phoneIds: List<Long>) {
        phoneDao.delete(phoneIds)
        updatePhonesLiveData()
    }

    fun movePhoneToTrash(phoneId: Long) {
        val dbPhone = phoneDao.findByIdSync(phoneId)
        val newDbPhone = dbPhone.copy(isInTrash = true)
        phoneDao.insert(newDbPhone)
        updatePhonesLiveData()
    }

    fun restorePhonesFromTrash(phoneIds: List<Long>) {
        val dbPhonesInTrash = phoneDao.getPhonesByIdsSync(phoneIds)
        dbPhonesInTrash.forEach {
            val newDbPhone = it.copy(isInTrash = false)
            phoneDao.insert(newDbPhone)
        }
        updatePhonesLiveData()
    }

    fun getAllColors(): LiveData<List<ColorModel>> =
        Transformations.map(colorDao.getAll()) { dbMapper.mapColors(it) }

    fun getAllTags(): LiveData<List<TagModel>> =
        Transformations.map(tagDao.getAll()) { dbMapper.mapTags(it) }

    private fun updatePhonesLiveData() {
        phonesNotInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(false))
        phonesInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(true))
    }
}