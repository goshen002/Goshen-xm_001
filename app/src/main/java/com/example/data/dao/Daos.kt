package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CommunityPostEntity
import com.example.data.entity.CommunityReplyEntity
import com.example.data.entity.ProgrammeEventEntity
import com.example.data.entity.ProjectEntity
import com.example.data.entity.SpreadsheetTableEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY CASE roleType WHEN 'ADMIN_1' THEN 1 WHEN 'ADMIN_2' THEN 2 WHEN 'ADMIN_3' THEN 3 ELSE 4 END, name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE assignedToUserId = :userId ORDER BY createdAt DESC")
    fun getTasksForUser(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getTasksForProject(projectId: Long): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun getProjectById(projectId: Long): Flow<ProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM community_posts ORDER BY isPinned DESC, timestamp DESC")
    fun getAllPosts(): Flow<List<CommunityPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CommunityPostEntity): Long

    @Update
    suspend fun updatePost(post: CommunityPostEntity)

    @Delete
    suspend fun deletePost(post: CommunityPostEntity)

    @Query("SELECT * FROM community_replies WHERE postId = :postId ORDER BY timestamp ASC")
    fun getRepliesForPost(postId: Long): Flow<List<CommunityReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: CommunityReplyEntity): Long
}

@Dao
interface ProgrammeDao {
    @Query("SELECT * FROM programme_events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<ProgrammeEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ProgrammeEventEntity): Long

    @Update
    suspend fun updateEvent(event: ProgrammeEventEntity)

    @Delete
    suspend fun deleteEvent(event: ProgrammeEventEntity)
}

@Dao
interface SpreadsheetDao {
    @Query("SELECT * FROM spreadsheet_tables ORDER BY lastModifiedTimestamp DESC")
    fun getAllSheets(): Flow<List<SpreadsheetTableEntity>>

    @Query("SELECT * FROM spreadsheet_tables WHERE id = :id LIMIT 1")
    fun getSheetById(id: Long): Flow<SpreadsheetTableEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSheet(sheet: SpreadsheetTableEntity): Long

    @Update
    suspend fun updateSheet(sheet: SpreadsheetTableEntity)

    @Delete
    suspend fun deleteSheet(sheet: SpreadsheetTableEntity)
}
