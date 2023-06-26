package com.bryll.hams.viewmodels

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.bryll.hams.models.Academic
import com.bryll.hams.models.Classes
import com.bryll.hams.models.Student
import com.bryll.hams.services.AuthServiceImpl
import com.bryll.hams.services.enrollment.EnrollmentService
import com.bryll.hams.services.enrollment.EnrollmentServiceImpl
import com.bryll.hams.utils.UiState

class EnrollmentViewModel(
    private val enrollmentService: EnrollmentService,
    private val savedStateHandle: SavedStateHandle
) :ViewModel() {
    private val _classes = MutableLiveData<UiState<List<Classes>>>()
    val classes : LiveData<UiState<List<Classes>>> = _classes

    private val  _submitApplication = MutableLiveData<UiState<String>>()
    val submitApplication : LiveData<UiState<String>> =_submitApplication

    fun getAllClasses(level : String) {
        enrollmentService.getAllClass(level = level) {
            _classes.postValue(it)
        }
    }
    fun submitEnrollmentForm(studentID : String ,academic: Academic) {
        enrollmentService.submitEnrollment(studentID,academic) {
            _submitApplication.postValue(it)
        }
    }


    companion object {
        fun provideFactory(
            myRepository: EnrollmentServiceImpl,
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
                    return EnrollmentViewModel(myRepository, handle) as T
                }
            }
    }
}