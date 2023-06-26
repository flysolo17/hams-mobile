package com.bryll.hams.adapters

import android.content.Context
import android.graphics.Color
import android.provider.CalendarContract.Colors
import android.provider.SyncStateContract.Constants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bryll.hams.R
import com.bryll.hams.models.Academic
import com.bryll.hams.models.AcademicStatus
import com.bryll.hams.models.Classes
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class EnrollmentAdapter(val context : Context,val academics : List<Academic>) : RecyclerView.Adapter<EnrollmentAdapter.EnrollmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val  view : View = LayoutInflater.from(context).inflate(R.layout.row_enrollement,parent,false)
        return EnrollmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  academics.size
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        val academic : Academic = academics[position]
        val firestore = FirebaseFirestore.getInstance()
        academic.classID?.let {
            holder.displayClass(firestore, it)
        }
       holder.textStatus.text = academic.academicStatus?.name ?: "NA"
        holder.textStatus.setTextColor(if (academic.academicStatus == AcademicStatus.ACCEPTED || academic.academicStatus == AcademicStatus.PASSED) Color.GREEN else Color.RED)
    }
    class EnrollmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textClassname : TextView = itemView.findViewById(R.id.textClassTitle)
        val textStatus : TextView = itemView.findViewById(R.id.textStatus)
        val textEducationLevel : TextView = itemView.findViewById(R.id.textEducationLevel)
        val  textSchoolYear : TextView = itemView.findViewById(R.id.textSchoolYear)
        fun displayClass(firestore: FirebaseFirestore, classID : String) {
            firestore.collection(com.bryll.hams.utils.Constants.CLASSES_TABLE)
                .document(classID)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val data = it.toObject(Classes::class.java)
                        textClassname.text = data?.name ?: "Class Deleted"
                        textEducationLevel.text = data?.educationLevel ?: "NA"
                        textSchoolYear.text = data?.schoolYear ?: "NA"
                    }
                }
        }
    }


}