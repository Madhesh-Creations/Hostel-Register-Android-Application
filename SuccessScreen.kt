package com.gokuldev.hostellock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SuccessScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_screen)
        window.statusBarColor = getColor(R.color.primary)
        Handler().postDelayed({
            val intent = Intent(this,HomeScreen::class.java)
            startActivity(intent)
        },2000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,HomeScreen::class.java)
        startActivity(intent)
    }
}