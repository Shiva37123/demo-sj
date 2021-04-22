package com.example.demoprojectsj.Model

import android.text.TextUtils
import android.util.Patterns
import androidx.databinding.BaseObservable

class User(private var email: String, private var password: String): BaseObservable() {

    val isDataValid:Boolean
        get() = (!TextUtils.isEmpty(getEmail())) && Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches() && getPassword().length >6

    private fun getPassword(): String {
        return password

    }

    private fun getEmail(): String {

        return email

    }


    fun setEmail(email: String){
        this.email = email
    }

    fun setPassword(email: String){
        this.password = password
    }
}