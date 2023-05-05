package com.example.phonebook.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phonebook.database.AppDatabase
import com.example.phonebook.database.DbMapper
import com.example.phonebook.database.Repository
import com.example.phonebook.domain.model.ColorModel
import com.example.phonebook.domain.model.PhoneBookModel
import com.example.phonebook.domain.model.TagModel
import com.example.phonebook.routing.MyPhoneBookRouter
import com.example.phonebook.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {
    val PhonesNotInTrash: LiveData<List<PhoneBookModel>> by lazy {
        repository.getAllPhonesNotInTrash()
    }

    private var _phoneEntry = MutableLiveData(PhoneBookModel())

    val phoneEntry: LiveData<PhoneBookModel> = _phoneEntry

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }
    val tags: LiveData<List<TagModel>> by lazy {
        repository.getAllTags()
    }

    val phonesInTrash by lazy { repository.getAllPhonesInTrash() }

    private var _selectedPhones = MutableLiveData<List<PhoneBookModel>>(listOf())

    val selectedPhones: LiveData<List<PhoneBookModel>> = _selectedPhones

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.phoneDao(), db.colorDao(), db.tagDao(),DbMapper())
    }

    fun onCreateNewPhoneClick() {
        _phoneEntry.value = PhoneBookModel()
        MyPhoneBookRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneClick(phone: PhoneBookModel) {
        _phoneEntry.value = phone
        MyPhoneBookRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneCheckedChange(phone: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)
        }
    }

    fun onPhoneSelected(phone: PhoneBookModel) {
        _selectedPhones.value = _selectedPhones.value!!.toMutableList().apply {
            if (contains(phone)) {
                remove(phone)
            } else {
                add(phone)
            }
        }
    }

    fun restorePhones(Phones: List<PhoneBookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restorePhonesFromTrash(Phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun permanentlyDeletePhones(phones: List<PhoneBookModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deletePhones(phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun onPhoneEntryChange(phone: PhoneBookModel) {
        _phoneEntry.value = phone
    }

    fun savePhone(phone: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)

            withContext(Dispatchers.Main) {
                MyPhoneBookRouter.navigateTo(Screen.Phones)

                _phoneEntry.value = PhoneBookModel()
            }
        }
    }

    fun movePhoneToTrash(phone: PhoneBookModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.movePhoneToTrash(phone.id)

            withContext(Dispatchers.Main) {
                MyPhoneBookRouter.navigateTo(Screen.Phones)
            }
        }
    }
}