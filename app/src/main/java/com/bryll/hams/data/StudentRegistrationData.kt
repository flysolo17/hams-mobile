package com.bryll.hams.data

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class StudentRegistrationData private constructor(
    val lrn: String,
    val first_name: String?,
    val middle_name: String?,
    val last_name: String?,
    val gender: Int ?,
    val password: String
) {
    // Builder class for StudentRegistrationData
    class Builder {
        private var lrn: String = ""
        private var firstName: String? = null
        private var middleName: String? = null
        private var lastName: String? = null
        private var gender: Int? = null

        private var password: String = ""

        fun setLRN(lrn: String) = apply {
            this.lrn = lrn
            return@apply
        }

        fun setFirstName(firstName: String?) = apply {
            this.firstName = firstName
         return@apply
        }
        fun setMiddleName(middleName: String?) = apply {
            this.middleName = middleName
            return@apply
        }
        fun setLastName(lastName: String?) = apply { this.lastName = lastName
        return@apply}


        fun setGender(gender: Int?) = apply { this.gender = gender
        return@apply}
        fun setPassword(password: String) = apply { this.password = password
            return@apply
        }
        fun build(): StudentRegistrationData {
            return StudentRegistrationData(lrn,firstName,middleName,lastName,gender,password)
        }
    }
}

