package com.bryll.hams.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contacts(
   val name: String? = null,
val phone: String? = null,
val  type: ContactType? = null,
) : Parcelable{
}