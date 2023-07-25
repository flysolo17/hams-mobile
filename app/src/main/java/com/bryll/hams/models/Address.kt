package com.bryll.hams.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Address(
    @SerializedName("id") val id: Int?,
    @SerializedName("house_no") val house_no: Int?,
    @SerializedName("street") val street: String?,
    @SerializedName("barangay") val barangay: String?,
    @SerializedName("municipality") val municipality: String?,
    @SerializedName("province") val province: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("zip_code") val zip_code: String?,
    @SerializedName("type") val type: Int?

) : Parcelable {
    override fun toString(): String {
        return buildString {
            append("Address ID: $id\n")
            append("Type: ${AddressType.values()[type ?: 0]}\n")
            append("Full Address: ${getFormattedAddress()}\n")
            appendLine("Zip Code: ${zip_code ?: "N/A"}")
            appendLine("Country: ${country ?: "N/A"}")
        }
    }

    fun getFormattedAddress(): String {
        val formattedAddress = buildString {
            append(house_no ?: "N/A")
            street?.let { append(" $it") }
            barangay?.let { append(" $it") }
            municipality?.let { append(", $it") }
            province?.let { append(", $it") }
        }
        return formattedAddress.ifBlank { "N/A" }
    }
}
