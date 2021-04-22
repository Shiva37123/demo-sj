package com.example.demoprojectsj

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_register1.*
import kotlinx.android.synthetic.main.activity_register1.email
import kotlinx.android.synthetic.main.activity_register1.password
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException


class RegisterActivity1 : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var imageUri: Uri
    private var x: String? = null
    lateinit var tempImage: ByteArray
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
//    private val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
    private val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"

    private val phonePattern = "[1-9][0-9]{9}"





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register1)
//        startActivity(Intent(this, MainActivity::class.java))

        dbHelper= DBHelper(this)



        add_photo_btn.setOnClickListener {
            verifyStoragePermissions(this)

            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, 1001)


        }

        already.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        submitbtn.setOnClickListener {



            if(validate()){
                val check = dbHelper.InsertUserData(
                        name.text.toString(),
                        email.text.toString(),
                        phone.text.toString(),
                        x, password.text.toString()
                );
                if (check){
                    startActivity(Intent(this, LoginActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    Toast.makeText(this, "Account Created", Toast.LENGTH_LONG).show();
                }
                else {
                    if(dbHelper.checkEmail(email.text.toString())){
                        Toast.makeText(this, "Email already exists", Toast.LENGTH_LONG).show()
                    }
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }


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

//                val f = File(x)
//                if (f.exists()){
//                    val myBitmap = BitmapFactory.decodeFile(f.absolutePath)
//                    profile_pic.setImageBitmap(myBitmap)
//
//                }




            }catch (e: IOException){
                e.printStackTrace()
            }
        }

//        if (data!=null) {
//            val selectedImage: Uri? = data.data
//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//
//            val cursor: Cursor? = selectedImage?.let {
//                contentResolver.query(it,
//                        filePathColumn, null, null, null)
//            }
//            cursor!!.moveToFirst()
//
//            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
//            val picturePath: String = cursor.getString(columnIndex)
//            cursor.close()
//
//            val mBitmap = BitmapFactory.decodeFile(picturePath)
//            profile_pic.setImageBitmap(mBitmap)
//
//            val stream = ByteArrayOutputStream()
//            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            val imageInByte: ByteArray = stream.toByteArray()
//            tempImage = imageInByte
//
//
//        }
//        else{
//            Toast.makeText(this, "Please Select A image", Toast.LENGTH_LONG).show()
//        }
    }


    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
            )
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

        fun validate(): Boolean {
            val emailID = email.text.toString().trim()
            val passwordGet = password.text.toString().trim()
            val phoneNo = phone.text.toString().trim()

            if (TextUtils.isEmpty(name.text)){
                Toast.makeText(this, "Please Enter name", Toast.LENGTH_LONG).show()
                return false

            }
            if (TextUtils.isEmpty(email.text)){
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_LONG).show()
                return false
            }


            else if(emailID.matches(emailPattern.toRegex())){

            }
            else{
                Toast.makeText(this, "Please Enter Valid Email", Toast.LENGTH_LONG).show()
                return false
            }
            if (TextUtils.isEmpty(phone.text)){
                Toast.makeText(this, "Please Enter phone no.", Toast.LENGTH_LONG).show()
                return false
            }
            else if(phoneNo.matches(phonePattern.toRegex())){

            }
            else{
                Toast.makeText(this, "Please Enter Valid phone no.", Toast.LENGTH_LONG).show()
                return false
            }


            if (TextUtils.isEmpty(password.text)){
                Toast.makeText(this, "Please Enter password", Toast.LENGTH_LONG).show()
                return false

            }
            else if(passwordGet.matches(passwordPattern.toRegex())){

            }
            else{
                Toast.makeText(this, "Password Should be of 8 length and contain digit special character and one letter", Toast.LENGTH_LONG).show()
                return false
            }

            if (TextUtils.isEmpty(repassword.text)){
                Toast.makeText(this, "Please Enter again", Toast.LENGTH_LONG).show()
                return false

            }

            //if (phone.text?.length!! < 10 or phone.text!!.length>10)

            if (!TextUtils.equals(password.text, repassword.text)){
                Toast.makeText(this, "Password Does Not Match", Toast.LENGTH_LONG).show()

                return false


            }

            if (imageUri==null){
                Toast.makeText(this, "Please select Image", Toast.LENGTH_LONG).show()
                return false
            }


            return true
        }

}