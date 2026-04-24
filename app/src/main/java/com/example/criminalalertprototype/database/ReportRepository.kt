package com.example.criminalalertprototype.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.criminalalertprototype.models.ReportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // CREATE - Insert a new report
    suspend fun insertReport(report: ReportModel): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, report.title)
            put(DatabaseHelper.COLUMN_DESCRIPTION, report.description)
            put(DatabaseHelper.COLUMN_CATEGORY, report.category)
            put(DatabaseHelper.COLUMN_URGENCY, report.urgency)
            put(DatabaseHelper.COLUMN_LOCATION, report.location)
            put(DatabaseHelper.COLUMN_USER_NAME, report.userName)
            put(DatabaseHelper.COLUMN_STATUS, report.status)
        }
        val result = db.insert(DatabaseHelper.TABLE_REPORTS, null, values)
        db.close()
        return@withContext result
    }

    // READ - Get all active reports
    suspend fun getAllActiveReports(userName: String): List<ReportModel> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_REPORTS}
            WHERE ${DatabaseHelper.COLUMN_USER_NAME} = ? 
            AND ${DatabaseHelper.COLUMN_STATUS} = 'active'
            ORDER BY ${DatabaseHelper.COLUMN_CREATED_AT} DESC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(userName))
        val reports = mutableListOf<ReportModel>()
        
        while (cursor.moveToNext()) {
            val report = ReportModel(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)),
                urgency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URGENCY)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME))
            )
            reports.add(report)
        }
        cursor.close()
        db.close()
        return@withContext reports
    }

    // READ - Get all reports (including confirmed/dismissed)
    suspend fun getAllReports(userName: String): List<ReportModel> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_REPORTS}
            WHERE ${DatabaseHelper.COLUMN_USER_NAME} = ?
            ORDER BY ${DatabaseHelper.COLUMN_CREATED_AT} DESC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(userName))
        val reports = mutableListOf<ReportModel>()
        
        while (cursor.moveToNext()) {
            val report = ReportModel(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)),
                urgency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URGENCY)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME))
            )
            reports.add(report)
        }
        cursor.close()
        db.close()
        return@withContext reports
    }

    // UPDATE - Update report status (confirm/dismiss)
    suspend fun updateReportStatus(reportId: Int, newStatus: String) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_STATUS, newStatus)
        }
        val result = db.update(
            DatabaseHelper.TABLE_REPORTS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(reportId.toString())
        )
        db.close()
        return@withContext result
    }

    // DELETE - Delete a report
    suspend fun deleteReport(reportId: Int) = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DatabaseHelper.TABLE_REPORTS,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(reportId.toString())
        )
        db.close()
        return@withContext result
    }

    // SEARCH - Dynamic SQL Query with LIKE (F5 Requirement)
    suspend fun searchReports(userName: String, query: String): List<ReportModel> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val searchQuery = """
            SELECT * FROM ${DatabaseHelper.TABLE_REPORTS}
            WHERE ${DatabaseHelper.COLUMN_USER_NAME} = ?
            AND (${DatabaseHelper.COLUMN_TITLE} LIKE ? 
                OR ${DatabaseHelper.COLUMN_DESCRIPTION} LIKE ? 
                OR ${DatabaseHelper.COLUMN_LOCATION} LIKE ?
                OR ${DatabaseHelper.COLUMN_CATEGORY} LIKE ?)
            ORDER BY ${DatabaseHelper.COLUMN_CREATED_AT} DESC
        """.trimIndent()
        
        val searchPattern = "%$query%"
        val cursor = db.rawQuery(searchQuery, arrayOf(userName, searchPattern, searchPattern, searchPattern, searchPattern))
        val reports = mutableListOf<ReportModel>()
        
        while (cursor.moveToNext()) {
            val report = ReportModel(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY)),
                urgency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_URGENCY)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LOCATION)),
                status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME))
            )
            reports.add(report)
        }
        cursor.close()
        db.close()
        return@withContext reports
    }

    // GET Active Alerts Count (for Stats Bar)
    suspend fun getActiveAlertsCount(userName: String): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_REPORTS}
            WHERE ${DatabaseHelper.COLUMN_USER_NAME} = ? 
            AND ${DatabaseHelper.COLUMN_STATUS} = 'active'
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(userName))
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return@withContext count
    }
}