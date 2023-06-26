package com.bryll.hams.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bryll.hams.R
import com.bryll.hams.models.Grade
import com.bryll.hams.models.Subjects
import com.google.firebase.firestore.FirebaseFirestore

class SubjectAdapter(val context: Context, private val subjects: List<Grade>) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.row_subjects,parent,false)
        return SubjectViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val grade = subjects[position]
        val firestore = FirebaseFirestore.getInstance()
        grade.subjectID?.let {
            holder.displaySubject(firestore,it)
        }

    }
    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textSubjectName : TextView = itemView.findViewById(R.id.textSubjectName)
        private val textSubjectTeacher : TextView = itemView.findViewById(R.id.textSubjectTeacher)
        fun displaySubject(firestore: FirebaseFirestore, subjectID : String) {
            firestore.collection(com.bryll.hams.utils.Constants.SUBJECTS_TABLE)
                .document(subjectID)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val data = it.toObject(Subjects::class.java)
                        textSubjectName.text = data?.name ?: "Subject Deleted"
                        textSubjectTeacher.text = data?.teacher ?: "NA"
                    }
                }
        }
    }


}