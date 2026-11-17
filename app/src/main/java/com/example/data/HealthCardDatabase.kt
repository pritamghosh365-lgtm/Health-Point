package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HealthCard::class], version = 1, exportSchema = false)
abstract class HealthCardDatabase : RoomDatabase() {
    abstract fun healthCardDao(): HealthCardDao

    companion object {
        @Volatile
        private var INSTANCE: HealthCardDatabase? = null

        fun getDatabase(context: Context): HealthCardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HealthCardDatabase::class.java,
                    "health_point_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
