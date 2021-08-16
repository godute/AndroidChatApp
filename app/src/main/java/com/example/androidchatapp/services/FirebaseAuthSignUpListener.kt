package com.example.androidchatapp.services

interface FirebaseAuthSignUpListener {
    fun onSignUpComplete(signupResult: Boolean)
}

interface FirebaseAuthSignInListener {
    fun onSignInComplete(signInResult: Boolean)
}