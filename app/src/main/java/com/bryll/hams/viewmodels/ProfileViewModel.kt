package com.bryll.hams.viewmodels

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bryll.hams.models.Students
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.datastore.DataStore
import com.bryll.hams.utils.UiState
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel  @Inject constructor(val dataStore: DataStore,val authRepository: AuthRepository): ViewModel() {

    private val _student = MutableLiveData<UiState<Students>>()
    val student : LiveData<UiState<Students>> = _student

    private val _updateBirthday = MutableLiveData<UiState<String?>>()
    val birthday : LiveData<UiState<String?>> = _updateBirthday

    private val _updateProfile = MutableLiveData<UiState<String>>()
    val profile : LiveData<UiState<String>> = _updateProfile
    fun getStudentInfo() {
        viewModelScope.launch {
            dataStore.getToken()?.let {token->
                authRepository.getStudentInfo(token) {
                    _student.postValue(it)
                }
            }
        }
    }
    fun updateBirthday(birthDay : String) {
        viewModelScope.launch {
            dataStore.getToken()?.let { token->
                authRepository.updateBirthDay(birthDay,token) {
                    _updateBirthday.postValue(it)
                }
            }
        }
    }
    fun updateProfile(file: File) {
        Log.d("student: ","Triggered")
        viewModelScope.launch {
            dataStore.getToken()?.let {token ->
                authRepository.uploadProfile(file,token) {
                    _updateProfile.postValue(it)
                }
            }
        }
    }
}