package com.example.androidchatapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidchatapp.R
import com.example.androidchatapp.databinding.FragmentSignUpBinding
import com.example.androidchatapp.models.SignUpInfo
import com.example.androidchatapp.services.FirebaseAuthService
import com.example.androidchatapp.services.FirebaseAuthSignUpListener

private const val TAG = "LoginViewModel"

class SignUpFragment : Fragment(), FirebaseAuthSignUpListener {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            signUpFragment = this@SignUpFragment
        }
    }

    fun cancel() {
        Log.d(TAG, "cancel() Called")
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    fun signUp() {
        Log.d(TAG, "signUp() Called")
        val signupInfo = SignUpInfo(
            binding.signupEmailText.text.toString(),
            binding.signupPasswordText.text.toString(),
            binding.signupNameText.text.toString(),
            binding.signupEmployeeNumberText.text.toString().toInt()
        )
        FirebaseAuthService.signUp(signupInfo)
    }

    override fun onSignUpComplete(signupResult: Boolean) {
        Log.d(TAG, "onSignUpComplete() Called")
        if(signupResult) {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }
}