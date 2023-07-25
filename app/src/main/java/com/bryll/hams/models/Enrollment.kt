package com.bryll.hams.models

import com.google.type.DateTime
import kotlinx.serialization.descriptors.StructureKind
import java.sql.Date

data class Enrollment(
    val id: Int,
    val student_id: Int,
    val grade_level: Int,
    val school_year: String,
    val track: String?,
    val strand: String?,
    val semester: Int?,
    val enrollment_date: java.util.Date,
    val updated_at: java.util.Date,
    val enrollment_types : String,
    val status: Int
)