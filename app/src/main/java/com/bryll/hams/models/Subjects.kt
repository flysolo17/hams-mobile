package com.bryll.hams.models

import java.util.Date

data class Subjects(
    val id: String  ?=null,
    val name: String ?=null,
    val code: String ?=null,
    val units: Int ?=null,
    val teacher: String ?=null,
    val createdAt: Date?=null
)
