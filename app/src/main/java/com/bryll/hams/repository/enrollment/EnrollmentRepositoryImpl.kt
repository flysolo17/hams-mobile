package com.bryll.hams.repository.enrollment

import android.util.Log
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.models.Enrollment
import com.bryll.hams.service.EnrollmentService
import com.bryll.hams.utils.UiState
import kotlinx.coroutines.delay
import java.lang.Exception

class EnrollmentRepositoryImpl(private val enrollmentService: EnrollmentService) : EnrollmentRepository {
    override suspend fun getEnrollments(
        token: String,
        result: (UiState<List<Enrollment>>) -> Unit
    ) {
        try {

            delay(1000)
            val response = enrollmentService.getEnrollments("Bearer $token")
            if (response.isSuccessful) {
                val  body = response.body()
                Log.d("enrollment",body.toString())
                result.invoke(UiState.SUCCESS(body ?: listOf()))

            } else {
                result.invoke(UiState.FAILED("Unknown Error!"))
            }
        } catch (e : Exception) {
            Log.d("enrollment",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun createEnrollment(
        gradeLevel: Int,
        schoolYear: String,
        track: String?,
        strand: String?,
        semester: Int?,
        enrollmentTypes: String,
        token: String,
        result: (UiState<ResponseData>) -> Unit
    ) {
        result.invoke(UiState.LOADING)
        try {
            delay(1000)
            val response = enrollmentService.createEnrollmentRequest(gradeLevel, schoolYear,track, strand, semester, enrollmentTypes,"Bearer $token")
            if (response.isSuccessful) {
                val  body = response.body()
                Log.d("enrollment",body.toString())
                body?.let {
                    result.invoke(UiState.SUCCESS(it))
                }
            } else {
                result.invoke(UiState.FAILED("Unknown Error!"))
            }
        } catch (e : Exception) {
            Log.d("enrollment",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun cancelEnrollment(
        id: Int,
        token: String,
        result: (UiState<ResponseData>) -> Unit
    ) {
        result.invoke(UiState.LOADING)
        try {
            delay(1000)
            val response = enrollmentService.cancelEnrollment(id,"Bearer $token")
            if (response.isSuccessful) {
                val  body = response.body()
                Log.d("enrollment",body.toString())
                body?.let {
                    result.invoke(UiState.SUCCESS(it))
                }
            } else {
                result.invoke(UiState.FAILED("Unknown Error!"))
            }
        } catch (e : Exception) {
            Log.d("enrollment",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }
}