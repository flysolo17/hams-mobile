package com.bryll.hams.viewmodels

import com.bryll.hams.utils.DataStorage
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bryll.hams.models.Students
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.datastore.DataStore
import com.bryll.hams.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore: DataStore,private val authRepository: AuthRepository) : ViewModel() {


    private val _student = MutableLiveData<UiState<Students>>()
    val student : LiveData<UiState<Students>> = _student

    fun getStudentInfo() {
        viewModelScope.launch {
            dataStore.getToken()?.let {token->
                authRepository.getStudentInfo(token) {
                    _student.postValue(it)
                }
            }
        }
    }
    fun deleteToken() {
        viewModelScope.launch {
            authRepository.logout().also {
                dataStore.deleteToken()
            }
        }

    }
}