package com.gokuldev.hostellock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class HomeScreen : AppCompatActivity() {
    private lateinit var stdinImg: ImageView
    private lateinit var stdotImg: ImageView
    private lateinit var stdadImg: ImageView
    private lateinit var stdinTxt: TextView
    private lateinit var stdotTxt: TextView
    private lateinit var stdadTxt: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        window.statusBarColor = getColor(R.color.primary)

        stdinImg = findViewById(R.id.std_inside)
        stdotImg = findViewById(R.id.std_outside)
        stdadImg = findViewById(R.id.std_admin)
        stdinTxt = findViewById(R.id.std_txt)
        stdotTxt = findViewById(R.id.stdot_txt)
        stdadTxt = findViewById(R.id.std_adtxt)

        stdinImg.setOnClickListener {
            nextScreen()
        }
        stdinTxt.setOnClickListener {
            nextScreen()
        }
        stdotImg.setOnClickListener {
            val intent = Intent(this,DetailsScreen::class.java)
            intent.putExtra("SHOW_PURPOSE",true)
            startActivity(intent)
        }
        stdotTxt.setOnClickListener {
            val intent = Intent(this,DetailsScreen::class.java)
            intent.putExtra("SHOW_PURPOSE",true)
            startActivity(intent)
        }
        stdadImg.setOnClickListener {
            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
        }
        stdotTxt.setOnClickListener {
            val intent = Intent(this,LoginScreen::class.java)
            startActivity(intent)
        }
    }

    private fun nextScreen(){
        val intent = Intent(this,DetailsScreen::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}