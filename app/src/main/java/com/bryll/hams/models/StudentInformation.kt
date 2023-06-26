package com.bryll.hams.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class StudentInformation(
    val firstName:String ? = null,
    val middleName: String ? = null,
    val lastname: String ? = null,
    val gender: Gender ? = null,
    val nationality: String ? = null,
    val dob: Date ? = null
) : Parcelable{
}