package com.anggi.timo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anggi.timo.laporan.LaporanBelajarEntity

@Database(entities = [LaporanBelajarEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun laporanBelajarDao(): LaporanBelajarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "timo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}