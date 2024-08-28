package com.gokuldev.hostellock

import DatabaseHelper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.Manifest
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailsScreen : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var registerNumber: EditText
    private lateinit var date: EditText
    private lateinit var time: EditText
    private lateinit var purpose: EditText
    private lateinit var submit: Button
    private lateinit var imageSet: ImageView
    private var isImageSet = false
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_screen)
        window.statusBarColor = getColor(R.color.primary)

        name = findViewById(R.id.name_edt)
        registerNumber = findViewById(R.id.register_edt)
        date = findViewById(R.id.date_edt)
        time = findViewById(R.id.time_edt)
        purpose = findViewById(R.id.purpose_edt)
        submit = findViewById(R.id.sub_btn)
        imageSet = findViewById(R.id.open_camera)

        dbHelper = DatabaseHelper(this)

        // Set current date and time
        val currentDate = getCurrentDate()
        val currentTime = getCurrentTime()

        // Set the date and time in the EditText fields
        date.setText(currentDate)
        time.setText(currentTime)
        Toast.makeText(this,"Capture your image to register",Toast.LENGTH_LONG).show()

        if (intent.getBooleanExtra("SHOW_PURPOSE", false)) {
            // If the extra information is true, make the purposeEditText visible
            purpose.visibility = View.VISIBLE
        }
        submit.setOnClickListener {
            validation()
        }
        imageSet.setOnClickListener {
            if (!isImageSet) {
                checkCameraPermission()
            } else {
                enableEditing()
            }
        }

    }

    private fun enableEditing() {
        name.isEnabled = true
        registerNumber.isEnabled = true
        date.isEnabled = true
        time.isEnabled = true
        purpose.isEnabled = true
    }

    private fun validation(){
        when {
            name.text.isEmpty() -> {
                name.setError("Enter the name")
                name.requestFocus()
            }
            registerNumber.text.isEmpty() -> {
                registerNumber.setError("Enter the number")
                registerNumber.requestFocus()
            }
            purpose.visibility == View.VISIBLE && purpose.text.isEmpty() -> {
                purpose.setError("Enter the purpose")
                purpose.requestFocus()
            }
            else -> {
                if (intent.getBooleanExtra("SHOW_PURPOSE", false)) {
                    // If the extra information is true, store data in the Purpose table
                    dbHelper.addPurposeData(name.text.toString(), registerNumber.text.toString(), date.text.toString(), time.text.toString(), purpose.text.toString(), imageToByteArray(imageSet.drawable.toBitmap()))
                    Toast.makeText(this,"Data Stored in Purpose",Toast.LENGTH_SHORT).show()
                } else {
                    // Otherwise, store data in the Visitor table
                    dbHelper.addVisitorData(name.text.toString(), registerNumber.text.toString(), date.text.toString(), time.text.toString(), imageToByteArray(imageSet.drawable.toBitmap()))
                    Toast.makeText(this,"Data Stored in Visitors",Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this,SuccessScreen::class.java)
                startActivity(intent)
            }
        }
    }

    private fun imageToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                navigateToAppSettings()
            }
        }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun navigateToAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = android.net.Uri.parse("package:$packageName")
        startActivity(intent)
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageSet.setImageBitmap(circularBitmap(imageBitmap))
            isImageSet = true
            enableEditing()
        }
    }

    private fun circularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        val paint = android.graphics.Paint()
        val rect = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 2f, paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun getCurrentTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(Date())
    }

}
