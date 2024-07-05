package com.the.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.the.app.models.Album
import com.the.app.models.Information

@Database(
    entities = [Album::class, Information::class],
    version = 1
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getAppDao(): AppDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

         fun getDb(context: Context): AppDataBase {
            return instance ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "App_DB"
                ).build()
                this.instance = instance
                return instance
            }
        }
    }
}