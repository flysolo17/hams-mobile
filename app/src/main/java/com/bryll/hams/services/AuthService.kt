package com.bryll.hams.services

import android.net.Uri
import com.bryll.hams.models.Student
import com.bryll.hams.utils.UiState
import com.google.firebase.auth.FirebaseUser

interface AuthService {
    fun signup(email: String,password: String,student: Student, result : (UiState<Student>) -> Unit)
    fun login(email: String, password : String,result: (UiState<FirebaseUser>) -> Unit)
    fun findStudentByID(id : String,result: (UiState<Student>) -> Unit)
    fun saveStudent(student: Student,result: (UiState<String>) -> Unit)
    fun getUserByEmail(email : String ,result: (UiState<Student>) -> Unit)
    fun reAuthenticateAccount(user: FirebaseUser, email: String, password: String, result: (UiState<FirebaseUser>) -> Unit)
    fun resetPassword(email : String,result: (UiState<String>) -> Unit)
    fun changePassword(user: FirebaseUser, password: String, result: (UiState<String>) -> Unit)
    fun updateAccount(student: Student ,result: (UiState<String>) -> Unit)
    fun uploadProfile(studentID : String,uri : Uri,type : String,result: (UiState<String>) -> Unit)
}