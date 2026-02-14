package tech.alt255.research.data.local

import kotlinx.coroutines.flow.Flow
import tech.alt255.research.data.local.dao.UserDao
import tech.alt255.research.data.local.entity.UserAchievementEntity
import tech.alt255.research.data.local.entity.UserCatEntity
import tech.alt255.research.data.local.entity.UserProfileEntity
import tech.alt255.research.data.local.entity.UserQuestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserDataSource @Inject constructor(
    private val userDao: UserDao
) {

    fun observeProfile(userId: Int): Flow<UserProfileEntity?> = userDao.getProfileFlow(userId)

    suspend fun saveProfile(profile: UserProfileEntity) = userDao.insertProfile(profile)

    suspend fun clearProfile(userId: Int) = userDao.deleteProfile(userId)

    fun observeQuests(userId: Int): Flow<List<UserQuestEntity>> = userDao.getQuestsFlow(userId)

    suspend fun saveQuests(quests: List<UserQuestEntity>, page: Int, clearPrevious: Boolean) {
        if (clearPrevious) {
            userDao.deleteQuestsByPage(userId = quests.firstOrNull()?.userId ?: return, page)
        }
        userDao.insertQuests(quests)
    }

    suspend fun clearAllQuests(userId: Int) = userDao.deleteAllQuests(userId)

    fun observeAchievements(userId: Int): Flow<List<UserAchievementEntity>> = userDao.getAchievementsFlow(userId)

    suspend fun saveAchievements(achievements: List<UserAchievementEntity>, page: Int, clearPrevious: Boolean) {
        if (clearPrevious) {
            userDao.deleteAchievementsByPage(userId = achievements.firstOrNull()?.userId ?: return, page)
        }
        userDao.insertAchievements(achievements)
    }

    suspend fun clearAllAchievements(userId: Int) = userDao.deleteAllAchievements(userId)

    fun observeCats(userId: Int): Flow<List<UserCatEntity>> = userDao.getCatsFlow(userId)

    suspend fun saveCats(cats: List<UserCatEntity>, page: Int, clearPrevious: Boolean) {
        if (clearPrevious) {
            userDao.deleteCatsByPage(userId = cats.firstOrNull()?.userId ?: return, page)
        }
        userDao.insertCats(cats)
    }

    suspend fun clearAllCats(userId: Int) = userDao.deleteAllCats(userId)

    suspend fun clearAllUserData(userId: Int) {
        clearProfile(userId)
        clearAllQuests(userId)
        clearAllAchievements(userId)
        clearAllCats(userId)
    }
}