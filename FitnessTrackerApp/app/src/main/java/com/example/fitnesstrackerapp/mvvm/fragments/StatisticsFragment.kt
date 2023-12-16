package com.example.fitnesstrackerapp.mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fitnesstrackerapp.databinding.FragmentStatisticsBinding
import com.example.fitnesstrackerapp.mvvm.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val viewModel: StatisticsViewModel by viewModels()
    private var binding: FragmentStatisticsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        return binding!!.root
    }
}