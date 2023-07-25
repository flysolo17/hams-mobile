package com.bryll.hams.utils
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.*
import java.lang.reflect.Type

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return try {
            json?.let {
                if (it.isJsonObject) {
                    val jsonObject = it.asJsonObject
                    val createdAtString = jsonObject.get("created_at").asString
                    LocalDateTime.parse(createdAtString, formatter)
                } else {
                    LocalDateTime.parse(it.asString, formatter)
                }
            } ?: LocalDateTime.MIN
        } catch (e: Exception) {
            LocalDateTime.MIN
        }
    }
}

