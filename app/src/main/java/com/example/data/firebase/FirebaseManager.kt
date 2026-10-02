package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.entity.CommunityPostEntity
import com.example.data.entity.ProgrammeEventEntity
import com.example.data.entity.Task
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

data class FirebaseConnectionState(
    val isInitialized: Boolean = false,
    val projectId: String = "",
    val isUserAuthenticated: Boolean = false,
    val userIdentifier: String = "",
    val lastSyncStatus: String = "Non synchronisé",
    val isSyncing: Boolean = false
)

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    fun isFirebaseReady(context: Context): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    fun getOrInitDefaultApp(context: Context): FirebaseApp? {
        return try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseApp.getInstance()
            } else {
                FirebaseApp.initializeApp(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Default FirebaseApp not initialized: ${e.message}")
            null
        }
    }

    fun initializeWithManualConfig(
        context: Context,
        apiKey: String,
        projectId: String,
        applicationId: String
    ): Boolean {
        return try {
            if (apiKey.isBlank() || projectId.isBlank() || applicationId.isBlank()) return false
            val options = FirebaseOptions.Builder()
                .setApiKey(apiKey.trim())
                .setProjectId(projectId.trim())
                .setApplicationId(applicationId.trim())
                .build()

            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseApp.getInstance().delete()
            }
            FirebaseApp.initializeApp(context, options)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed manual Firebase initialization", e)
            false
        }
    }

    suspend fun signInAnonymouslyOrCheckAuth(context: Context): Boolean {
        return try {
            val app = getOrInitDefaultApp(context) ?: return false
            val auth = FirebaseAuth.getInstance(app)
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
            auth.currentUser != null
        } catch (e: Exception) {
            Log.w(TAG, "Auth exception (normal if rules/config pending): ${e.message}")
            false
        }
    }

    suspend fun pushTasksToFirestore(context: Context, tasks: List<Task>): Result<Int> {
        return try {
            val app = getOrInitDefaultApp(context)
                ?: return Result.failure(IllegalStateException("Firebase n'est pas encore initialisé. Veuillez ajouter le fichier google-services.json."))
            val firestore = FirebaseFirestore.getInstance(app)

            var count = 0
            for (task in tasks) {
                val data = hashMapOf(
                    "id" to task.id,
                    "title" to task.title,
                    "description" to task.description,
                    "assignedUser" to task.assignedUser,
                    "assignedToUserId" to task.assignedToUserId,
                    "assignedByUserId" to task.assignedByUserId,
                    "deadline" to task.deadline,
                    "dueDate" to task.dueDate,
                    "completionStatus" to task.completionStatus,
                    "isCompleted" to task.isCompleted,
                    "status" to task.status.name,
                    "priority" to task.priority.name,
                    "projectId" to task.projectId,
                    "updatedAt" to System.currentTimeMillis()
                )
                firestore.collection("tasks")
                    .document(task.id.toString())
                    .set(data, SetOptions.merge())
                    .await()
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing tasks to Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun pushCommunityPostsToFirestore(context: Context, posts: List<CommunityPostEntity>): Result<Int> {
        return try {
            val app = getOrInitDefaultApp(context)
                ?: return Result.failure(IllegalStateException("Firebase n'est pas initialisé."))
            val firestore = FirebaseFirestore.getInstance(app)

            var count = 0
            for (post in posts) {
                val data = hashMapOf(
                    "id" to post.id,
                    "authorId" to post.authorId,
                    "title" to post.title,
                    "content" to post.content,
                    "category" to post.category.name,
                    "likesCount" to post.likesCount,
                    "timestamp" to post.timestamp,
                    "isPinned" to post.isPinned
                )
                firestore.collection("community_posts")
                    .document(post.id.toString())
                    .set(data, SetOptions.merge())
                    .await()
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Log.e(TAG, "Error pushing posts to Firestore", e)
            Result.failure(e)
        }
    }
}
