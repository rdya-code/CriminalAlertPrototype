package com.example.criminalalertprototype.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.criminalalertprototype.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferenceRepository(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // CREATE or UPDATE user preferences
    suspend fun saveUserPreferences(user: UserModel): Long = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        
        // Check if user exists
        val existing = getUserPreferences(user.username)
        
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PREF_USER_NAME, user.username)
            put(DatabaseHelper.COLUMN_NOTIFICATION_MODE, user.notificationMode)
            put(DatabaseHelper.COLUMN_STEALTH_MODE, if (user.stealthMode) 1 else 0)
        }
        
        val result = if (existing != null) {
            // Update existing
            db.update(
                DatabaseHelper.TABLE_USER_PREFERENCES,
                values,
                "${DatabaseHelper.COLUMN_PREF_USER_NAME} = ?",
                arrayOf(user.username)
            ).toLong()
        } else {
            // Insert new
            db.insert(DatabaseHelper.TABLE_USER_PREFERENCES, null, values)
        }
        
        db.close()
        return@withContext result
    }

    // READ - Get user preferences
    suspend fun getUserPreferences(username: String): UserModel? = withContext(Dispatchers.IO) {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_USER_PREFERENCES}
            WHERE ${DatabaseHelper.COLUMN_PREF_USER_NAME} = ?
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(username))
        var user: UserModel? = null
        
        if (cursor.moveToFirst()) {
            user = UserModel(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREF_ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PREF_USER_NAME)),
                notificationMode = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIFICATION_MODE)),
                stealthMode = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STEALTH_MODE)) == 1
            )
        }
        cursor.close()
        db.close()
        return@withContext user
    }

    // UPDATE notification mode
    suspend fun updateNotificationMode(username: String, mode: String): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOTIFICATION_MODE, mode)
        }
        val result = db.update(
            DatabaseHelper.TABLE_USER_PREFERENCES,
            values,
            "${DatabaseHelper.COLUMN_PREF_USER_NAME} = ?",
            arrayOf(username)
        )
        db.close()
        return@withContext result
    }

    // UPDATE stealth mode
    suspend fun updateStealthMode(username: String, enabled: Boolean): Int = withContext(Dispatchers.IO) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_STEALTH_MODE, if (enabled) 1 else 0)
        }
        val result = db.update(
            DatabaseHelper.TABLE_USER_PREFERENCES,
            values,
            "${DatabaseHelper.COLUMN_PREF_USER_NAME} = ?",
            arrayOf(username)
        )
        db.close()
        return@withContext result
    }
}