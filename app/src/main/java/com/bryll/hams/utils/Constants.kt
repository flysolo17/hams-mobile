package com.bryll.hams.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.bryll.hams.models.Curriculum
import com.bryll.hams.models.Grade
import com.bryll.hams.models.GradeStatus


fun getImageTypeFromUri(context: Context, imageUri: Uri?): String? {
    val contentResolver: ContentResolver = context.contentResolver
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri!!))
}

fun getSubjectBySem(curriculums : List<Curriculum> ,sem : Int) : List<Curriculum> {
    return  curriculums.filter { it.sem== sem }
}
fun generateGrades(curriculums : List<Curriculum>): MutableList<Grade> {
    val grades = mutableListOf<Grade>()
    curriculums.map {
        grades.add(Grade(it.subjectID,0.00,0.00,0.00,0.00,GradeStatus.ONGOING))
    }
    return grades;
}
class Constants {


    companion object {
        const val STUDENT_TABLE ="Students"
        const val CLASSES_TABLE ="Classes"
        const val SUBJECTS_TABLE ="Subjects"
        val SEM_LIST = listOf(1,2)

    }
}