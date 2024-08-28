package com.gokuldev.hostellock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class LoginScreen : AppCompatActivity() {
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var start: Button
    private lateinit var eyecon : ImageView
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)
        window.statusBarColor = getColor(R.color.primary)

        userName = findViewById(R.id.user_edt)
        password = findViewById(R.id.pwd_edt)
        start = findViewById(R.id.strt_btn)
        eyecon = findViewById(R.id.close_eye)

        start.setOnClickListener {
            validation()
        }
        updateEyeIcon()
        setupEyeIconClick()
    }

    private fun validation(){
        var user = "admin"
        var pwd = "praTHap"
        when {
            userName.text.isEmpty() -> {
                userName.error = "Enter User Name"
                userName.requestFocus()
            }
            password.text.isEmpty() -> {
                password.error = "Enter Password"
                password.requestFocus()
            }
            !userName.text.matches(user.toRegex()) -> {
                Toast.makeText(this,"User Name Invalid",Toast.LENGTH_SHORT).show()
            }
            !password.text.matches(pwd.toRegex()) -> {
                Toast.makeText(this,"Password is not valid",Toast.LENGTH_SHORT).show()
            }
            else -> {
                val intent = Intent(this,StudentsInOutList::class.java)
                startActivity(intent)
                userName.text.clear()
                password.text.clear()
            }
        }
    }
    private fun updateEyeIcon() {
        if (isPasswordVisible) {
            eyecon.setImageResource(R.drawable.eye_open) // Change to your closed eye icon
            password.transformationMethod = null
        } else {
            eyecon.setImageResource(R.drawable.eye_close) // Change to your open eye icon
            password.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    private fun setupEyeIconClick() {
        eyecon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            updateEyeIcon()
            password.setSelection(password.text.length)
        }
    }

}