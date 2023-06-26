package com.bryll.hams.services.enrollment

import com.bryll.hams.models.Academic
import com.bryll.hams.models.Classes
import com.bryll.hams.models.Subjects
import com.bryll.hams.utils.Constants
import com.bryll.hams.utils.UiState
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EnrollmentServiceImpl(private val firestore: FirebaseFirestore) : EnrollmentService {
    override fun getAllClass(level : String ,result: (UiState<List<Classes>>) -> Unit) {
        result.invoke(UiState.onLoading)
        firestore.collection(Constants.CLASSES_TABLE)
                .whereEqualTo("educationLevel",level)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.invoke(UiState.onSuccess(it.result.toObjects(Classes::class.java)))
                    } else {
                        result.invoke(UiState.onFailed("Failed getting classes!"))
                    }
                }.addOnFailureListener {
                    result.invoke(UiState.onFailed(it.message!!))
                }
    }

    override fun getAllSubjects(result: (UiState<List<Subjects>>) -> Unit) {
        result.invoke(UiState.onLoading)
            firestore.collection(Constants.SUBJECTS_TABLE)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        result.invoke(UiState.onSuccess(it.result.toObjects(Subjects::class.java)))
                    } else {
                        result.invoke(UiState.onFailed("Failed getting subjects!"))
                    }
                }.addOnFailureListener {
                    result.invoke(UiState.onFailed(it.message!!))
                }
    }

    override fun submitEnrollment(studentID : String ,academic: Academic, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.onLoading)
        firestore.collection(Constants.STUDENT_TABLE)
            .document(studentID)
            .update("academics",FieldValue.arrayUnion(academic))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.onSuccess("new application sent"))
                } else {
                    result.invoke(UiState.onFailed("failed to send an application"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.onFailed(it.message!!))
            }
    }
}