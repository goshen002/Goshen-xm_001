package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entity.CommunityPostEntity
import com.example.data.entity.CommunityReplyEntity
import com.example.data.entity.ProgrammeEventEntity
import com.example.data.entity.ProjectEntity
import com.example.data.entity.SpreadsheetTableEntity
import com.example.data.entity.Task
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserEntity
import com.example.data.firebase.FirebaseConnectionState
import com.example.data.firebase.FirebaseManager
import com.example.data.model.EventCategory
import com.example.data.model.PostCategory
import com.example.data.model.ProjectStatus
import com.example.data.model.RoleType
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.data.repository.EnterpriseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EnterpriseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EnterpriseRepository

    val allUsers: StateFlow<List<UserEntity>>
    val allTasks: StateFlow<List<TaskEntity>>
    val allProjects: StateFlow<List<ProjectEntity>>
    val allPosts: StateFlow<List<CommunityPostEntity>>
    val allEvents: StateFlow<List<ProgrammeEventEntity>>
    val allSheets: StateFlow<List<SpreadsheetTableEntity>>

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _activePostIdForReplies = MutableStateFlow<Long?>(null)
    val activePostIdForReplies: StateFlow<Long?> = _activePostIdForReplies.asStateFlow()

    private val _currentPostReplies = MutableStateFlow<List<CommunityReplyEntity>>(emptyList())
    val currentPostReplies: StateFlow<List<CommunityReplyEntity>> = _currentPostReplies.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _firebaseState = MutableStateFlow(FirebaseConnectionState())
    val firebaseState: StateFlow<FirebaseConnectionState> = _firebaseState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = EnterpriseRepository(
            userDao = database.userDao(),
            taskDao = database.taskDao(),
            projectDao = database.projectDao(),
            communityDao = database.communityDao(),
            programmeDao = database.programmeDao(),
            spreadsheetDao = database.spreadsheetDao()
        )

        refreshFirebaseStatus()

        allUsers = repository.allUsers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allTasks = repository.allTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allProjects = repository.allProjects.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allPosts = repository.allPosts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allEvents = repository.allEvents.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allSheets = repository.allSheets.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.ensureInitialData()
            // Set default user to Admin 1 (Director)
            val users = repository.allUsers.first()
            if (users.isNotEmpty() && _currentUser.value == null) {
                _currentUser.value = users.firstOrNull { it.roleType == RoleType.ADMIN_1 } ?: users.first()
            }
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun selectUser(user: UserEntity) {
        _currentUser.value = user
        _userMessage.value = "Connecté en tant que ${user.name} (${user.roleType.title})"
    }

    fun registerOrUpdateWhatsAppProfile(
        name: String,
        whatsappNumber: String,
        roleType: RoleType,
        position: String,
        department: String
    ) {
        viewModelScope.launch {
            val id = "user_${System.currentTimeMillis()}"
            val colorHex = when (roleType) {
                RoleType.ADMIN_1 -> "#D4AF37"
                RoleType.ADMIN_2 -> "#2196F3"
                RoleType.ADMIN_3 -> "#9C27B0"
                RoleType.MEMBER -> "#009688"
            }
            val newUser = UserEntity(
                id = id,
                name = name.trim(),
                roleType = roleType,
                position = position.trim(),
                whatsappNumber = whatsappNumber.trim(),
                avatarColorHex = colorHex,
                department = department.trim(),
                bio = "Compte d'entreprise synchronisé WhatsApp."
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            _userMessage.value = "Profil WhatsApp créé et activé !"
        }
    }

    fun createTask(
        title: String,
        description: String,
        assignedToUserId: String,
        projectId: Long?,
        priority: TaskPriority,
        dueDate: String
    ): Boolean {
        val user = _currentUser.value
        if (user == null || !user.roleType.isAdministrator) {
            _userMessage.value = "Seuls les Administrateurs (1, 2 ou 3) peuvent créer et attribuer des tâches."
            return false
        }
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                description = description.trim(),
                assignedUser = assignedToUserId,
                assignedToUserId = assignedToUserId,
                assignedByUserId = user.id,
                projectId = projectId,
                deadline = dueDate.trim(),
                dueDate = dueDate.trim(),
                completionStatus = TaskStatus.TODO.label,
                isCompleted = false,
                status = TaskStatus.TODO,
                priority = priority
            )
            repository.insertTask(task)
            _userMessage.value = "Tâche créée et attribuée par ${user.name}."
        }
        return true
    }

    fun updateTaskStatus(task: TaskEntity, newStatus: TaskStatus) {
        viewModelScope.launch {
            repository.updateTask(
                task.copy(
                    status = newStatus,
                    completionStatus = newStatus.label,
                    isCompleted = (newStatus == TaskStatus.DONE)
                )
            )
            _userMessage.value = "Statut de la tâche mis à jour : ${newStatus.label}"
        }
    }

    fun deleteTask(task: TaskEntity) {
        val user = _currentUser.value
        if (user == null || !user.roleType.isAdministrator) {
            _userMessage.value = "Action réservée aux Administrateurs."
            return
        }
        viewModelScope.launch {
            repository.deleteTask(task)
            _userMessage.value = "Tâche supprimée."
        }
    }

    fun createProject(
        name: String,
        description: String,
        budget: String,
        startDate: String,
        targetDate: String,
        googleSheetUrl: String
    ): Boolean {
        val user = _currentUser.value
        if (user == null || !user.roleType.isAdministrator) {
            _userMessage.value = "Seuls les Administrateurs (1, 2 ou 3) peuvent élaborer des projets."
            return false
        }
        viewModelScope.launch {
            val project = ProjectEntity(
                name = name.trim(),
                description = description.trim(),
                leadAdminId = user.id,
                budget = if (budget.isBlank()) "0 $" else budget.trim(),
                status = ProjectStatus.PLANNING,
                startDate = startDate.trim(),
                targetDate = targetDate.trim(),
                googleSheetUrl = googleSheetUrl.trim()
            )
            repository.insertProject(project)
            _userMessage.value = "Projet créé avec succès !"
        }
        return true
    }

    fun updateProjectStatus(project: ProjectEntity, newStatus: ProjectStatus) {
        viewModelScope.launch {
            repository.updateProject(project.copy(status = newStatus))
            _userMessage.value = "Statut du projet mis à jour : ${newStatus.label}"
        }
    }

    fun createPost(title: String, content: String, category: PostCategory) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val post = CommunityPostEntity(
                authorId = user.id,
                title = title.trim(),
                content = content.trim(),
                category = category,
                isPinned = user.roleType == RoleType.ADMIN_1
            )
            repository.insertPost(post)
            _userMessage.value = "Sujet publié dans la communauté !"
        }
    }

    fun likePost(post: CommunityPostEntity) {
        viewModelScope.launch {
            repository.updatePost(post.copy(likesCount = post.likesCount + 1))
        }
    }

    fun selectPostForReplies(postId: Long) {
        _activePostIdForReplies.value = postId
        viewModelScope.launch {
            repository.getRepliesForPost(postId).collect { replies ->
                _currentPostReplies.value = replies
            }
        }
    }

    fun addReplyToPost(postId: Long, content: String) {
        val user = _currentUser.value ?: return
        if (content.isBlank()) return
        viewModelScope.launch {
            val reply = CommunityReplyEntity(
                postId = postId,
                authorId = user.id,
                content = content.trim()
            )
            repository.insertReply(reply)
            _userMessage.value = "Réponse ajoutée."
        }
    }

    fun createProgrammeEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        locationOrLink: String,
        category: EventCategory
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val event = ProgrammeEventEntity(
                title = title.trim(),
                description = description.trim(),
                date = date.trim(),
                time = time.trim(),
                locationOrLink = locationOrLink.trim(),
                organizerId = user.id,
                category = category
            )
            repository.insertEvent(event)
            _userMessage.value = "Événement ajouté au programme d'entreprise !"
        }
    }

    fun deleteEvent(event: ProgrammeEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            _userMessage.value = "Événement retiré du programme."
        }
    }

    // Google Sheets Management
    fun createSpreadsheet(
        title: String,
        description: String,
        columns: List<String>,
        googleSheetUrl: String = ""
    ) {
        val user = _currentUser.value
        if (user == null || !user.roleType.isAdministrator) {
            _userMessage.value = "Seuls les Administrateurs peuvent créer un tableau Google Sheets."
            return
        }
        viewModelScope.launch {
            val cleanCols = columns.joinToString(",") { it.trim() }
            val sheet = SpreadsheetTableEntity(
                title = title.trim(),
                description = description.trim(),
                columnsRaw = cleanCols,
                rowsRaw = "",
                externalGoogleSheetUrl = googleSheetUrl.trim(),
                lastModifiedBy = "${user.name} (${user.roleType.badgeSymbol})",
                lastModifiedTimestamp = System.currentTimeMillis()
            )
            repository.insertSheet(sheet)
            _userMessage.value = "Feuille de calcul créée !"
        }
    }

    fun updateSpreadsheetCell(sheet: SpreadsheetTableEntity, rowIndex: Int, colIndex: Int, newValue: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            val rawRows = sheet.rowsRaw.split("\n").filter { it.isNotBlank() }.toMutableList()
            if (rowIndex in rawRows.indices) {
                val rowCells = rawRows[rowIndex].split("\t").toMutableList()
                val colCount = sheet.columnsRaw.split(",").size
                while (rowCells.size < colCount) {
                    rowCells.add("-")
                }
                if (colIndex in rowCells.indices) {
                    rowCells[colIndex] = newValue.trim()
                    rawRows[rowIndex] = rowCells.joinToString("\t")
                    val updatedSheet = sheet.copy(
                        rowsRaw = rawRows.joinToString("\n"),
                        lastModifiedBy = user?.name ?: "Admin",
                        lastModifiedTimestamp = System.currentTimeMillis()
                    )
                    repository.updateSheet(updatedSheet)
                    _userMessage.value = "Cellule mise à jour !"
                }
            }
        }
    }

    fun addSpreadsheetRow(sheet: SpreadsheetTableEntity, rowCells: List<String>) {
        val user = _currentUser.value
        viewModelScope.launch {
            val existing = if (sheet.rowsRaw.isBlank()) "" else sheet.rowsRaw + "\n"
            val newRowLine = rowCells.joinToString("\t") { it.trim().ifEmpty { "-" } }
            val updatedSheet = sheet.copy(
                rowsRaw = existing + newRowLine,
                lastModifiedBy = user?.name ?: "Admin",
                lastModifiedTimestamp = System.currentTimeMillis()
            )
            repository.updateSheet(updatedSheet)
            _userMessage.value = "Ligne ajoutée au tableau Google Sheets."
        }
    }

    fun deleteSpreadsheetRow(sheet: SpreadsheetTableEntity, rowIndex: Int) {
        val user = _currentUser.value
        viewModelScope.launch {
            val rawRows = sheet.rowsRaw.split("\n").filter { it.isNotBlank() }.toMutableList()
            if (rowIndex in rawRows.indices) {
                rawRows.removeAt(rowIndex)
                val updatedSheet = sheet.copy(
                    rowsRaw = rawRows.joinToString("\n"),
                    lastModifiedBy = user?.name ?: "Admin",
                    lastModifiedTimestamp = System.currentTimeMillis()
                )
                repository.updateSheet(updatedSheet)
                _userMessage.value = "Ligne supprimée."
            }
        }
    }

    fun updateSpreadsheetUrl(sheet: SpreadsheetTableEntity, newUrl: String) {
        val user = _currentUser.value
        viewModelScope.launch {
            val updatedSheet = sheet.copy(
                externalGoogleSheetUrl = newUrl.trim(),
                lastModifiedBy = user?.name ?: "Admin",
                lastModifiedTimestamp = System.currentTimeMillis()
            )
            repository.updateSheet(updatedSheet)
            _userMessage.value = "Lien Google Sheets synchronisé !"
        }
    }

    fun refreshFirebaseStatus() {
        val context = getApplication<Application>()
        val isReady = FirebaseManager.isFirebaseReady(context)
        val defaultApp = FirebaseManager.getOrInitDefaultApp(context)
        val projectId = defaultApp?.options?.projectId ?: ""
        _firebaseState.value = _firebaseState.value.copy(
            isInitialized = isReady || defaultApp != null,
            projectId = projectId
        )
    }

    fun syncWithFirebase() {
        val context = getApplication<Application>()
        viewModelScope.launch {
            if (!FirebaseManager.isFirebaseReady(context) && FirebaseManager.getOrInitDefaultApp(context) == null) {
                _userMessage.value = "Firebase non configuré : téléchargez google-services.json dans le projet ou entrez vos clés."
                return@launch
            }
            _firebaseState.value = _firebaseState.value.copy(
                isSyncing = true,
                lastSyncStatus = "Synchronisation cloud en cours..."
            )
            val tasksResult = FirebaseManager.pushTasksToFirestore(context, allTasks.value)
            val postsResult = FirebaseManager.pushCommunityPostsToFirestore(context, allPosts.value)

            if (tasksResult.isSuccess && postsResult.isSuccess) {
                val tCount = tasksResult.getOrDefault(0)
                val pCount = postsResult.getOrDefault(0)
                _firebaseState.value = _firebaseState.value.copy(
                    isSyncing = false,
                    lastSyncStatus = "Synchronisé avec succès ($tCount tâches, $pCount publications)"
                )
                _userMessage.value = "Synchronisation Firebase Cloud réussie !"
            } else {
                val errorMsg = tasksResult.exceptionOrNull()?.message
                    ?: postsResult.exceptionOrNull()?.message
                    ?: "Erreur de connexion"
                _firebaseState.value = _firebaseState.value.copy(
                    isSyncing = false,
                    lastSyncStatus = "Échec : $errorMsg"
                )
                _userMessage.value = "Erreur Firebase : $errorMsg"
            }
        }
    }

    fun configureFirebaseManual(apiKey: String, projectId: String, appId: String) {
        val context = getApplication<Application>()
        val success = FirebaseManager.initializeWithManualConfig(context, apiKey, projectId, appId)
        if (success) {
            refreshFirebaseStatus()
            _userMessage.value = "Firebase initialisé manuellement avec succès !"
        } else {
            _userMessage.value = "Échec d'initialisation : vérifiez l'API Key, Project ID et App ID."
        }
    }
}
