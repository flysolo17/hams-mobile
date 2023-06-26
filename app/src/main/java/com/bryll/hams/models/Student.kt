package com.bryll.hams.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
@Parcelize
data class Student(
    val id: String ? = null,
    val email: String ? = null,
    val profile : String ? = null,
    val studentInfo: StudentInformation ? = null,
    val contacts: Contacts ? = null,
    val status: StudentStatus ? = null,
    val academics: List<Academic>? = null,
    val createdAt: Date  ? = null,
) : Parcelable {

}