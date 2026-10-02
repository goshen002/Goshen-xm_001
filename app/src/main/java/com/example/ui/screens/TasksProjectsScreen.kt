package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ProjectEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserEntity
import com.example.data.model.ProjectStatus
import com.example.data.model.RoleType
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.ui.components.RoleBadge
import com.example.ui.components.UserAvatar
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.util.WhatsAppHelper

@Composable
fun TasksProjectsScreen(
    tasks: List<TaskEntity>,
    projects: List<ProjectEntity>,
    users: List<UserEntity>,
    currentUser: UserEntity?,
    onUpdateTaskStatus: (TaskEntity, TaskStatus) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onCreateTask: (title: String, desc: String, assigneeId: String, projId: Long?, priority: TaskPriority, dueDate: String) -> Unit,
    onCreateProject: (name: String, desc: String, budget: String, start: String, target: String, sheetUrl: String) -> Unit,
    onOpenSheet: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var taskFilter by remember { mutableStateOf<String>("ALL") } // "ALL", "MY_TASKS", "TODO", "DONE"
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var showCreateProjectDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val canManage = currentUser?.roleType?.isAdministrator == true

    Column(modifier = modifier.fillMaxSize()) {
        // Tab Row: Tâches vs Projets
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tâches Quotidiennes (${tasks.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Élaboration Projets (${projects.size})", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedTabIndex == 0) {
                // Tasks Tab Content
                TasksContent(
                    tasks = tasks,
                    users = users,
                    currentUser = currentUser,
                    taskFilter = taskFilter,
                    onFilterChange = { taskFilter = it },
                    onUpdateTaskStatus = onUpdateTaskStatus,
                    onDeleteTask = onDeleteTask,
                    onSendWhatsApp = { task, assignee, assigner ->
                        val msg = WhatsAppHelper.formatTaskNotification(task, assignee, assigner)
                        val phone = assignee?.whatsappNumber ?: ""
                        WhatsAppHelper.openWhatsAppChat(context, phone, msg)
                    }
                )
            } else {
                // Projects Tab Content
                ProjectsContent(
                    projects = projects,
                    tasks = tasks,
                    users = users,
                    currentUser = currentUser,
                    onOpenSheet = onOpenSheet
                )
            }

            // Floating Action Button to Add Task or Project (Admin 1, 2, 3)
            if (canManage) {
                FloatingActionButton(
                    onClick = {
                        if (selectedTabIndex == 0) showCreateTaskDialog = true
                        else showCreateProjectDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 16.dp, end = 16.dp)
                        .testTag("add_task_project_fab"),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedTabIndex == 0) "Attribuer tâche" else "Élaborer projet",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Dialog: Create Task (Admins 1, 2, 3 only)
    if (showCreateTaskDialog) {
        CreateTaskDialog(
            users = users,
            projects = projects,
            currentUser = currentUser,
            onDismiss = { showCreateTaskDialog = false },
            onCreate = { title, desc, assigneeId, projId, prio, due ->
                onCreateTask(title, desc, assigneeId, projId, prio, due)
                showCreateTaskDialog = false
            }
        )
    }

    // Dialog: Create Project (Admins 1, 2, 3 only)
    if (showCreateProjectDialog) {
        CreateProjectDialog(
            currentUser = currentUser,
            onDismiss = { showCreateProjectDialog = false },
            onCreate = { name, desc, budget, start, target, sheetUrl ->
                onCreateProject(name, desc, budget, start, target, sheetUrl)
                showCreateProjectDialog = false
            }
        )
    }
}

@Composable
fun TasksContent(
    tasks: List<TaskEntity>,
    users: List<UserEntity>,
    currentUser: UserEntity?,
    taskFilter: String,
    onFilterChange: (String) -> Unit,
    onUpdateTaskStatus: (TaskEntity, TaskStatus) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onSendWhatsApp: (TaskEntity, UserEntity?, UserEntity?) -> Unit
) {
    val filteredTasks = remember(tasks, taskFilter, currentUser) {
        when (taskFilter) {
            "MY_TASKS" -> tasks.filter { it.assignedToUserId == currentUser?.id }
            "TODO" -> tasks.filter { it.status == TaskStatus.TODO }
            "IN_PROGRESS" -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
            "DONE" -> tasks.filter { it.status == TaskStatus.DONE }
            else -> tasks
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Admin attribution notice
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (currentUser?.roleType?.isAdministrator == true)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (currentUser?.roleType?.isAdministrator == true) Icons.Default.Assignment else Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentUser?.roleType?.isAdministrator == true)
                                "Attribution Hiérarchique Active"
                            else "Espace Collaborateur",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = if (currentUser?.roleType?.isAdministrator == true)
                                "En tant qu'Administrateur, vous pouvez affecter des tâches aux membres et les notifier sur WhatsApp."
                            else "Les tâches sont définies par les Administrateurs 1, 2 et 3. Vous pouvez changer l'état de vos tâches ci-dessous.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Filters
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = taskFilter == "ALL",
                        onClick = { onFilterChange("ALL") },
                        label = { Text("Toutes (${tasks.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = taskFilter == "MY_TASKS",
                        onClick = { onFilterChange("MY_TASKS") },
                        label = { Text("Mes tâches") }
                    )
                }
                item {
                    FilterChip(
                        selected = taskFilter == "TODO",
                        onClick = { onFilterChange("TODO") },
                        label = { Text("À faire") }
                    )
                }
                item {
                    FilterChip(
                        selected = taskFilter == "IN_PROGRESS",
                        onClick = { onFilterChange("IN_PROGRESS") },
                        label = { Text("En cours") }
                    )
                }
                item {
                    FilterChip(
                        selected = taskFilter == "DONE",
                        onClick = { onFilterChange("DONE") },
                        label = { Text("Terminées") }
                    )
                }
            }
        }

        if (filteredTasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aucune tâche correspondante.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredTasks, key = { it.id }) { task ->
                val assignee = users.firstOrNull { it.id == task.assignedToUserId }
                val assigner = users.firstOrNull { it.id == task.assignedByUserId }

                TaskCard(
                    task = task,
                    assignee = assignee,
                    assigner = assigner,
                    canDelete = currentUser?.roleType?.isAdministrator == true,
                    onStatusChange = { newStatus -> onUpdateTaskStatus(task, newStatus) },
                    onDelete = { onDeleteTask(task) },
                    onSendWhatsApp = { onSendWhatsApp(task, assignee, assigner) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: TaskEntity,
    assignee: UserEntity?,
    assigner: UserEntity?,
    canDelete: Boolean,
    onStatusChange: (TaskStatus) -> Unit,
    onDelete: () -> Unit,
    onSendWhatsApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var statusExpanded by remember { mutableStateOf(false) }

    val priorityColor = when (task.priority) {
        TaskPriority.URGENT -> Color(0xFFD32F2F)
        TaskPriority.HIGH -> Color(0xFFF57C00)
        TaskPriority.MEDIUM -> Color(0xFF1976D2)
        TaskPriority.LOW -> Color(0xFF388E3C)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Priority + Due Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = priorityColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Priorité : ${task.priority.label}",
                        color = priorityColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Échéance : ${task.dueDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (canDelete) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = "Supprimer",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Assigned To & By Information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (assignee != null) {
                    UserAvatar(name = assignee.name, roleType = assignee.roleType, size = 32)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Assigné à : ${assignee.name}",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${assignee.position} • WA: ${assignee.whatsappNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = WhatsAppDark
                        )
                    }
                } else {
                    Text(
                        text = "Non assigné",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (assigner != null) {
                    RoleBadge(roleType = assigner.roleType)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Selector and WhatsApp Dispatch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        color = when (task.status) {
                            TaskStatus.DONE -> Color(0xFFE8F5E9)
                            TaskStatus.IN_PROGRESS -> Color(0xFFE3F2FD)
                            TaskStatus.REVIEW -> Color(0xFFFFF3E0)
                            TaskStatus.TODO -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Statut : ${task.status.label}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = when (task.status) {
                                    TaskStatus.DONE -> Color(0xFF2E7D32)
                                    TaskStatus.IN_PROGRESS -> Color(0xFF1565C0)
                                    TaskStatus.REVIEW -> Color(0xFFEF6C00)
                                    TaskStatus.TODO -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        TaskStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.label) },
                                onClick = {
                                    onStatusChange(status)
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Send Brief to WhatsApp
                Button(
                    onClick = onSendWhatsApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Notifier sur WhatsApp",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Brief WhatsApp",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectsContent(
    projects: List<ProjectEntity>,
    tasks: List<TaskEntity>,
    users: List<UserEntity>,
    currentUser: UserEntity?,
    onOpenSheet: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Élaboration Stratégique des Projets",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Rôle réservé aux Admins 1, 2 et 3 : planification des budgets, objectifs, jalons et connexion aux feuilles Google Sheets.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (projects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aucun projet en cours d'élaboration.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(projects, key = { it.id }) { project ->
                val leadAdmin = users.firstOrNull { it.id == project.leadAdminId }
                val projectTasks = tasks.filter { it.projectId == project.id }
                val completedTasksCount = projectTasks.count { it.status == TaskStatus.DONE }
                val progress = if (projectTasks.isNotEmpty()) completedTasksCount.toFloat() / projectTasks.size else 0f

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = project.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = project.status.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = project.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Details: Budget & Dates & Lead Admin
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Budget Alloué",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = project.budget,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column {
                                Text(
                                    text = "Échéance Finale",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = project.targetDate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            if (leadAdmin != null) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Responsable",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = leadAdmin.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress bar based on tasks
                        Text(
                            text = "Avancement des tâches : $completedTasksCount/${projectTasks.size} terminées (${(progress * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        if (project.googleSheetUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onOpenSheet(project.googleSheetUrl) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0F9D58), // Google Sheets Green
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Consulter le Google Sheets lié au projet", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    users: List<UserEntity>,
    projects: List<ProjectEntity>,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, assigneeId: String, projId: Long?, prio: TaskPriority, due: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedAssignee by remember { mutableStateOf(users.firstOrNull { it.roleType == RoleType.MEMBER } ?: users.firstOrNull()) }
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var dueDate by remember { mutableStateOf("15 Oct 2026") }

    var assigneeExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }
    var projectExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Attribuer une tâche d'entreprise", fontWeight = FontWeight.Bold)
                if (currentUser != null) {
                    Text(
                        text = "Émise par : ${currentUser.name} (${currentUser.roleType.title})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Titre de la tâche") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Assignee Dropdown
                ExposedDropdownMenuBox(
                    expanded = assigneeExpanded,
                    onExpandedChange = { assigneeExpanded = !assigneeExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedAssignee?.let { "${it.name} (${it.position})" } ?: "Choisir le collaborateur",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assigner à (Membre de l'équipe)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = assigneeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = assigneeExpanded,
                        onDismissRequest = { assigneeExpanded = false }
                    ) {
                        users.forEach { user ->
                            DropdownMenuItem(
                                text = { Text("${user.name} • ${user.position} (${user.roleType.badgeSymbol})") },
                                onClick = {
                                    selectedAssignee = user
                                    assigneeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Project Dropdown
                ExposedDropdownMenuBox(
                    expanded = projectExpanded,
                    onExpandedChange = { projectExpanded = !projectExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedProject?.name ?: "Aucun projet spécifique",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Projet associé") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = projectExpanded,
                        onDismissRequest = { projectExpanded = false }
                    ) {
                        projects.forEach { proj ->
                            DropdownMenuItem(
                                text = { Text(proj.name) },
                                onClick = {
                                    selectedProject = proj
                                    projectExpanded = false
                                }
                            )
                        }
                    }
                }

                // Priority Dropdown
                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedPriority.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Niveau de priorité") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        TaskPriority.values().forEach { prio ->
                            DropdownMenuItem(
                                text = { Text(prio.label) },
                                onClick = {
                                    selectedPriority = prio
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Date d'échéance (ex: 20 Oct 2026)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Instructions & Consignes détaillées") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val assigneeId = selectedAssignee?.id ?: ""
                    if (title.isNotBlank() && assigneeId.isNotBlank()) {
                        onCreate(title, desc, assigneeId, selectedProject?.id, selectedPriority, dueDate)
                    }
                },
                enabled = title.isNotBlank() && selectedAssignee != null
            ) {
                Text("Attribuer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun CreateProjectDialog(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onCreate: (name: String, desc: String, budget: String, start: String, target: String, sheetUrl: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("01 Oct 2026") }
    var targetDate by remember { mutableStateOf("30 Nov 2026") }
    var sheetUrl by remember { mutableStateOf("https://docs.google.com/spreadsheets/d/...") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Élaborer un nouveau projet", fontWeight = FontWeight.Bold)
                Text(
                    text = "Réservé aux Administrateurs 1, 2 et 3",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du projet") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget prévisionnel (ex: 25 000 $)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Date de livraison visée") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sheetUrl,
                    onValueChange = { sheetUrl = it },
                    label = { Text("Lien Google Sheets du projet") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Objectifs & Périmètre") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, desc, budget, startDate, targetDate, sheetUrl)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Créer le projet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
