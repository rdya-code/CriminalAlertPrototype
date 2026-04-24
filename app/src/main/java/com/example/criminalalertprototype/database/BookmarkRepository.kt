package com.example.criminalalertprototype.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.criminalalertprototype.models.ReportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookmarkRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // CREATE - Add bookmark
    suspend fun addBookmark(reportId: Int): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_REPORT_ID, reportId)
        }
        val result = db.insert(DatabaseHelper.TABLE_BOOKMARKS, null, values)
        db.close()
        return@withContext result
    }

    // DELETE - Remove bookmark
    suspend fun removeBookmark(reportId: Int): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val result = db.delete(
            DatabaseHelper.TABLE_BOOKMARKS,
            "${DatabaseHelper.COLUMN_REPORT_ID} = ?",
            arrayOf(reportId.toString())
        )
        db.close()
        return@withContext result
    }

    // READ - Check if report is bookmarked
    suspend fun isBookmarked(reportId: Int): Boolean = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_BOOKMARKS}
            WHERE ${DatabaseHelper.COLUMN_REPORT_ID} = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(reportId.toString()))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return@withContext exists
    }

    // READ - Get all bookmarked reports
    suspend fun getAllBookmarkedReports(userName: String): List<ReportModel> = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT r.* FROM ${DatabaseHelper.TABLE_REPORTS} r
            INNER JOIN ${DatabaseHelper.TABLE_BOOKMARKS} b 
            ON r.${DatabaseHelper.COLUMN_ID} = b.${DatabaseHelper.COLUMN_REPORT_ID}
            WHERE r.${DatabaseHelper.COLUMN_USER_NAME} = ?
            ORDER BY b.${DatabaseHelper.COLUMN_BOOKMARKED_AT} DESC
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

    // GET bookmark count
    suspend fun getBookmarkCount(): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_BOOKMARKS}"
        val cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return@withContext count
    }
}