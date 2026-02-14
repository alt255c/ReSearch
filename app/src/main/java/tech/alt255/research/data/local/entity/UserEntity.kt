package tech.alt255.research.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: Int,
    val email: String,
    val userName: String,
    val userNickname: String,
    val userPhoto: String,
    val stars: Int,
    val level: Int,
    val nextLevelStars: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_quests", primaryKeys = ["id", "userId"])
data class UserQuestEntity(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val questType: String,
    val rewardStars: Int,
    val userStatus: String,
    val progress: Int,
    val totalSteps: Int,
    val completedAt: String?,
    val districtName: String,
    val isRelevant: Boolean,
    val page: Int
)

@Entity(tableName = "user_achievements", primaryKeys = ["id", "userId"])
data class UserAchievementEntity(
    val id: Int,
    val userId: Int,
    val name: String,
    val description: String,
    val points: Int,
    val isCompleted: Boolean,
    val unlockedAt: String?,
    val page: Int
)

@Entity(tableName = "user_cats", primaryKeys = ["id", "userId"])
data class UserCatEntity(
    val id: Int,
    val userId: Int,
    val name: String,
    val rarity: String,
    val imageUrl: String,
    val description: String,
    val baseValue: Int,
    val obtainedAt: String,
    val page: Int
)