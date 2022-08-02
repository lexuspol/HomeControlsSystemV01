package com.example.homecontrolssystemv01.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataDbModel::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    companion object{
        private var db: AppDatabase? = null
        private const val DB_NAME = "main.db"
        private val LOCK = Any()//для синхронизации

        fun getInstance(context: Context): AppDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                db = instance
                return instance
            }
        }
    }
    abstract fun dataDao(): DataDao
}