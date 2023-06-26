package com.bryll.hams.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bryll.hams.MainActivity
import com.bryll.hams.R
import com.bryll.hams.databinding.FragmentAcademicBinding


class AcademicFragment : Fragment() {

    private lateinit var binding : FragmentAcademicBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAcademicBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCheckUser.setOnClickListener {

        }
    }
}