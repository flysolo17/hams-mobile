package com.bryll.hams.services

import com.bryll.hams.models.Student
import com.bryll.hams.utils.Constants
import com.bryll.hams.utils.UiState
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthServiceImpl(private val firebaseAuth: FirebaseAuth, private val firestore : FirebaseFirestore) : AuthService {
    var student : Student? = null
    override fun signup(email: String,password: String,student: Student, result: (UiState<Student>) -> Unit) {
        result.invoke(UiState.onLoading)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.onSuccess(student))
                } else {
                    result.invoke(UiState.onFailed("Failed creating account"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.onFailed(it.message!!))
            }
    }

    override fun login(email: String, password: String, result: (UiState<FirebaseUser>) -> Unit) {
        result.invoke(UiState.onLoading)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                result.invoke(UiState.onSuccess(it.result.user!!))
            } else {
                result.invoke(UiState.onFailed("Failed"))
            }
        }.addOnFailureListener {
            result.invoke(UiState.onFailed(it.message!!))
        }


    }

    override fun findStudentByID(id: String, result: (UiState<Student>) -> Unit) {
        result.invoke(UiState.onLoading)
        if (student == null) {
            firestore.collection(Constants.STUDENT_TABLE)
                .document(id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val student : Student? = it.toObject(Student::class.java)
                        this.student = student
                        this.student?.let { students ->
                            result.invoke(UiState.onSuccess(students))
                        }
                    } else {
                        result.invoke(UiState.onFailed("No user!"))
                    }
                }.addOnFailureListener {
                    result.invoke(UiState.onFailed(it.message!!))
                }
        } else {
            student?.let {
                result.invoke(UiState.onSuccess(it))
            }
        }

    }

    override fun saveStudent(student: Student, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.onLoading)
        student.id?.let {
            firestore.collection(Constants.STUDENT_TABLE)
                .document(it)
                .set(student)
                .addOnCompleteListener {task->
                    if (task.isSuccessful) {
                        result.invoke(UiState.onSuccess("New Student created!"))
                    } else {
                        result.invoke(UiState.onFailed("Failed: saving student info"))
                    }
                }.addOnFailureListener {err->
                    result.invoke(UiState.onFailed(err.message!!))
                }
        }

    }

    override fun getUserByEmail(email: String, result: (UiState<Student>) -> Unit) {
        result.invoke(UiState.onLoading)
        if (student == null) {
            firestore.collection(Constants.STUDENT_TABLE)
                .whereEqualTo("email",email)
                .limit(1)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val data = it.result.toObjects(Student::class.java)
                        if (data.size != 0) {
                            result.invoke(UiState.onSuccess(data[0]))
                        } else {
                            result.invoke(UiState.onFailed("No user found!"))
                        }
                    } else {
                        result.invoke(UiState.onFailed("No user found!"))
                    }
                }.addOnFailureListener {
                    result.invoke(UiState.onFailed(it.message!!))
                }
        }
    }

    override fun reAuthenticateAccount(
        user : FirebaseUser,
        email: String,
        password: String,
        result: (UiState<FirebaseUser>) -> Unit
    ) {
        result.invoke(UiState.onLoading)
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.onSuccess(user))
                } else  {
                    result.invoke(UiState.onFailed("Wrong Password!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.onFailed(it.message!!))
            }
    }

    override fun resetPassword(
        email: String,
        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.onLoading)
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                result.invoke(UiState.onSuccess("We sent a password reset link to your email!"))
            } else {
                result.invoke(UiState.onFailed("Failed to send password reset link to $email"))
            }
        }.addOnFailureListener {
            result.invoke(UiState.onFailed(it.message!!))
        }
    }

    override fun changePassword(
        user: FirebaseUser,
        password: String,
        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.onLoading)
        user.updatePassword(password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.onSuccess("Password changed successfully"))
                } else  {
                    result.invoke(UiState.onFailed("Wrong Password!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.onFailed(it.message!!))
            }
    }


    override fun updateAccount(student: Student, result: (UiState<String>) -> Unit) {
        TODO("Not yet implemented")
    }
}