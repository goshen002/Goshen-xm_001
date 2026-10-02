package com.example.data.database

import androidx.room.TypeConverter
import com.example.data.model.EventCategory
import com.example.data.model.PostCategory
import com.example.data.model.ProjectStatus
import com.example.data.model.RoleType
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromRoleType(role: RoleType): String = role.name

    @TypeConverter
    fun toRoleType(value: String): RoleType = try {
        RoleType.valueOf(value)
    } catch (_: Exception) {
        RoleType.MEMBER
    }

    @TypeConverter
    fun fromTaskStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toTaskStatus(value: String): TaskStatus = try {
        TaskStatus.valueOf(value)
    } catch (_: Exception) {
        TaskStatus.TODO
    }

    @TypeConverter
    fun fromTaskPriority(priority: TaskPriority): String = priority.name

    @TypeConverter
    fun toTaskPriority(value: String): TaskPriority = try {
        TaskPriority.valueOf(value)
    } catch (_: Exception) {
        TaskPriority.MEDIUM
    }

    @TypeConverter
    fun fromProjectStatus(status: ProjectStatus): String = status.name

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus = try {
        ProjectStatus.valueOf(value)
    } catch (_: Exception) {
        ProjectStatus.PLANNING
    }

    @TypeConverter
    fun fromPostCategory(cat: PostCategory): String = cat.name

    @TypeConverter
    fun toPostCategory(value: String): PostCategory = try {
        PostCategory.valueOf(value)
    } catch (_: Exception) {
        PostCategory.GENERAL
    }

    @TypeConverter
    fun fromEventCategory(cat: EventCategory): String = cat.name

    @TypeConverter
    fun toEventCategory(value: String): EventCategory = try {
        EventCategory.valueOf(value)
    } catch (_: Exception) {
        EventCategory.MEETING
    }
}
