package com.example.androidchatapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.FragmentLoginBinding
import com.example.androidchatapp.models.LoginViewModel

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            loginViewModel = viewModel
            loginFragment = this@LoginFragment
        }
    }

    fun signIn() {
        Log.d("LoginViewModel", "Email: ${viewModel.email}, password: ${viewModel.password}")
    }

    fun signUp() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

    companion object {

    }
}