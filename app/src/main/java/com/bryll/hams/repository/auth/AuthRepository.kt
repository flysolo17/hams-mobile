package com.bryll.hams.repository.auth

import com.bryll.hams.data.StudentRegistrationData
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.models.Address
import com.bryll.hams.models.Contacts
import com.bryll.hams.models.Gender
import com.bryll.hams.models.Students
import com.bryll.hams.utils.UiState
import java.io.File

interface AuthRepository {
    suspend fun signup(studentRegistrationData: StudentRegistrationData, result : (UiState<ResponseData>) -> Unit)
    suspend fun login(studentID : String,password : String , result: (UiState<ResponseData>) -> Unit)
    suspend fun getStudentInfo(token : String,result: (UiState<Students>) -> Unit)
    fun logout()
    suspend fun createAddress(houseNo : Int,street : String,barangay : String,municipality : String,province : String,country : String,zipCode : String,type : Int,token: String,result: (UiState<Address>) -> Unit)

    suspend fun createContact(firstName : String,middleName : String, lastName : String, phone : String, type : Int, token: String, result: (UiState<Contacts?>) -> Unit)

    suspend fun updateBirthDay(birthDay : String,token: String,result: (UiState<String?>) -> Unit)

    suspend fun uploadProfile(file : File,token: String,result: (UiState<String>) -> Unit)

    suspend fun updateInfo(firstName: String,middleName: String,lastName: String,extensionName : String?,gender : Int,nationality : String , email : String, token : String,result: (UiState<ResponseData>) -> Unit)

}