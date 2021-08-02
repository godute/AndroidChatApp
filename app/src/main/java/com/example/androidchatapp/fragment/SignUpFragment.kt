package com.example.androidchatapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.FragmentSignUpBinding
import com.example.androidchatapp.models.LoginViewModel

class SignUpFragment : Fragment() {
    private val viewModel: LoginViewModel by activityViewModels()

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            signUpViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
            signUpFragment = this@SignUpFragment
        }
    }

    fun signUp() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    fun cancel() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }


}