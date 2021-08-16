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
import com.example.androidchatapp.services.FirebaseAuthService
import com.example.androidchatapp.services.FirebaseAuthSignInListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "LoginFragment"
class LoginFragment : Fragment(), FirebaseAuthSignInListener {
    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

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
        auth = Firebase.auth

        FirebaseAuthService.setOnSignInListener(this)

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

    override fun onResume() {
        super.onResume()
        viewModel.init()
    }

    fun signIn() {
        Log.d("LoginViewModel", "Email: ${binding.loginEmailText.text.toString()}, password: ${binding.loginPasswordText.text.toString()}")
        FirebaseAuthService.signIn(binding.loginEmailText.text.toString(), binding.loginPasswordText.text.toString())
    }

    fun signUp() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }

    override fun onSignInComplete(signInResult: Boolean) {
        if(signInResult) {
            findNavController().navigate(R.id.action_loginFragment_to_tabActivity)
        }
    }
}