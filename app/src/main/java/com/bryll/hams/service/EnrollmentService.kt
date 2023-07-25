package com.bryll.hams.service

import com.bryll.hams.data.response.ResponseData
import com.bryll.hams.models.Enrollment
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface EnrollmentService {

    @GET("enrollment/")
    suspend fun getEnrollments(@Header("Authorization") token : String) : Response<List<Enrollment>>


    @FormUrlEncoded
    @POST("enrollment/create-enrollment-request")
    suspend fun createEnrollmentRequest(
        @Field("grade_level") gradeLevel: Int,
        @Field("school_year") schoolYear: String,
        @Field("track") track: String? = null,
        @Field("strand") strand: String? = null,
        @Field("semester") semester: Int? = null,
        @Field("enrollment_types") enrollmentTypes: String,
        @Header("Authorization") token: String
    ) : Response<ResponseData>


    @PATCH("enrollment/cancel-enrollment")
    suspend fun cancelEnrollment(
        @Query("id") id : Int,
        @Header("Authorization") token: String
    ) : Response<ResponseData>
}