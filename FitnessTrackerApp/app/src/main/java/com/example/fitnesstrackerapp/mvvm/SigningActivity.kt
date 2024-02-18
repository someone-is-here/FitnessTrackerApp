package com.example.fitnesstrackerapp.mvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnesstrackerapp.databinding.ActivitySigningBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SigningActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigningBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySigningBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}