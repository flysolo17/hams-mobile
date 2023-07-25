package com.bryll.hams.service

import com.bryll.hams.data.StudentRegistrationData
import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.models.Address
import com.bryll.hams.models.Contacts
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part


interface AuthService {
    @POST("student/insert")
    suspend fun registerStudent(@Body customerRegistrationRequest: StudentRegistrationData) : Response<ResponseData>

    @POST("student/login")
    @FormUrlEncoded
    suspend fun login(@Field("lrn") studentID : String,@Field("password") password : String) : Response<ResponseData>

    @Headers("Content-Type: application/json")
    @GET("student/")
    fun getStudentData(@Header("Authorization") token: String): Call<ResponseData>

    @POST("student/create-address")
    @FormUrlEncoded
    suspend fun addAddress(@Field("house_no") houseNo : Int,
                   @Field("street") street : String,
                   @Field("barangay") barangay : String,
                   @Field("municipality") municipality : String,
                   @Field("province") province : String,
                   @Field("country") country : String,
                   @Field("zip_code")zipCode : String,
                   @Field("type") type : Int,
                   @Header("Authorization") token: String
    ) : Response<Address>

    @FormUrlEncoded
    @POST("student/create-contact")
    suspend fun createContact(@Field("first_name") firstName: String?,
        @Field("middle_name") middleName: String?,
        @Field("last_name") lastName: String?,
        @Field("phone") phone: String?,
        @Field("type") type: Int,
        @Header("Authorization") token: String
    ): Response<Contacts?>

    @FormUrlEncoded
    @PATCH("student/update-birthday")
    suspend fun updateBirthday(@Field("birth_date") birthDay : String, @Header("Authorization") token: String) : Response<Boolean?>

    @Multipart
    @PATCH("student/update-profile")
    suspend fun uploadFile(@Part profile: MultipartBody.Part,@Header("Authorization") token: String) : Response<ResponseData>

    @PATCH("student/update-info")
    @FormUrlEncoded
    suspend fun updateInfo(
        @Field("first_name") firstName: String,
        @Field("middle_name") middle_name: String,
        @Field("last_name") last_name: String,
        @Field("extension_name") extension_name: String?,
        @Field("gender") gender: Int,
        @Field("nationality") nationality: String,
        @Field("email") email: String,
        @Header("Authorization") token: String
    ) : Response<ResponseData>
}