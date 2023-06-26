package com.bryll.hams.services.enrollment

import com.bryll.hams.models.Academic
import com.bryll.hams.models.Classes
import com.bryll.hams.models.Subjects
import com.bryll.hams.utils.UiState

interface EnrollmentService {
    fun getAllClass(level : String ,result : (UiState<List<Classes>>) -> Unit)
    fun getAllSubjects(result: (UiState<List<Subjects>>) -> Unit)
    fun submitEnrollment(studentID : String,academic: Academic,result: (UiState<String>) -> Unit)
}