package com.bryll.hams.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bryll.hams.databinding.FragmentDashboardBinding
import com.bryll.hams.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth


class DashboardFragment : Fragment() {

    private lateinit var binding : FragmentDashboardBinding
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater,container,false)
        loadingDialog = LoadingDialog(binding.root.context)
        binding.recyclerviewSubjects.layoutManager = LinearLayoutManager(binding.root.context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }



}