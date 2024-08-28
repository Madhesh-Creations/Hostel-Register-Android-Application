package com.gokuldev.hostellock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StudentsInOutList : AppCompatActivity() {
    private lateinit var inLock: Button
    private lateinit var outLock: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_in_out_list)
        window.statusBarColor = getColor(R.color.primary)

        inLock = findViewById(R.id.inside_lock)
        outLock = findViewById(R.id.outside_lock)

        inLock.setOnClickListener {
            val intent = Intent(this,StudentsListScreen::class.java)
            startActivity(intent)
        }
        outLock.setOnClickListener {
            val intent = Intent(this,StudentsListScreen::class.java)
            intent.putExtra("SHOW_PURPOSE",true)
            startActivity(intent)
        }
    }
}