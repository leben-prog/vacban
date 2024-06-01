package com.example.vacban

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class User(val id: Int, val login: String, val name: String, val pass: String)
data class UserRequest(val id: Int, val category: String, val departureDate: String, val arrivalDate: String, val status: String, val userId: Int)

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "app.db"
        private const val TABLE_USERS = "users"
        private const val TABLE_REQUESTS = "requests"

        private const val USER_ID = "id"
        private const val USER_LOGIN = "login"
        private const val USER_NAME = "name"
        private const val USER_PASS = "pass"

        private const val REQUEST_ID = "id"
        private const val REQUEST_CATEGORY = "category"
        private const val REQUEST_DEPARTURE_DATE = "departureDate"
        private const val REQUEST_ARRIVAL_DATE = "arrivalDate"
        private const val REQUEST_STATUS = "status"
        private const val REQUEST_USER_ID = "userId"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = "CREATE TABLE $TABLE_USERS (" +
                "$USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USER_LOGIN TEXT, " +
                "$USER_NAME TEXT, " +
                "$USER_PASS TEXT)"
        db.execSQL(createUserTable)

        val createRequestTable = "CREATE TABLE $TABLE_REQUESTS (" +
                "$REQUEST_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$REQUEST_CATEGORY TEXT, " +
                "$REQUEST_DEPARTURE_DATE TEXT, " +
                "$REQUEST_ARRIVAL_DATE TEXT, " +
                "$REQUEST_STATUS TEXT, " +
                "$REQUEST_USER_ID INTEGER)"
        db.execSQL(createRequestTable)

        // Adding admin user
        val addAdmin = "INSERT INTO $TABLE_USERS ($USER_LOGIN, $USER_NAME, $USER_PASS) VALUES ('admin', 'Админ', 'adminpass')"
        db.execSQL(addAdmin)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REQUESTS")
        onCreate(db)
    }

    fun addUser(user: User) {
        val values = ContentValues().apply {
            put(USER_LOGIN, user.login)
            put(USER_NAME, user.name)
            put(USER_PASS, user.pass)
        }

        val db = this.writableDatabase
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun getUser(login: String, pass: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $USER_LOGIN = ? AND $USER_PASS = ?"
        val result = db.rawQuery(query, arrayOf(login, pass))
        val exists = result.moveToFirst()
        result.close()
        return exists
    }

    fun addReq(item: UserRequest, userId: Int) {
        val values = ContentValues().apply {
            put(REQUEST_CATEGORY, item.category)
            put(REQUEST_DEPARTURE_DATE, item.departureDate)
            put(REQUEST_ARRIVAL_DATE, item.arrivalDate)
            put(REQUEST_STATUS, item.status)
            put(REQUEST_USER_ID, userId)
        }

        val db = this.writableDatabase
        db.insert(TABLE_REQUESTS, null, values)
        db.close()
        Log.d("DbHelper", "Request added: $item for user $userId")
    }

    fun getReq(userId: Int): List<UserRequest> {
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM $TABLE_REQUESTS WHERE $REQUEST_USER_ID = ?", arrayOf(userId.toString()))
        val requests = mutableListOf<UserRequest>()

        if (result.moveToFirst()) {
            do {
                val id = result.getInt(result.getColumnIndexOrThrow(REQUEST_ID))
                val category = result.getString(result.getColumnIndexOrThrow(REQUEST_CATEGORY))
                val departureDate = result.getString(result.getColumnIndexOrThrow(REQUEST_DEPARTURE_DATE))
                val arrivalDate = result.getString(result.getColumnIndexOrThrow(REQUEST_ARRIVAL_DATE))
                val status = result.getString(result.getColumnIndexOrThrow(REQUEST_STATUS))
                val userId = result.getInt(result.getColumnIndexOrThrow(REQUEST_USER_ID))
                val request = UserRequest(id, category, departureDate, arrivalDate, status, userId)
                requests.add(request)
                Log.d("DbHelper", "Request loaded: $request")
            } while (result.moveToNext())
        } else {
            Log.d("DbHelper", "No requests found for user $userId")
        }
        result.close()
        return requests
    }

    fun getAllUsers(): List<User> {
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM $TABLE_USERS", null)
        val users = mutableListOf<User>()

        if (result.moveToFirst()) {
            do {
                val id = result.getInt(result.getColumnIndexOrThrow(USER_ID))
                val login = result.getString(result.getColumnIndexOrThrow(USER_LOGIN))
                val name = result.getString(result.getColumnIndexOrThrow(USER_NAME))
                val pass = result.getString(result.getColumnIndexOrThrow(USER_PASS))
                val user = User(id, login, name, pass)
                users.add(user)
                Log.d("DbHelper", "User loaded: $user")
            } while (result.moveToNext())
        } else {
            Log.d("DbHelper", "No users found")
        }
        result.close()
        return users
    }

    fun updateRequestStatus(requestId: Int, newStatus: String) {
        val values = ContentValues().apply {
            put(REQUEST_STATUS, newStatus)
        }

        val db = this.writableDatabase
        db.update(TABLE_REQUESTS, values, "$REQUEST_ID = ?", arrayOf(requestId.toString()))
        db.close()
        Log.d("DbHelper", "Request status updated: $requestId to $newStatus")
    }
}
