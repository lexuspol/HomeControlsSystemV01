package com.example.homecontrolssystemv01.data.database.shop

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShopDbModel::class,TaskDbModel::class],
    version = 2,
    exportSchema = false)
abstract class ShopDatabase: RoomDatabase() {
    companion object{
        private var db: ShopDatabase? = null
        private const val DB_NAME = "shop.db"
        private val LOCK = Any()//для синхронизации

        fun getInstance(context: Context): ShopDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        ShopDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                db = instance
                return instance
            }
        }
    }
    abstract fun shopDao(): ShopDao
}