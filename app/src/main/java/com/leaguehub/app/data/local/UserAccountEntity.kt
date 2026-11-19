package com.leaguehub.app.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "user_accounts", indices = [Index(value = ["email"], unique = true)])
data class UserAccountEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val role: String? = null,
    val selectedLeagueId: String? = null,
    val onboardingCompleted: Boolean = false
)