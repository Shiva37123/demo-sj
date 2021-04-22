package com.example.demoprojectsj

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.email
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_register1.*

class LoginActivity : AppCompatActivity() {

    lateinit var dbHelper:DBHelper
    lateinit var emailID : String
    private var pressedTime: Long = 0
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
//    private val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
private val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"


    val viewModel: FormValidationViewModel by lazy {
        ViewModelProviders.of(this).get(FormValidationViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)

        create_text.setOnClickListener {
            startActivity(Intent(this, RegisterActivity1::class.java))

        }


        login.setOnClickListener {
            if (validate() && checkCredential()) {
                emailID = email.text.toString()
                val sh: SharedPreferences? = this?.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor? =  sh?.edit()
                editor?.putBoolean("isLogin", true)
                editor?.putString("email", emailID)
                editor?.apply()
                editor?.commit()

                val userName: String? = dbHelper.getUserName(emailID)
                if (userName != null) {
                    showNotification(emailID,userName)
                    startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))

                }

            }
            else{

            }
        }




    }

    override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        pressedTime = System.currentTimeMillis()
    }

    fun showNotification(email: String,userName:String){
        val mBuilder = NotificationCompat.Builder(this)
        mBuilder.setSmallIcon(R.drawable.user)
        mBuilder.setContentTitle(" Logged in Successful")
        mBuilder.setContentText("Welcome " + userName)



        val notifcationIntent = Intent(this, MainActivity::class.java)
        val contentIntent = PendingIntent.getActivity(this, 0, notifcationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(contentIntent)

        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH)
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }
        mNotificationManager.notify(1, mBuilder.build())
        Toast.makeText(this, email, Toast.LENGTH_LONG).show()

    }


    fun validate(): Boolean {

        val emailID = email.text.toString().trim()
        val passwordGet = password.text.toString().trim()

        if (TextUtils.isEmpty(email.text)){
            Toast.makeText(this,"Please Enter Email", Toast.LENGTH_LONG).show()

            return false
        }
        else if(emailID.matches(emailPattern.toRegex())){

        }
        else{
            Toast.makeText(this, "Please Enter Valid Email", Toast.LENGTH_LONG).show()
            return false
        }

        if (TextUtils.isEmpty(password.text)){
            Toast.makeText(this,"Please Enter Password", Toast.LENGTH_LONG).show()

            return false
        }
        else if(passwordGet.matches(passwordPattern.toRegex())){

        }
        else{
            Toast.makeText(this, "Password Should be of 8 length and contain digit special character and one letter", Toast.LENGTH_LONG).show()
            return false
        }


        return true
    }

    fun checkCredential(): Boolean{
        val check = dbHelper.checkEmailPassword(email.text.toString(),password.text.toString())
        if(check) {
            return true
        }
        if (!dbHelper.checkEmail(email.text.toString())){
            Toast.makeText(this,"Email does not exist please sign up",Toast.LENGTH_LONG).show()
            return false
        }
        else{
           Toast.makeText(this,"Password Is Incorrect.... Please Check", Toast.LENGTH_LONG).show()
            return false
        }




    }

}



class FormValidationViewModel: ViewModel() {
    val emailAddress = MutableLiveData<String>("")
}