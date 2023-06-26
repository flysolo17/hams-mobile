package com.bryll.hams.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.bryll.hams.models.Student
import com.bryll.hams.services.AuthService
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class AuthViewModel constructor(
    private val authService: AuthService,
    private val savedStateHandle: SavedStateHandle

) : ViewModel() {


    private val _login = MutableLiveData<UiState<FirebaseUser>>()
    val login : LiveData<UiState<FirebaseUser>> = _login


    private val _student = MutableLiveData<UiState<Student>>()
    val student : LiveData<UiState<Student>> = _student

    private val _signup = MutableLiveData<UiState<Student>>()
    val signup : LiveData<UiState<Student>> = _signup


    private val _saveStudent = MutableLiveData<UiState<String>>()
    val saveStudent : LiveData<UiState<String>> = _saveStudent


    private val _findUserByEmail = MutableLiveData<UiState<Student>>()
    val findUserByEmail : LiveData<UiState<Student>> = _findUserByEmail

    private val _resetPassword = MutableLiveData<UiState<String>>()
    val resetPassword : LiveData<UiState<String>> = _resetPassword


    private val _reauthenticate= MutableLiveData<UiState<FirebaseUser>>()
    val reauthenticate : LiveData<UiState<FirebaseUser>> = _reauthenticate


    private val _changePassword = MutableLiveData<UiState<String>>()
    val changePassword : LiveData<UiState<String>> = _changePassword


    fun login(email: String,password : String) {
        authService.login(email,password) {
            _login.postValue(it)
        }
    }

    fun getUserByID(id : String) {
        authService.findStudentByID(id) {
            _student.postValue(it)
        }
    }
    fun signup(email: String,password: String,student: Student) {
        authService.signup(email,password,student) {
            _signup.postValue(it)
        }
    }
    fun saveStudent(student: Student) {
        authService.saveStudent(student) {
            _saveStudent.postValue(it)
        }
    }
    fun findStudentByEmail(email: String) {
        authService.getUserByEmail(email) {
            _findUserByEmail.postValue(it)
        }
    }
    fun forgotPassword(email: String) {
        authService.resetPassword(email) {
            _resetPassword.postValue(it)
        }
    }
    fun changePassword(user: FirebaseUser,password: String) {
        authService.changePassword(user,password) {
            _changePassword.postValue(it)
        }
    }
    fun reauthenticate(user: FirebaseUser,email: String,password: String) {
        authService.reAuthenticateAccount(user,email,password) {
            _reauthenticate.postValue(it)
        }
    }
    companion object {
        fun provideFactory(
            myRepository: AuthServiceImpl,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return AuthViewModel(myRepository, handle) as T
                }
            }

    }
}