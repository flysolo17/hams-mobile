package com.bryll.hams.repository.enrollment

import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.models.Enrollment
import com.bryll.hams.utils.UiState

interface EnrollmentRepository {

    suspend fun getEnrollments(token : String,result : (UiState<List<Enrollment>>) -> Unit)
    suspend fun createEnrollment(
        gradeLevel: Int,
        schoolYear: String,
        track: String? = null,
        strand: String?  = null,
        semester: Int? = null,
        enrollmentTypes: String,
        token: String, result: (UiState<ResponseData>) -> Unit)

    suspend fun cancelEnrollment(id : Int,token: String ,result: (UiState<ResponseData>) -> Unit)
}