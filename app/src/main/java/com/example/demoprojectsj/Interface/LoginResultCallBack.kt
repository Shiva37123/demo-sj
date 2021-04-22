package com.example.demoprojectsj.Interface

interface LoginResultCallBack {
    fun onSuccess(message: String)
    fun onError(message: String)
}