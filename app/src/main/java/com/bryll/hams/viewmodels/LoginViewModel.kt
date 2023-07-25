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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val dataStore: DataStore,private val authRepository: AuthRepository) : ViewModel() {
    private val _login = MutableLiveData<UiState<ResponseData>>()
    val login : LiveData<UiState<ResponseData>> get() = _login

    fun loginFunc(id : String ,password : String) {
        viewModelScope.launch {
            authRepository.login(id,password) {
                _login.postValue(it)
            }
        }
    }
    fun getToken() : String? = runBlocking {
        dataStore.getToken()
    }
    fun saveToken(token : String) {
        viewModelScope.launch {
            dataStore.saveToken(token)
        }
    }

}