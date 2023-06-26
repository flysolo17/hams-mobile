package com.bryll.hams.models

import java.util.Date

data class Classes(
    val id: String ?=null,
    val name: String ?=null,
    val schoolYear: String ?=null,
    val educationLevel: String?=null,
    val curriculum: List<Curriculum>?=null,
    val createdAt: Date ?=null
)
