package com.bryll.hams.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.bryll.hams.data.StudentRegistrationData
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.auth.AuthRepositoryImpl
import com.bryll.hams.repository.datastore.DataStore
import com.bryll.hams.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStore: DataStore,
    private  val authRepository: AuthRepository
) : ViewModel() {

    private val _login = MutableLiveData<UiState<ResponseData>>()
    val login : LiveData<UiState<ResponseData>> get() = _login
    fun login(studentID : String ,password : String) {
        viewModelScope.launch {
            delay(2)
            authRepository.login(studentID,password) {
                _login.postValue(it)
            }
        }
    }

    fun saveUser(token : String) {
        viewModelScope.launch {
            dataStore.saveToken(token)
        }
    }


}