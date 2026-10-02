package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.NewProfileDialog
import com.example.ui.components.ProfileSwitcherDialog
import com.example.ui.components.RoleBadge
import com.example.ui.components.UserAvatar
import com.example.ui.components.WhatsAppActionButton
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.HierarchyScreen
import com.example.ui.screens.ProgrammeScreen
import com.example.ui.screens.SpreadsheetScreen
import com.example.ui.screens.TasksProjectsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.EnterpriseViewModel
import com.example.util.WhatsAppHelper

enum class AppNavDestination(val label: String, val icon: ImageVector) {
    COMMUNITY("Forum", Icons.Default.Forum),
    TASKS("Tâches", Icons.Default.Assignment),
    PROGRAMME("Programme", Icons.Default.CalendarMonth),
    SPREADSHEET("Tableur", Icons.Default.TableChart),
    HIERARCHY("Équipe", Icons.Default.Business)
}

class MainActivity : ComponentActivity() {
    private val viewModel: EnterpriseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: EnterpriseViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var currentDestination by remember { mutableStateOf(AppNavDestination.COMMUNITY) }
    var showProfileSwitcher by remember { mutableStateOf(false) }
    var showNewProfileDialog by remember { mutableStateOf(false) }

    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val projects by viewModel.allProjects.collectAsStateWithLifecycle()
    val posts by viewModel.allPosts.collectAsStateWithLifecycle()
    val events by viewModel.allEvents.collectAsStateWithLifecycle()
    val sheets by viewModel.allSheets.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeReplies by viewModel.currentPostReplies.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val firebaseState by viewModel.firebaseState.collectAsStateWithLifecycle()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Back button handling: if not on Community screen, back returns to Community
    BackHandler(enabled = currentDestination != AppNavDestination.COMMUNITY) {
        currentDestination = AppNavDestination.COMMUNITY
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_goshen_logo_1790955947247),
                            contentDescription = "Logo Goshen XM",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showProfileSwitcher = true }
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showProfileSwitcher = true }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Goshen XM",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = WhatsAppGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "WhatsApp Workspace",
                                        color = WhatsAppDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            if (currentUser != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${currentUser?.name} • ${currentUser?.roleType?.badgeSymbol}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Changer",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        // WhatsApp Broadcast / Help
                        if (currentUser != null) {
                            UserAvatar(
                                name = currentUser!!.name,
                                roleType = currentUser!!.roleType,
                                size = 34,
                                modifier = Modifier.clickable { showProfileSwitcher = true }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        WhatsAppActionButton(
                            phoneNumber = "",
                            prefilledMessage = "Bonjour l'équipe Goshen XM,",
                            compact = true
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                AppNavDestination.values().forEach { destination ->
                    val isSelected = destination == currentDestination
                    val badgeCount = when (destination) {
                        AppNavDestination.TASKS -> tasks.count { it.assignedToUserId == currentUser?.id }
                        AppNavDestination.COMMUNITY -> posts.size
                        else -> 0
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}"),
                        icon = {
                            if (badgeCount > 0 && destination == AppNavDestination.TASKS) {
                                BadgedBox(badge = { Badge { Text("$badgeCount") } }) {
                                    Icon(destination.icon, contentDescription = destination.label)
                                }
                            } else {
                                Icon(destination.icon, contentDescription = destination.label)
                            }
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppNavDestination.COMMUNITY -> {
                    CommunityScreen(
                        posts = posts,
                        users = users,
                        currentUser = currentUser,
                        activePostReplies = activeReplies,
                        onSelectPost = { postId -> viewModel.selectPostForReplies(postId) },
                        onLikePost = { post -> viewModel.likePost(post) },
                        onAddReply = { postId, reply -> viewModel.addReplyToPost(postId, reply) },
                        onCreatePost = { title, content, cat -> viewModel.createPost(title, content, cat) }
                    )
                }

                AppNavDestination.TASKS -> {
                    TasksProjectsScreen(
                        tasks = tasks,
                        projects = projects,
                        users = users,
                        currentUser = currentUser,
                        onUpdateTaskStatus = { task, status -> viewModel.updateTaskStatus(task, status) },
                        onDeleteTask = { task -> viewModel.deleteTask(task) },
                        onCreateTask = { title, desc, assigneeId, projId, prio, due ->
                            viewModel.createTask(title, desc, assigneeId, projId, prio, due)
                        },
                        onCreateProject = { name, desc, budget, start, target, sheetUrl ->
                            viewModel.createProject(name, desc, budget, start, target, sheetUrl)
                        },
                        onOpenSheet = { url ->
                            if (url.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        }
                    )
                }

                AppNavDestination.PROGRAMME -> {
                    ProgrammeScreen(
                        events = events,
                        users = users,
                        currentUser = currentUser,
                        onCreateEvent = { title, desc, date, time, loc, cat ->
                            viewModel.createProgrammeEvent(title, desc, date, time, loc, cat)
                        },
                        onDeleteEvent = { ev -> viewModel.deleteEvent(ev) }
                    )
                }

                AppNavDestination.SPREADSHEET -> {
                    SpreadsheetScreen(
                        sheets = sheets,
                        currentUser = currentUser,
                        onUpdateCell = { sheet, r, c, valStr -> viewModel.updateSpreadsheetCell(sheet, r, c, valStr) },
                        onAddRow = { sheet, cells -> viewModel.addSpreadsheetRow(sheet, cells) },
                        onDeleteRow = { sheet, r -> viewModel.deleteSpreadsheetRow(sheet, r) },
                        onCreateSheet = { title, desc, cols, url -> viewModel.createSpreadsheet(title, desc, cols, url) },
                        onUpdateSheetUrl = { sheet, url -> viewModel.updateSpreadsheetUrl(sheet, url) }
                    )
                }

                AppNavDestination.HIERARCHY -> {
                    HierarchyScreen(
                        users = users,
                        tasks = tasks,
                        currentUser = currentUser,
                        firebaseState = firebaseState,
                        onSyncFirebaseClick = { viewModel.syncWithFirebase() },
                        onConfigureFirebase = { apiKey, projId, appId ->
                            viewModel.configureFirebaseManual(apiKey, projId, appId)
                        },
                        onSwitchUserClick = { showProfileSwitcher = true },
                        onAddNewProfileClick = { showNewProfileDialog = true }
                    )
                }
            }
        }
    }

    // Switch Profile Dialog
    if (showProfileSwitcher) {
        ProfileSwitcherDialog(
            users = users,
            currentUser = currentUser,
            onSelectUser = { user -> viewModel.selectUser(user) },
            onAddNewProfile = { showNewProfileDialog = true },
            onDismiss = { showProfileSwitcher = false }
        )
    }

    // New Profile / WhatsApp Register Dialog
    if (showNewProfileDialog) {
        NewProfileDialog(
            onRegister = { name, phone, role, position, dept ->
                viewModel.registerOrUpdateWhatsAppProfile(name, phone, role, position, dept)
                showNewProfileDialog = false
            },
            onDismiss = { showNewProfileDialog = false }
        )
    }
}
