package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.EventCategory
import com.example.data.model.PostCategory
import com.example.data.model.ProjectStatus
import com.example.data.model.RoleType
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val roleType: RoleType,
    val position: String,
    val whatsappNumber: String,
    val avatarColorHex: String = "#0F4C5C",
    val department: String = "Opérations",
    val bio: String = ""
)

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val assignedUser: String = "",
    val deadline: String = "",
    val completionStatus: String = "À faire",
    val isCompleted: Boolean = false,
    val assignedToUserId: String = assignedUser,
    val assignedByUserId: String = "admin_1",
    val projectId: Long? = null,
    val status: TaskStatus = if (isCompleted) TaskStatus.DONE else TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: String = deadline,
    val createdAt: Long = System.currentTimeMillis()
)

typealias TaskEntity = Task

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val leadAdminId: String,
    val budget: String = "0 $",
    val status: ProjectStatus = ProjectStatus.PLANNING,
    val startDate: String,
    val targetDate: String,
    val googleSheetUrl: String = ""
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorId: String,
    val title: String,
    val content: String,
    val category: PostCategory = PostCategory.GENERAL,
    val likesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Entity(tableName = "community_replies")
data class CommunityReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorId: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "programme_events")
data class ProgrammeEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val locationOrLink: String = "Bureau principal / En ligne",
    val organizerId: String,
    val category: EventCategory = EventCategory.MEETING
)

@Entity(tableName = "spreadsheet_tables")
data class SpreadsheetTableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val columnsRaw: String, // Comma-delimited list of column names
    val rowsRaw: String,    // Newline-delimited rows, comma/tab separated cells
    val externalGoogleSheetUrl: String = "",
    val lastModifiedBy: String = "Admin 1",
    val lastModifiedTimestamp: Long = System.currentTimeMillis()
)
