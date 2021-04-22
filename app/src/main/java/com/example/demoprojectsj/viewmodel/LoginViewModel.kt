package com.example.demoprojectsj.viewmodel

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.demoprojectsj.Interface.LoginResultCallBack
import com.example.demoprojectsj.Model.User

class LoginViewModel(private val listener: LoginResultCallBack): ViewModel() {

    private val user : User

    init {
        user = User("","")
    }

    val emailTextWatcher: TextWatcher

        get() = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            user.setEmail(s.toString() )
        }

    }

    val passwordTextWatcher: TextWatcher

        get() = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                user.setPassword(s.toString() )
            }

        }

    fun onLoginClicked(v: View){
        if (user.isDataValid){
            listener.onSuccess("Login Success")
        }
        else{
            listener.onError("Login Error")
        }
    }

}