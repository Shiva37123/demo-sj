package com.example.demoprojectsj

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.logout_popup.view.*
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    private lateinit var email : String
    private var isLogin : Boolean = false
    lateinit var sh: SharedPreferences
    lateinit var userName : String
    private var pressedTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        sh = this?.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

        if (sh != null) {
            isLogin = sh.getBoolean("isLogin", false)
        }

        Log.e("isLogin", isLogin.toString())

        if (!isLogin){
            startActivity(Intent(this, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }

        else {

            dbHelper = DBHelper(this)
            email = sh.getString("email", null).toString()

            if (email != null) {
                val data = dbHelper.getData(email)
                data.moveToFirst()


                if (data != null) {
                    userName = data.getString(data.getColumnIndex("name"))
                    Name.text = userName

                    Email.text = data.getString(data.getColumnIndex("email"))
                    Phone.text = data.getString(data.getColumnIndex("phone"))

                    //showNotification()

                    val img = data.getString(data.getColumnIndex("image"))

                    val fs = FileInputStream(img)
                    val tempImage = ByteArray(fs.available())
                    fs.read(tempImage)
//                    Toast.makeText(this, img.toString(), Toast.LENGTH_LONG).show()

                    val bt = BitmapFactory.decodeByteArray(tempImage, 0, tempImage.size)
                    edit_profile_pic.setImageBitmap(bt)

//                val bt = BitmapFactory.decodeByteArray(img, 0, img.size)
//                profile_pic.setImageBitmap(bt)
                    //profile_pic.setImageURI(Uri.parse(img))
                }
            }

            //startActivity(Intent(this,LoginActivity::class.java))
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.edit -> {
                edit(email)
                true
            }
            R.id.delete -> {
                delete(email)
                true
            }
            R.id.logout -> {
                val logoutDialog = LayoutInflater.from(this).inflate(R.layout.logout_popup, null)
                val builder = AlertDialog.Builder(this).setView(logoutDialog)
                val alertDialog = builder.show()
                alertDialog.setCanceledOnTouchOutside(false)

                logoutDialog.logout_popup_button.setOnClickListener {
                    alertDialog.cancel()
                    logout()
                }
                logoutDialog.cancel_popup_button.setOnClickListener {
                    alertDialog.cancel()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)

        }



    }

    fun showNotification(){
        val mBuilder = NotificationCompat.Builder(this)
        mBuilder.setSmallIcon(R.drawable.user)
        mBuilder.setContentTitle(" Logged in Successful")
        mBuilder.setContentText("Welcome " + userName)



        val notifcationIntent = Intent(this, RegisterActivity1::class.java)
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

    private fun logout() {

        val editor: SharedPreferences.Editor? =  sh?.edit()
        editor?.putBoolean("isLogin", false)
        editor?.putString("email", null)
        editor?.apply()
        editor?.commit()

        isLogin = sh.getBoolean("isLogin", false)
        if (!isLogin){
            startActivity(Intent(this, LoginActivity::class.java))
        }
        Toast.makeText(this, "Log Out Successfully", Toast.LENGTH_LONG).show()
    }

    private fun delete(email: String) {

        val deleted = dbHelper.deleteUserData(email)
        if (deleted){
            logout()
        }
        else{
            Toast.makeText(this, "Delete Failure", Toast.LENGTH_LONG).show()

        }

    }

    private fun edit(email: String) {


        startActivity(Intent(this, EditActivity::class.java))
//        Toast.makeText(this, "edit" + email, Toast.LENGTH_LONG).show()



    }

}




//Code for loading image in imageview using image uri
/*var uri = intent.getStringExtra("image_uri")

      Toast.makeText(this,uri,Toast.LENGTH_LONG).show()
      profile_pic.setImageURI(null)
      profile_pic.setImageURI(Uri.parse(uri))*/