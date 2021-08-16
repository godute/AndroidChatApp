package com.example.androidchatapp.services

import android.util.Log
import com.example.androidchatapp.models.SignUpInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private const val TAG = "FirebaseAuthService"


object FirebaseAuthService {
    private var auth = Firebase.auth
    private lateinit var _listener: FirebaseAuthInterface

    fun setOnSignupListener(listener: FirebaseAuthInterface) {
        _listener = listener
    }

    fun signUp(signUpInfo: SignUpInfo) {
        auth.createUserWithEmailAndPassword(
            signUpInfo.email,
            signUpInfo.password )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success ${task.result?.user?.uid}")

                    val uid = task.result?.user?.uid.toString()
                    val name = signUpInfo.userName
                    val employeeNo = signUpInfo.employeeNumber

                    saveUserToFirebaseDatabase(uid, name, employeeNo)

                    _listener.onSignUpComplete(true)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _listener.onSignUpComplete(false)
                }
            }
    }

    private fun saveUserToFirebaseDatabase(uid: String, name: String, employeeNo: Int ){
        val db = FirebaseFirestore.getInstance()
        val user = com.example.androidchatapp.models.UserInfo(
            uid,
            name,
            "",
            HashMap<String, String>(),
            employeeNo
        )

        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")
            }
    }
}