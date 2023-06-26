package com.bryll.hams.views.enrollment

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bryll.hams.R

import com.bryll.hams.models.Curriculum
import com.bryll.hams.models.Subjects
import com.bryll.hams.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class EnrollmentSubjectAdapter(val context : Context, private val curriculums: List<Curriculum>) : RecyclerView.Adapter<EnrollmentSubjectAdapter.EnrollmentSubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentSubjectViewHolder {
        val view : View =  LayoutInflater.from(context).inflate(R.layout.row_sbjects_1,parent,false)
        return EnrollmentSubjectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return curriculums.size
    }

    override fun onBindViewHolder(holder: EnrollmentSubjectViewHolder, position: Int) {
        val  firestore = FirebaseFirestore.getInstance()
        val curriculum = curriculums[position]
        holder.displaySubject(subjectID = curriculum.subjectID ?: "",firestore)
    }
    fun getAllCurriculums() : List<Curriculum> {
        return curriculums
    }
    class EnrollmentSubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textSubjectTitle : TextView = itemView.findViewById(R.id.textSubjectTitle)
        private val textSubjectUnit : TextView = itemView.findViewById(R.id.textSubjectUnits)
        private val textSubjectTeacher : TextView= itemView.findViewById(R.id.textSubjectTeacher)
        fun displaySubject(subjectID : String,firestore: FirebaseFirestore) {
            textSubjectTitle.text = "Loading..."
            textSubjectTeacher.text = "Loading..."
            textSubjectUnit.text ="Loading..."
            firestore.collection(Constants.SUBJECTS_TABLE)
                .document(subjectID)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val data = it.toObject(Subjects::class.java)
                        data?.let {
                            textSubjectTitle.text = data.name
                            textSubjectTeacher.text = data.teacher
                            textSubjectUnit.text ="  ${data.units} Units"
                        }
                    } else {
                        textSubjectTitle.text = "No Subject found..."
                        textSubjectTeacher.text = ""
                        textSubjectUnit.text =""
                    }
                }
        }
    }

}