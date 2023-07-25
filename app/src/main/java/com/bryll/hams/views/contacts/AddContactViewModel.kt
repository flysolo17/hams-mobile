package com.bryll.hams.views.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bryll.hams.models.Contacts
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.datastore.DataStore
import com.bryll.hams.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(val dataStore: DataStore,val authRepository: AuthRepository) : ViewModel(){
    private var _addContact = MutableLiveData<UiState<Contacts?>>()
    val contacts :LiveData<UiState<Contacts?>> = _addContact

    fun saveContact(firstName : String,middleName : String, lastName : String, phone : String, type : Int) {
        viewModelScope.launch {
            dataStore.getToken()?.let {token ->
                authRepository.createContact(firstName,middleName,lastName,phone,type,token) {
                    _addContact.postValue(it)
                }
            }
        }
    }
}