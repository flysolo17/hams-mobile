package com.bryll.hams.data.response

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


@Serializable
data class ResponseData(
    val success : Boolean ? = null,
    val message : String ? = null,
    val error : String ? = null,
    @Contextual
    val data: Any ? = null,
    )
