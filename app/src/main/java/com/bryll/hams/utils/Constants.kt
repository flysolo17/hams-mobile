package com.bryll.hams.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.bryll.hams.BuildConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


const val SECRET_KEY = "TOKEN"

const val STORAGE_LINK = "http://${BuildConfig.API}/uploads/"
fun getImageTypeFromUri(context: Context, imageUri: Uri?): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri!!))
}


fun Activity.getFileName(uri: Uri): String? {
    var fileName: String? = null
    val cursor = contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (displayNameIndex != -1) {
                fileName = it.getString(displayNameIndex)
            }
        }
    }
    return fileName
}
fun getFileExtension(fileName: String?): String? {
    return fileName?.substringAfterLast(".", "")
}
fun Activity.createFile(uri: Uri) : File? {
    val contentResolver: ContentResolver = this.contentResolver
    val name = getFileName(uri)
    val extension = getFileExtension(name)
    if (name != null && extension != null) {
        val file = File(cacheDir, "$name")
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
    return null
}
fun Activity.getMimeType( uri: Uri): String? {
    return contentResolver.getType(uri)
}


 fun formatDate(year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, day)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

fun toDate(dateString: String): java.sql.Date? {
    return try {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = simpleDateFormat.parse(dateString)
        java.sql.Date(date!!.time)
    } catch (e: Exception) {
        null
    }
}

class Constants {


    companion object {
        const val STUDENT_TABLE ="Students"
        const val CLASSES_TABLE ="Classes"
        const val SUBJECTS_TABLE ="Subjects"
        const val USERS_TABLE ="Users"
        val SEM_LIST = listOf(1,2)

    }
}