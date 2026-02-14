package tech.alt255.research.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.alt255.research.data.local.entity.UserAchievementEntity
import tech.alt255.research.data.local.entity.UserCatEntity
import tech.alt255.research.data.local.entity.UserProfileEntity
import tech.alt255.research.data.local.entity.UserQuestEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM user_profile WHERE userId = :userId")
    fun getProfileFlow(userId: Int): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity)

    @Query("DELETE FROM user_profile WHERE userId = :userId")
    suspend fun deleteProfile(userId: Int)

    @Query("SELECT * FROM user_quests WHERE userId = :userId ORDER BY page ASC, id ASC")
    fun getQuestsFlow(userId: Int): Flow<List<UserQuestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuests(quests: List<UserQuestEntity>)

    @Query("DELETE FROM user_quests WHERE userId = :userId AND page = :page")
    suspend fun deleteQuestsByPage(userId: Int, page: Int)

    @Query("DELETE FROM user_quests WHERE userId = :userId")
    suspend fun deleteAllQuests(userId: Int)

    @Query("SELECT * FROM user_achievements WHERE userId = :userId ORDER BY page ASC, id ASC")
    fun getAchievementsFlow(userId: Int): Flow<List<UserAchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<UserAchievementEntity>)

    @Query("DELETE FROM user_achievements WHERE userId = :userId AND page = :page")
    suspend fun deleteAchievementsByPage(userId: Int, page: Int)

    @Query("DELETE FROM user_achievements WHERE userId = :userId")
    suspend fun deleteAllAchievements(userId: Int)

    @Query("SELECT * FROM user_cats WHERE userId = :userId ORDER BY page ASC, id ASC")
    fun getCatsFlow(userId: Int): Flow<List<UserCatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCats(cats: List<UserCatEntity>)

    @Query("DELETE FROM user_cats WHERE userId = :userId AND page = :page")
    suspend fun deleteCatsByPage(userId: Int, page: Int)

    @Query("DELETE FROM user_cats WHERE userId = :userId")
    suspend fun deleteAllCats(userId: Int)
}