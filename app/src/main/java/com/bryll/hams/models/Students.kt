package com.bryll.hams.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.sql.Date
@Serializable
@Parcelize
 class Students(
    val lrn: String,
    var email: String?,
    val profile: String?,
    var first_name: String?,
    var middle_name: String?,
    var last_name: String?,
    var extension_name: String?,
    var birth_date: String?,
    var gender: Int?,
    var nationality: String?,
    var addresses: MutableList<Address>? = mutableListOf(),
    var contacts: MutableList<Contacts>? = mutableListOf()
) : Parcelable {
   fun hasNullValues(): Boolean {
      return javaClass.declaredFields
         .filterNot { it.name == "extension_name" }
         .map{ field ->
         field.isAccessible = true
         field.get(this)
      }.any { value ->
         value == null
      }
   }
}



