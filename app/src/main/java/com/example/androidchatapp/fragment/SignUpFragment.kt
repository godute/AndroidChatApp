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
import com.example.androidchatapp.models.SignUpInfo
import com.example.androidchatapp.services.FirebaseAuthSignUpListener
import com.example.androidchatapp.services.FirebaseAuthService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "LoginViewModel"

class SignUpFragment : Fragment(), FirebaseAuthSignUpListener {
    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        FirebaseAuthService.setOnSignupListener(this)
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
        auth = Firebase.auth
    }

    fun cancel() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    fun signUp() {
        val signupInfo:SignUpInfo = SignUpInfo(
            binding.signupEmailText.text.toString(),
            binding.signupPasswordText.text.toString(),
            binding.signupNameText.text.toString(),
            binding.signupEmployeeNumberText.text.toString().toInt()
        )
        FirebaseAuthService.signUp(signupInfo)
    }

    override fun onSignUpComplete(signupResult: Boolean) {
        if(signupResult) {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }
}