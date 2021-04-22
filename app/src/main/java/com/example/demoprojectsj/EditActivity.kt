package com.example.demoprojectsj

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_edit.edit_password
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.edit_profile_pic
import kotlinx.android.synthetic.main.activity_register1.*
import kotlinx.android.synthetic.main.activity_register1.password
import kotlinx.android.synthetic.main.edit_password_popup.*
import kotlinx.android.synthetic.main.edit_password_popup.view.*
import kotlinx.android.synthetic.main.logout_popup.view.*
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException

class EditActivity : AppCompatActivity() {
    lateinit var dbHelper : DBHelper
    lateinit var sh: SharedPreferences
    private lateinit var email : String
    private lateinit var imageUri: Uri
    private var x: String? = null
    lateinit var tempImage: ByteArray
//    private val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
    private val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    private val phonePattern = "[1-9][0-9]{9}"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        sh = this?.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        dbHelper = DBHelper(this)
        email = sh.getString("email", null).toString()

        if (email != null) {
            val data = dbHelper.getData(email)
            data.moveToFirst()


            if (data != null) {
                edit_name.setText(data.getString(data.getColumnIndex("name")))



                edit_email.setText(data.getString(data.getColumnIndex("email")))
                edit_phone.setText(data.getString(data.getColumnIndex("phone")))


                val img = data.getString(data.getColumnIndex("image"))

                val fs = FileInputStream(img)
                val tempImage = ByteArray(fs.available())
                fs.read(tempImage)

                val bt = BitmapFactory.decodeByteArray(tempImage, 0, tempImage.size)
                edit_profile_pic.setImageBitmap(bt)

            }
        }

        edit_email.setOnClickListener {

            Toast.makeText(this,"Cannot edit email", Toast.LENGTH_LONG).show()

        }

        edit_photo_btn.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 1001)
        }

        edit_password.setOnClickListener {


            val editPassDialog = LayoutInflater.from(this).inflate(R.layout.edit_password_popup, null)
            val builder = AlertDialog.Builder(this).setView(editPassDialog)
            val alertDialog = builder.show()
            alertDialog.setCanceledOnTouchOutside(false)


            editPassDialog.edit_password_btn.setOnClickListener {

                val previous= editPassDialog.edit_previous_password.text.toString()
                val new = editPassDialog.edit_password.text.toString()
                val renew = editPassDialog.edit_repassword.text.toString()

                if(checkPassword(previous,new,renew)){
                    if (validatePassword(new,renew)){
                        dbHelper.changePassword(email,new)
                        Toast.makeText(this,"Password Changed",Toast.LENGTH_LONG).show()

                        alertDialog.cancel()
                    }
                    else{
                        Toast.makeText(this, "Password Should be of 8 length and contain digit special character and one letter", Toast.LENGTH_LONG).show()

                    }

                }

            }
            editPassDialog.edit_password_cancel.setOnClickListener {
                alertDialog.cancel()
            }
        }


        update_btn.setOnClickListener {
            if (validatePhone(edit_phone.text.toString())) {
                val check = dbHelper.UpdateUserData(
                        edit_name.text.toString(),
                        edit_email.text.toString(),
                        edit_phone.text.toString(),
                        x,
                );
                if (check) {
                    Toast.makeText(this, "Account Updated", Toast.LENGTH_LONG).show();
                    startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))

                }
            }
        }

    }

    private fun checkPassword(previous: String, new: String, renew: String): Boolean {

        val check = dbHelper.checkEmailPassword(email,previous)
        if(check)
        {           // if ()

            return true
        }
        else{
            Toast.makeText(this,"Incorrect password",Toast.LENGTH_LONG).show()
            return false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (data!=null) {
            imageUri = data.data!!
            x = imageUri?.let { getPath(it) }

            try {
                val fs = FileInputStream(x)
                tempImage = ByteArray(fs.available())
                fs.read(tempImage)
                Toast.makeText(this,x,Toast.LENGTH_LONG).show()

                val bt = BitmapFactory.decodeByteArray(tempImage,0,tempImage.size)
                edit_profile_pic.setImageBitmap(bt)
                val stream = ByteArrayOutputStream()
                bt.compress(Bitmap.CompressFormat.JPEG,80,stream)
                val byteArray = stream.toByteArray()
                val compressesBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)


            }catch (e: IOException){
                e.printStackTrace()
            }
        }

    }

    fun getPath(uri: Uri) : String?{
        if (uri==null)
            return null
        else
        {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = managedQuery(uri, projection, null, null, null)
            if (cursor!=null){
                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(column_index)
            }
            return uri.path
        }
    }

    fun validatePassword(new:String, rePassword:String):Boolean{
        if (TextUtils.isEmpty(new)){
            Toast.makeText(this, "Please Enter password", Toast.LENGTH_LONG).show()
            return false

        }
        else if(new.matches(passwordPattern.toRegex())){

        }
        else{
            Toast.makeText(this, "Password Should be of 8 length and must contain only  digit and letter", Toast.LENGTH_LONG).show()
            return false
        }
        if (TextUtils.isEmpty(rePassword)){
            Toast.makeText(this, "Please Enter again", Toast.LENGTH_LONG).show()
            return false

        }


        if (!TextUtils.equals(new, rePassword)){
            Toast.makeText(this, "Password Does Not Match", Toast.LENGTH_LONG).show()

            return false


        }
        return true

    }

    fun validatePhone(phone : String): Boolean{
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please Enter phone no.", Toast.LENGTH_LONG).show()
            return false
        }
        else if(phone.matches(phonePattern.toRegex())){

        }
        else{
            Toast.makeText(this, "Please Enter Valid phone no.", Toast.LENGTH_LONG).show()
            return false
        }


        return true

    }

}