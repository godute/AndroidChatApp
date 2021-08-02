package com.example.androidchatapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment

class LoginViewModel : ViewModel() {
    private lateinit var _navHostFragment: NavHostFragment

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _passwordConfirm = MutableLiveData<String>()
    val passwordConfirm: LiveData<String> = _passwordConfirm

    private val _employeeNumber = MutableLiveData<Int>()
    val employeeNumber: LiveData<Int> = _employeeNumber

    fun cancel() {

    }

    fun init() {
        _email.value = ""
        _password.value = ""
        _passwordConfirm.value = ""
        _employeeNumber.value = 0
    }


}