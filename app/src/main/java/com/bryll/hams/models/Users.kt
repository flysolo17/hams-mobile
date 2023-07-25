package com.bryll.hams.models

import java.util.Date

data class Users(
    val id: String?=null,
    val email: String?=null,
    val name: String?=null,
    val profile: String?=null,
    val type: UserType ? = null,
    val createdAt: Date? = null
)
