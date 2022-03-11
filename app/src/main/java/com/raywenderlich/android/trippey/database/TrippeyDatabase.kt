package com.raywenderlich.android.trippey.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raywenderlich.android.trippey.database.DatabaseConstants.DATABASE_NAME
import com.raywenderlich.android.trippey.database.DatabaseConstants.DATABASE_VERSION
import com.raywenderlich.android.trippey.database.DatabaseConstants.QUERY_BY_ID
import com.raywenderlich.android.trippey.database.DatabaseConstants.SQL_CREATE_ENTRIES
import com.raywenderlich.android.trippey.database.DatabaseConstants.SQL_DELETE_ENTRIES
import com.raywenderlich.android.trippey.database.DatabaseConstants.SQL_UPDATE_TABLE_ADD_LOCATIONS
import com.raywenderlich.android.trippey.model.Trip
import com.raywenderlich.android.trippey.model.TripLocation

class TrippeyDatabase(
    context: Context,
    private val gson: Gson
) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1 && newVersion == 2) {
            db?.execSQL(SQL_UPDATE_TABLE_ADD_LOCATIONS)
        } else {
            db?.execSQL(SQL_DELETE_ENTRIES)
            onCreate(db)
        }
    }

    fun getTrips(): List<Trip> {
        val trips = mutableListOf<Trip>()
        val database = readableDatabase ?: return trips

        database.query(
            DatabaseConstants.TRIP_TABLE_NAME,
            null, null, null, null, null, null
        ).use {
            while (it.moveToNext()) {
                trips += Trip(
                    it.getString(it.getColumnIndexOrThrow(DatabaseConstants.COLUMN_ID)),
                    it.getString(it.getColumnIndexOrThrow(DatabaseConstants.COLUMN_TITLE)),
                    it.getString(it.getColumnIndexOrThrow(DatabaseConstants.COLUMN_COUNTRY)),
                    it.getString(it.getColumnIndexOrThrow(DatabaseConstants.COLUMN_DETAILS)),
                    it.getString(it.getColumnIndexOrThrow(DatabaseConstants.COLUMN_IMAGE_URL)),
                    parseTripLocations(it.getString(it.getColumnIndexOrThrow(DatabaseConstants.COLUMN_LOCATIONS)))
                )
            }
        }

        return trips
    }

    fun saveTrip(trip: Trip) {
        val database = writableDatabase ?: return
        val newValues = ContentValues().apply {
            put(DatabaseConstants.COLUMN_ID, trip.id)
            put(DatabaseConstants.COLUMN_TITLE, trip.title)
            put(DatabaseConstants.COLUMN_COUNTRY, trip.country)
            put(DatabaseConstants.COLUMN_DETAILS, trip.details)
            put(DatabaseConstants.COLUMN_IMAGE_URL, trip.imageUrl)
            put(DatabaseConstants.COLUMN_LOCATIONS, gson.toJson(trip.locations))
        }
        database.insert(DatabaseConstants.TRIP_TABLE_NAME, null, newValues)
    }

    fun updateTrip(trip: Trip) {
        TODO()
    }

    fun deleteTrip(tripId: String) {
        val database = writableDatabase ?: return
        database.delete(DatabaseConstants.TRIP_TABLE_NAME, QUERY_BY_ID, arrayOf(tripId))
    }

    private fun parseTripLocations(json: String?): List<TripLocation> {
        if (json == null) return emptyList()

        val typeToken = object : TypeToken<List<TripLocation>>() {}.type

        return try {
            gson.fromJson(json, typeToken)
        } catch (e: Throwable) {
            e.printStackTrace()
            emptyList()
        }
    }
}