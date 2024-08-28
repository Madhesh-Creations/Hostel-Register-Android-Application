import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.gokuldev.hostellock.PurposeDataModel
import com.gokuldev.hostellock.VisitorDataModel
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Properties
import java.util.*

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "VisitorData.db"

        // Table names
        private const val TABLE_VISITOR = "visitor"
        private const val TABLE_PURPOSE = "purpose"

        // Common column names
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_REGISTER_NUMBER = "register_number"
        private const val KEY_DATE = "date"
        private const val KEY_TIME = "time"
        private const val KEY_CAPTURED_IMAGE = "captured_image"

        // Purpose table specific column names
        private const val KEY_PURPOSE = "purpose"

        // Create table statements
        private const val CREATE_TABLE_VISITOR =
            ("CREATE TABLE $TABLE_VISITOR($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_NAME TEXT, $KEY_REGISTER_NUMBER TEXT, $KEY_DATE TEXT, $KEY_TIME TEXT, $KEY_CAPTURED_IMAGE BLOB)")

        private const val CREATE_TABLE_PURPOSE =
            ("CREATE TABLE $TABLE_PURPOSE($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_NAME TEXT, $KEY_REGISTER_NUMBER TEXT, $KEY_DATE TEXT, $KEY_TIME TEXT, $KEY_PURPOSE TEXT, $KEY_CAPTURED_IMAGE BLOB)")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_VISITOR)
        db?.execSQL(CREATE_TABLE_PURPOSE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_VISITOR")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PURPOSE")
        onCreate(db)
    }

    fun addVisitorData(name: String, registerNumber: String, date: String, time: String, capturedImage: ByteArray) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_REGISTER_NUMBER, registerNumber)
        values.put(KEY_DATE, date)
        values.put(KEY_TIME, time)
        values.put(KEY_CAPTURED_IMAGE, capturedImage)
        db.insert(TABLE_VISITOR, null, values)
        db.close()
    }

    fun addPurposeData(name: String, registerNumber: String, date: String, time: String, purpose: String, capturedImage: ByteArray) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_NAME, name)
        values.put(KEY_REGISTER_NUMBER, registerNumber)
        values.put(KEY_DATE, date)
        values.put(KEY_TIME, time)
        values.put(KEY_PURPOSE, purpose)
        values.put(KEY_CAPTURED_IMAGE, capturedImage)
        db.insert(TABLE_PURPOSE, null, values)
        db.close()
    }

    // Add this method in your DatabaseHelper class

    @SuppressLint("Range")
    fun getAllVisitorData(): List<VisitorDataModel> {
        val dataList = mutableListOf<VisitorDataModel>()
        val selectQuery = "SELECT * FROM $TABLE_VISITOR"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val registerNumber = cursor.getString(cursor.getColumnIndex(KEY_REGISTER_NUMBER))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val time = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                val capturedImage = cursor.getBlob(cursor.getColumnIndex(KEY_CAPTURED_IMAGE))

                dataList.add(
                    VisitorDataModel(
                        id,
                        name,
                        registerNumber,
                        date,
                        time,
                        capturedImage
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return dataList
    }

    // Add this method in your DatabaseHelper class

    @SuppressLint("Range")
    fun getAllPurposeData(): List<PurposeDataModel> {
        val dataList = mutableListOf<PurposeDataModel>()
        val selectQuery = "SELECT * FROM $TABLE_PURPOSE"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex(KEY_ID))
                val name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val registerNumber = cursor.getString(cursor.getColumnIndex(KEY_REGISTER_NUMBER))
                val date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                val time = cursor.getString(cursor.getColumnIndex(KEY_TIME))
                val purpose = cursor.getString(cursor.getColumnIndex(KEY_PURPOSE))
                val capturedImage = cursor.getBlob(cursor.getColumnIndex(KEY_CAPTURED_IMAGE))

                dataList.add(
                    PurposeDataModel(
                        id,
                        name,
                        registerNumber,
                        date,
                        time,
                        purpose,
                        capturedImage
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return dataList
    }

     fun exportDataToExcel(context: Context, intent: Intent, exportPurpose: Boolean) {
        val workbook = XSSFWorkbook()

        val dataList: List<Any> = if (exportPurpose) {
            getAllPurposeData() // Replace this with your function to get purpose data
        } else {
            getAllVisitorData() // Replace this with your function to get visitor data
        }

        saveExcelFile(context, workbook, exportPurpose, dataList)
    }
    fun sendExcelFileViaEmail(context: Context, exportPurpose: Boolean) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = if (exportPurpose) "PurposeData_$timestamp.xlsx" else "VisitorData_$timestamp.xlsx"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

        if (file.exists()) {
            // Create an email intent
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            // Optionally set email subject and body
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hostel Register")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Attachment")

            // Start the email intent
            try {
                context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No email client installed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show()
        }
    }


    fun saveExcelFile(context: Context, workbook: XSSFWorkbook, exportPurpose: Boolean, dataList: List<Any>) {
        val sheet = workbook.createSheet("Sheet1")

        // Write data to the sheet based on the data model
        writeDataToSheet(sheet, dataList, exportPurpose)

        // Get the Downloads directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // Create a unique file name with timestamp
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = if (exportPurpose) "PurposeData_$timestamp.xlsx" else "VisitorData_$timestamp.xlsx"

        val excelFile = File(downloadsDir, fileName)

        try {
            // Create output stream to write the workbook to the file
            FileOutputStream(excelFile).use { fileOut ->
                workbook.write(fileOut)
                workbook.close()
            }

            // Notify the MediaScanner about the new file so that it's visible in the Downloads
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(excelFile)))

            // Display a toast message with the file path
            Toast.makeText(context, "Excel file saved successfully at ${excelFile.absolutePath}", Toast.LENGTH_SHORT).show()
            println("Excel file saved successfully at ${excelFile.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error saving Excel file", Toast.LENGTH_SHORT).show()
        }
    }


    fun writeDataToSheet(sheet: XSSFSheet, dataList: List<Any>, exportPurpose: Boolean) {
        val headerRow = sheet.createRow(0)

        // Define headers based on your data model
        val headers = arrayOf("ID", "Name", "Register Number", "Date", "Time", if (exportPurpose) "Purpose" else "", "Captured Image")

        for ((index, header) in headers.withIndex()) {
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }

        for ((rowIndex, data) in dataList.withIndex()) {
            val dataRow = sheet.createRow(rowIndex + 1)

            // Populate rows based on your data model
            // Adjust the code according to your actual data model
            if (data is VisitorDataModel) {
                dataRow.createCell(0).setCellValue(data.id.toDouble())
                dataRow.createCell(1).setCellValue(data.name)
                dataRow.createCell(2).setCellValue(data.registerNumber)
                dataRow.createCell(3).setCellValue(data.date)
                dataRow.createCell(4).setCellValue(data.time)
                // Add more cells for other properties
            } else if (data is PurposeDataModel) {
                dataRow.createCell(0).setCellValue(data.id.toDouble())
                dataRow.createCell(1).setCellValue(data.name)
                dataRow.createCell(2).setCellValue(data.registerNumber)
                dataRow.createCell(3).setCellValue(data.date)
                dataRow.createCell(4).setCellValue(data.time)
                if (exportPurpose) {
                    dataRow.createCell(5).setCellValue(data.purpose)
                }
                // Add more cells for other properties
            }
        }
    }
    fun clearAllData() {
        val db = this.writableDatabase

        // Clear records from the visitor table
        db.delete(TABLE_VISITOR, null, null)

        // Clear records from the purpose table
        db.delete(TABLE_PURPOSE, null, null)

        // Close the database
        db.close()
    }
}
