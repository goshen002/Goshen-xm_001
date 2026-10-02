package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CommunityDao
import com.example.data.dao.ProgrammeDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.SpreadsheetDao
import com.example.data.dao.TaskDao
import com.example.data.dao.UserDao
import com.example.data.entity.CommunityPostEntity
import com.example.data.entity.CommunityReplyEntity
import com.example.data.entity.ProgrammeEventEntity
import com.example.data.entity.ProjectEntity
import com.example.data.entity.SpreadsheetTableEntity
import com.example.data.entity.Task
import com.example.data.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        Task::class,
        ProjectEntity::class,
        CommunityPostEntity::class,
        CommunityReplyEntity::class,
        ProgrammeEventEntity::class,
        SpreadsheetTableEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun projectDao(): ProjectDao
    abstract fun communityDao(): CommunityDao
    abstract fun programmeDao(): ProgrammeDao
    abstract fun spreadsheetDao(): SpreadsheetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "synergie_enterprise.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabasePrepopulateCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabasePrepopulateCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Pre-populate through coroutine when instance is created
        }
    }
}
