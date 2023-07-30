package com.bryll.hams.views.enrollment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import com.bryll.hams.R
import com.bryll.hams.models.Enrollment
import com.bryll.hams.models.EnrollmentStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class EnrollmentAdapter(val context : Context, private val enrollments : List<Enrollment>,
                        private val enrollmentClickListener: EnrollmentClickListener) : RecyclerView.Adapter<EnrollmentAdapter.EnrollmentViewHolder>() {

    interface EnrollmentClickListener {
        fun onCancel(id : Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnrollmentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_enrollement,parent,false)
        return  EnrollmentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  enrollments.size
    }

    override fun onBindViewHolder(holder: EnrollmentViewHolder, position: Int) {
        val enrollment = enrollments[position]
        holder.textGradeLevel.text = String.format("Grade: %s",enrollment.grade_level)
        holder.textTrack.text = String.format("Track: %s \nStrand: %s",enrollment.track ?: "none" , enrollment.strand ?: "none")

        holder.textSchoolYear.text = String.format("SY: %s \nSemester: %s",enrollment.school_year ,enrollment.semester ?: "none")
        holder.textStatus.text = EnrollmentStatus.values()[enrollment.status].name
        holder.buttonCancel.visibility = if (enrollment.status == 0) View.VISIBLE else View.GONE
        holder.buttonCancel.setOnClickListener {
            enrollmentClickListener.onCancel(enrollment.id)
        }
        if (enrollment.grade_level > 6 && enrollment.semester == 1 && enrollment.status == 1) {
            holder.buttonUpdate.visibility = View.VISIBLE
        } else {
            holder.buttonUpdate.visibility = View.GONE
        }
    }

    class EnrollmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textGradeLevel : TextView = itemView.findViewById(R.id.textGradeLevel)
        val textTrack : TextView = itemView.findViewById(R.id.textTrack)
        val textSchoolYear : TextView = itemView.findViewById(R.id.textSchoolYear)
        val textStatus : TextView = itemView.findViewById(R.id.textStatus)
        val buttonCancel : MaterialButton = itemView.findViewById(R.id.buttonCancel)
        val buttonUpdate : MaterialButton = itemView.findViewById(R.id.buttonUpdate)
    }
}