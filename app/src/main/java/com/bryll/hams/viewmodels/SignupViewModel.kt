package com.bryll.hams.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bryll.hams.data.StudentRegistrationData
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SignupViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel(){
    private val _signUp = MutableLiveData<UiState<ResponseData>>()
    val  signUp : LiveData<UiState<ResponseData>> get() = _signUp
    fun signUp(studentRegistrationData: StudentRegistrationData) {

        viewModelScope.launch {
            authRepository.signup(studentRegistrationData) {
                _signUp.postValue(it)
            }
        }
    }

}