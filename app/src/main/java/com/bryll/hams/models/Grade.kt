package com.bryll.hams.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Grade(
    val subjectID: String ? = null,
    val first: Double? = null,
    val second: Double? = null,
    val third: Double? = null,
    val fourth: Double? = null,
    val status: GradeStatus? = null
) : Parcelable
