package com.bryll.hams.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.models.Enrollment
import com.bryll.hams.models.Students
import com.bryll.hams.repository.auth.AuthRepository
import com.bryll.hams.repository.datastore.DataStore
import com.bryll.hams.repository.enrollment.EnrollmentRepository
import com.bryll.hams.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val dataStore: DataStore,
    private val authRepository: AuthRepository,
    private val enrollmentRepository: EnrollmentRepository
) :ViewModel() {
    private val _student = MutableLiveData<UiState<Students>>()
    val student : LiveData<UiState<Students>> = _student


    private val _getEnrollments = MutableLiveData<UiState<List<Enrollment>>>()
    val enrollments : LiveData<UiState<List<Enrollment>>> = _getEnrollments

    fun getStudentInfo() {
        viewModelScope.launch {
            dataStore.getToken()?.let { token->
                authRepository.getStudentInfo(token) {
                    _student.postValue(it)
                }
            }
        }
    }



    fun getEnrollments() {
        viewModelScope.launch {
            dataStore.getToken()?.let {token->
                enrollmentRepository.getEnrollments(token) {

                    _getEnrollments.postValue(it)
                }
            }
        }
    }

    private val _createRequest = MutableLiveData<UiState<ResponseData>>()
    val enrollmentRequest : LiveData<UiState<ResponseData>> = _createRequest


    fun createEnrollment(gradeLevel : Int, schoolYear : String, track : String?  = null, strand : String?  = null, semester : Int?  = null, enrollmentTypes : List<String>, ) {
        viewModelScope.launch {
            dataStore.getToken()?.let {token->
                enrollmentRepository.createEnrollment(gradeLevel,schoolYear,track ,strand,semester,enrollmentTypes.joinToString(","),token) {
                    _createRequest.postValue(it)
                }
            }
        }
    }


    private val _cancelEnrollment = MutableLiveData<UiState<ResponseData>>()
    val cancelEnrollment : LiveData<UiState<ResponseData>> = _cancelEnrollment


    fun cancelEnrollment(id : Int) {
        viewModelScope.launch {
            dataStore.getToken()?.let {token->
                enrollmentRepository.cancelEnrollment(id,token) {
                    _cancelEnrollment.postValue(it)
                }
            }
        }
    }
}