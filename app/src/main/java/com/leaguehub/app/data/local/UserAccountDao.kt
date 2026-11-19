package com.leaguehub.app.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE userId = :id") suspend fun findById(id: String): UserAccountEntity?
    @Query("UPDATE user_accounts SET name = :name WHERE userId = :id") suspend fun updateName(id: String, name: String)
    @Query("UPDATE user_accounts SET passwordHash = :hash, passwordSalt = :salt WHERE userId = :id") suspend fun updatePassword(id: String, hash: String, salt: String)
    @Insert suspend fun insert(account: UserAccountEntity)
    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserAccountEntity?
    @Query("UPDATE user_accounts SET role = :role, selectedLeagueId = :leagueId, onboardingCompleted = :completed WHERE userId = :userId")
    suspend fun updatePreferences(userId: String, role: String?, leagueId: String?, completed: Boolean)
}
