package com.bryll.hams.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Contacts(
   val id: Int,
   val type: Int?,
   val phone: String?,
   val last_name: String?,
   val first_name: String?,
   val middle_name: String?
) : Parcelable
