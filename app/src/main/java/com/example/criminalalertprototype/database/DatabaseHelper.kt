package com.example.criminalalertprototype.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "criminal_alert.db"
        private const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_REPORTS = "reports"
        const val TABLE_BOOKMARKS = "bookmarks"
        const val TABLE_USER_PREFERENCES = "user_preferences"

        // Reports Table Columns
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_URGENCY = "urgency"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_STATUS = "status"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_USER_NAME = "user_name"

        // Bookmarks Table Columns
        const val COLUMN_BOOKMARK_ID = "id"
        const val COLUMN_REPORT_ID = "report_id"
        const val COLUMN_BOOKMARKED_AT = "bookmarked_at"

        // User Preferences Table Columns
        const val COLUMN_PREF_ID = "id"
        const val COLUMN_PREF_USER_NAME = "user_name"
        const val COLUMN_NOTIFICATION_MODE = "notification_mode"
        const val COLUMN_STEALTH_MODE = "stealth_mode"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Reports Table
        val createReportsTable = """
            CREATE TABLE $TABLE_REPORTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_URGENCY TEXT NOT NULL,
                $COLUMN_LOCATION TEXT NOT NULL,
                $COLUMN_STATUS TEXT DEFAULT 'active',
                $COLUMN_CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_USER_NAME TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createReportsTable)

        // Create Bookmarks Table with Foreign Key to Reports
        val createBookmarksTable = """
            CREATE TABLE $TABLE_BOOKMARKS (
                $COLUMN_BOOKMARK_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_REPORT_ID INTEGER NOT NULL,
                $COLUMN_BOOKMARKED_AT DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_REPORT_ID) REFERENCES $TABLE_REPORTS($COLUMN_ID) ON DELETE CASCADE
            )
        """.trimIndent()
        db.execSQL(createBookmarksTable)

        // Create User Preferences Table
        val createUserPreferencesTable = """
            CREATE TABLE $TABLE_USER_PREFERENCES (
                $COLUMN_PREF_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PREF_USER_NAME TEXT UNIQUE NOT NULL,
                $COLUMN_NOTIFICATION_MODE TEXT DEFAULT 'all',
                $COLUMN_STEALTH_MODE INTEGER DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createUserPreferencesTable)

        // Insert dummy data for testing
        insertDummyData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPORTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKMARKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_PREFERENCES")
        onCreate(db)
    }

    private fun insertDummyData(db: SQLiteDatabase) {
        val dummyReports = listOf(
            createDummyReport("Suspicious Activity", "Silver sedan idling with headlights off near the north gate", "Suspicious", "Medium", "North Gate Area", "John"),
            createDummyReport("Theft Reported", "Bicycle stolen from porch on Elm Street. Lock was cut.", "Theft", "High", "Elm Street", "John"),
            createDummyReport("Fire Incident", "Small trash fire contained near community park. Fire department notified.", "Fire", "High", "Community Park", "John"),
            createDummyReport("Vandalism", "Graffiti reported on wall of abandoned building", "Vandalism", "Low", "Oak Street", "John"),
            createDummyReport("Gas Leak Report", "Residents report strong gas smell near the intersection", "Fire", "High", "Main & 2nd Street", "John")
        )

        dummyReports.forEach { report ->
            db.insert(TABLE_REPORTS, null, report)
        }
    }

    private fun createDummyReport(
        title: String,
        description: String,
        category: String,
        urgency: String,
        location: String,
        userName: String
    ): ContentValues {
        return ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_CATEGORY, category)
            put(COLUMN_URGENCY, urgency)
            put(COLUMN_LOCATION, location)
            put(COLUMN_USER_NAME, userName)
            put(COLUMN_STATUS, "active")
        }
    }
}