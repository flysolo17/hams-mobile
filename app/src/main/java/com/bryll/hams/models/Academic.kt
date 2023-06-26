package com.bryll.hams.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Academic(
   val id: String? = null,
   val classID: String? = null,
   val sem: String? = null,
   val grades: List<Grade>? = null,
   val academicStatus : AcademicStatus ?=null
) : Parcelable
