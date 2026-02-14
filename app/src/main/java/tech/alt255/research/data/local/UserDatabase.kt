package tech.alt255.research.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.alt255.research.data.local.dao.UserDao
import tech.alt255.research.data.local.entity.UserAchievementEntity
import tech.alt255.research.data.local.entity.UserCatEntity
import tech.alt255.research.data.local.entity.UserProfileEntity
import tech.alt255.research.data.local.entity.UserQuestEntity

@Database(
    entities = [
        UserProfileEntity::class,
        UserQuestEntity::class,
        UserAchievementEntity::class,
        UserCatEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}