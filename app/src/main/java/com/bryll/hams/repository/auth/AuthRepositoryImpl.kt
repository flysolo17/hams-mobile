package com.bryll.hams.repository.auth

import android.util.Log
import com.bryll.hams.data.StudentRegistrationData
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.models.Address
import com.bryll.hams.models.Contacts
import com.bryll.hams.models.Students
import com.bryll.hams.service.AuthService
import com.bryll.hams.utils.UiState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Part
import java.io.File
import java.io.IOException


class AuthRepositoryImpl(private val authService: AuthService) : AuthRepository {
    private var students : Students ? = null;
    val gson = Gson()
    override suspend fun signup(
        studentRegistrationData: StudentRegistrationData,
        result: (UiState<ResponseData>) -> Unit
    ) {
        result.invoke(UiState.LOADING)
        try {
            
            delay(1000)
            val response = authService.registerStudent(studentRegistrationData)
            if (response.isSuccessful) {
                result.invoke(UiState.SUCCESS(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                val errorResponse = Json.decodeFromString<JsonObject>(errorBody)
                result.invoke(UiState.FAILED("${response.code()} : ${errorResponse["message"]}"))
            }

        } catch (e : HttpException) {
            result.invoke(UiState.FAILED(e.localizedMessage?.toString() ?: "Unknown Error"))

        } catch (e : IOException) {
            result.invoke(UiState.FAILED(e.localizedMessage?.toString()  ?: "Unknown Error"))
        } catch (e : Exception) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun login(
        studentID: String,
        password: String,
        result: (UiState<ResponseData>) -> Unit
    ) {
        try {
            delay(1000)
            result.invoke(UiState.LOADING)
            //user == user token
            val response = authService.login(studentID, password)
            if (response.isSuccessful) {
                result.invoke(UiState.SUCCESS(response.body()!!))
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                val errorResponse = Json.decodeFromString<JsonObject>(errorBody)
                result.invoke(UiState.FAILED("${response.code()} : ${errorResponse["message"]}"))
            }

        } catch (e : HttpException) {
            result.invoke(UiState.FAILED(e.message()))
        } catch (e : IOException) {
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun getStudentInfo(token: String, result: (UiState<Students>) -> Unit) {
        result.invoke(UiState.LOADING)

        if (students == null) {
            delay(1000)
            authService.getStudentData("Bearer $token").enqueue(object : Callback<ResponseData> {
                override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        val body = data?.data
                        val jsonString = gson.toJson(body)
                        Log.d("student",jsonString)
                        val student = gson.fromJson(jsonString,Students::class.java)
                        students = student
                        result.invoke(UiState.SUCCESS(students!!))
                    } else {
                        val errorBody = response.errorBody()?.string() ?: ""
                        Log.d("student",errorBody)
                        result.invoke(UiState.FAILED("${response.code()} : $errorBody"))
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    result.invoke(UiState.FAILED(t.message!!))
                    Log.d("student",t.message!!)
                }
            })
        } else {
            result.invoke(UiState.SUCCESS(students!!))

        }
    }

    override fun logout() {
        students = null
    }

    override suspend fun createAddress(
        houseNo: Int,
        street: String,
        barangay: String,
        municipality: String,
        province: String,
        country: String,
        zipCode: String,
        type: Int,
        token: String,
        result: (UiState<Address>) -> Unit
    ) {
        result.invoke(UiState.LOADING)

        try {
            delay(1000)
            val response = authService.addAddress(houseNo, street, barangay, municipality, province, country, zipCode, type, "Bearer $token")
            if (response.isSuccessful) {
                val data = response.body() as Address
                students?.let {
                    it.addresses?.add(data)
                    result.invoke(UiState.SUCCESS(data))
                }
                Log.d("student",data.toString())


            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.d("student",errorBody)
                result.invoke(UiState.FAILED("${response.code()} : $errorBody"))
            }

        } catch (e : HttpException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message()))
        } catch (e : IOException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        } catch (e : Exception) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun createContact(
        firstName: String,
        middleName: String,
        lastName: String,
        phone: String,
        type: Int,
        token: String,
        result: (UiState<Contacts?>) -> Unit
    ) {
        try {
            delay(1000)
            val response = authService.createContact(firstName, middleName, lastName, phone, type ,"Bearer $token")
            if (response.isSuccessful) {
                val data = response.body()

                data?.let { contact->
                    students?.let {
                        it.contacts?.add(contact)
                        result.invoke(UiState.SUCCESS(contact))
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.d("student",errorBody)
                result.invoke(UiState.FAILED("${response.code()} : $errorBody"))
            }

        } catch (e : HttpException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message()))
        } catch (e : IOException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        } catch (e : Exception) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun updateBirthDay(
        birthDay: String,
        token: String,
        result: (UiState<String?>) -> Unit
    ) {
        try {
            delay(1000)
            val response = authService.updateBirthday(birthDay,"Bearer $token")
            if (response.isSuccessful) {
                val data = response.body()
                data?.let { _ ->
                    if (data) {
                        students?.let {
                            it.birth_date = birthDay
                        }
                        result.invoke(UiState.SUCCESS(birthDay))
                    } else {
                        result.invoke(UiState.FAILED("${response.code()}: Unknown Error"))
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.d("student",errorBody)
                result.invoke(UiState.FAILED("${response.code()} : $errorBody"))
            }
        } catch (e : HttpException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message()))
        } catch (e : IOException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }catch (e : Exception) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun uploadProfile(file: File,token: String, result: (UiState<String>) -> Unit) {
        val requestBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("profile", file.name, requestBody)
        result.invoke(UiState.LOADING)
        try {
            delay(1000)
            val response = authService.uploadFile(filePart,"Bearer $token")
            if (response.isSuccessful) {
                val data = response.body()
                data?.let {res->
                    result.invoke(UiState.SUCCESS(res.data.toString()))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.d("student",errorBody)
                result.invoke(UiState.FAILED("${response.code()} : $errorBody"))
            }
        } catch (e : HttpException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message()))
        } catch (e : IOException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        } catch (e : Exception) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }

    override suspend fun updateInfo(
        firstName: String,
        middleName: String,
        lastName: String,
        extensionName: String?,
        gender: Int,
        nationality: String,
        email: String,
        token: String,
        result: (UiState<ResponseData>) -> Unit
    ) {
        result.invoke(UiState.LOADING)
        try {
            delay(1000)
            val response = authService.updateInfo(firstName,middleName,lastName,extensionName,gender,nationality,email, "Bearer $token")
            if (response.isSuccessful) {
                val data = response.body()
                data?.let { res->
                    if (res.success == true) {
                        students?.let {
                            it.first_name = firstName
                            it.middle_name = middleName
                            it.last_name = lastName
                            it.extension_name = extensionName
                            it.gender = gender
                            it.nationality = nationality
                            it.email= email
                        }
                    }
                    result.invoke(UiState.SUCCESS(res))
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.d("student",errorBody)
                result.invoke(UiState.FAILED("${response.code()} : $errorBody"))
            }
        } catch (e : HttpException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message()))
        } catch (e : IOException) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        } catch (e : Exception) {
            Log.d("student",e.message.toString())
            result.invoke(UiState.FAILED(e.message!!))
        }
    }
}