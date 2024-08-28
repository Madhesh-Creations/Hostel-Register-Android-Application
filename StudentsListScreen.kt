package com.gokuldev.hostellock

import DatabaseHelper
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import java.io.File

class StudentsListScreen : AppCompatActivity() {
    private lateinit var gmail: Button
    private lateinit var stdList: ListView
    private lateinit var dataTxt: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var dataNo: TextView
    private val REQUEST_STORAGE_PERMISSION = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_list_screen)
        window.statusBarColor = getColor(R.color.primary)

        gmail = findViewById(R.id.genmail_exc)
        stdList = findViewById(R.id.std_listView)
        dataTxt = findViewById(R.id.in_lst)
        dataNo = findViewById(R.id.no_data)

        dbHelper = DatabaseHelper(this)

        // Check if extra data is present in the intent
        if (intent.getBooleanExtra("SHOW_PURPOSE", false)) {
            // If extra data is true, fetch and display purpose data
            dataTxt.text = "Outside Lock"
            val purposeDataList = dbHelper.getAllPurposeData()
            if (purposeDataList.isEmpty()){
                dataNo.isVisible = true
                gmail.isVisible = false
            }else{
                val adapter = CustomAdapter(this, purposeDataList)
                stdList.adapter = adapter
            }

        } else {
            // If extra data is false or not present, fetch and display visitor data
            val visitorDataList = dbHelper.getAllVisitorData()
            if (visitorDataList.isEmpty()){
                dataNo.isVisible = true
                gmail.isVisible = false
            }else{
                val adapter = CustomAdapter(this, visitorDataList)
                stdList.adapter = adapter
            }

        }

        // Check if the app has permission to write to external storage
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        } else {
            // Permission has already been granted
            // Call your exportDataToExcel or related functions here
            // ...
        }

        gmail.setOnClickListener {
            val showPurpose = intent.getBooleanExtra("SHOW_PURPOSE", false)

            // Create an instance of DatabaseHelper
            val databaseHelper = DatabaseHelper(this)

            // Call the method on the instance
            databaseHelper.exportDataToExcel(this, intent, showPurpose)
            databaseHelper.sendExcelFileViaEmail(this,showPurpose)
            dbHelper.clearAllData()
        }

    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, call your exportDataToExcel or related functions here
                    // ...
                } else {
                    // Permission denied
                    Toast.makeText(
                        this,
                        "Storage permission is required to save Excel file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
}