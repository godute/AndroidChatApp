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
import com.example.androidchatapp.databinding.FragmentSignUpBinding
import com.example.androidchatapp.models.LoginViewModel
import com.example.androidchatapp.models.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private const val TAG = "LoginViewModel"

class SignUpFragment : Fragment() {
    private val viewModel: LoginViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth

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
        auth = Firebase.auth
    }

    fun cancel() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    fun signUp() {
        auth.createUserWithEmailAndPassword(binding.signupEmailText.text.toString(), binding.signupPasswordText.text.toString())
            .addOnCompleteListener(requireActivity()) {  task ->
                if(task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success ${task.result?.user?.uid}")

                    val uid = task.result?.user?.uid.toString()
                    val profileImg = ""
                    val name = binding.signupNameText.text.toString()
                    val employeeNo = binding.signupEmployeeNumberText.text.toString().toInt()

                    saveUserToFirebaseDatabase(uid, name, profileImg, employeeNo)

                    findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                }
                else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    private fun saveUserToFirebaseDatabase(uid: String, name: String, profileImg: String, employeeNo: Int ){
        val db = FirebaseFirestore.getInstance()
        val user = UserInfo(uid, name, profileImg, HashMap<String,String>() ,employeeNo)

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
    }
}