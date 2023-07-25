package com.bryll.hams.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.datastore.DataStore
import com.bryll.hams.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class UpdateAccountViewModel @Inject constructor(val dataStore: DataStore,val authRepository: AuthRepository) : ViewModel(){
    private val _updateAccount = MutableLiveData<UiState<ResponseData>>()
    val updateAccount : LiveData<UiState<ResponseData>> = _updateAccount

    fun updateInfo(firstName: String,
                   middleName: String,
                   lastName: String,
                   extensionName: String?,
                   gender: Int,
                   nationality: String,
                   email: String) {
        viewModelScope.launch {
            dataStore.getToken()?.let {token ->
                authRepository.updateInfo(firstName,middleName,lastName,extensionName,gender,nationality,email,token) {
                    _updateAccount.postValue(it)
                }
            }
        }
    }
}