package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserEntity
import com.example.data.model.RoleType
import com.example.data.model.TaskStatus
import com.example.data.firebase.FirebaseConnectionState
import com.example.ui.components.FirebaseConfigDialog
import com.example.ui.components.FirebaseConnectionCard
import com.example.ui.components.RoleBadge
import com.example.ui.components.UserAvatar
import com.example.ui.components.WhatsAppActionButton
import com.example.ui.theme.Admin1Gold
import com.example.ui.theme.Admin2Blue
import com.example.ui.theme.Admin3Purple
import com.example.ui.theme.MemberTeal
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.util.WhatsAppHelper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun HierarchyScreen(
    users: List<UserEntity>,
    tasks: List<TaskEntity>,
    currentUser: UserEntity?,
    firebaseState: FirebaseConnectionState = FirebaseConnectionState(),
    onSyncFirebaseClick: () -> Unit = {},
    onConfigureFirebase: (String, String, String) -> Unit = { _, _, _ -> },
    onSwitchUserClick: () -> Unit,
    onAddNewProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFirebaseDialog by remember { mutableStateOf(false) }

    val admin1 = users.firstOrNull { it.roleType == RoleType.ADMIN_1 }
    val admin2 = users.firstOrNull { it.roleType == RoleType.ADMIN_2 }
    val admin3 = users.firstOrNull { it.roleType == RoleType.ADMIN_3 }
    val members = users.filter { it.roleType == RoleType.MEMBER }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Active Profile Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "VOTRE COMPTE ACTIF",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (currentUser != null) {
                                UserAvatar(name = currentUser.name, roleType = currentUser.roleType, size = 46)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUser.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = currentUser.position,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "WhatsApp : ${currentUser.whatsappNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WhatsAppDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                RoleBadge(roleType = currentUser.roleType)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onSwitchUserClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Changer de profil", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onAddNewProfileClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nouveau compte", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Firebase Cloud Connection Card
            item {
                FirebaseConnectionCard(
                    firebaseState = firebaseState,
                    onSyncClick = onSyncFirebaseClick,
                    onConfigureClick = { showFirebaseDialog = true }
                )
            }

        // Hierarchy Title Banner
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Organigramme & Rôles de l'Entreprise",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Structure hiérarchique : les 3 Administrateurs délèguent et gèrent les projets, tâches et tableaux pour les postes de l'équipe.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 1: The 3 Administrators
        item {
            Text(
                text = "CONSEIL D'ADMINISTRATION (3 ADMINISTRATEURS)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Admin 1
        if (admin1 != null) {
            item {
                AdminHierarchyCard(
                    user = admin1,
                    roleTitle = "PREMIER ADMINISTRATEUR • DIRECTION GÉNÉRALE",
                    roleDescription = "Rôle suprême : définition des orientations stratégiques, attribution des tâches transversales, arbitrage des budgets et validation générale.",
                    accentColor = Admin1Gold,
                    isCurrent = currentUser?.id == admin1.id,
                    onWhatsAppClick = {
                        val msg = "Bonjour ${admin1.name}, je vous contacte depuis la plateforme d'entreprise Goshen XM."
                        WhatsAppHelper.openWhatsAppChat(context, admin1.whatsappNumber, msg)
                    }
                )
            }
        }

        // Admin 2
        if (admin2 != null) {
            item {
                AdminHierarchyCard(
                    user = admin2,
                    roleTitle = "DEUXIÈME ADMINISTRATEUR • SUPERVISEUR GÉNÉRAL",
                    roleDescription = "Supervision opérationnelle : suivi de l'avancement des tâches quotidiennes, attribution aux chefs de pôles et coordination des équipes.",
                    accentColor = Admin2Blue,
                    isCurrent = currentUser?.id == admin2.id,
                    onWhatsAppClick = {
                        val msg = "Bonjour ${admin2.name}, je vous contacte pour un point de supervision Goshen XM."
                        WhatsAppHelper.openWhatsAppChat(context, admin2.whatsappNumber, msg)
                    }
                )
            }
        }

        // Admin 3
        if (admin3 != null) {
            item {
                AdminHierarchyCard(
                    user = admin3,
                    roleTitle = "TROISIÈME ADMINISTRATEUR • COORDINATEUR PROJETS & GOOGLE SHEETS",
                    roleDescription = "Coordination technique et données : élaboration des projets, tenue à jour des tableaux de bord Google Sheets et planification du programme.",
                    accentColor = Admin3Purple,
                    isCurrent = currentUser?.id == admin3.id,
                    onWhatsAppClick = {
                        val msg = "Bonjour ${admin3.name}, je vous contacte concernant les projets et tableaux Goshen XM."
                        WhatsAppHelper.openWhatsAppChat(context, admin3.whatsappNumber, msg)
                    }
                )
            }
        }

        // Section 2: Team Members & Positions
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "COLLABORATEURS & POSTES DE L'ÉQUIPE (${members.size})",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(members, key = { it.id }) { member ->
            val memberTasks = tasks.filter { it.assignedToUserId == member.id }
            val activeTasksCount = memberTasks.count { it.status != TaskStatus.DONE }

            MemberHierarchyCard(
                user = member,
                activeTasksCount = activeTasksCount,
                isCurrent = currentUser?.id == member.id,
                onWhatsAppClick = {
                    val msg = "Bonjour ${member.name}, je vous contacte concernant votre poste de ${member.position} sur Goshen XM."
                    WhatsAppHelper.openWhatsAppChat(context, member.whatsappNumber, msg)
                }
            )
        }
    }

    if (showFirebaseDialog) {
        FirebaseConfigDialog(
            firebaseState = firebaseState,
            onManualConnect = onConfigureFirebase,
            onDismiss = { showFirebaseDialog = false }
        )
    }
    }
}

@Composable
fun AdminHierarchyCard(
    user: UserEntity,
    roleTitle: String,
    roleDescription: String,
    accentColor: Color,
    isCurrent: Boolean,
    onWhatsAppClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = if (isCurrent) accentColor else accentColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(name = user.name, roleType = user.roleType, size = 44)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = roleTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${user.position} • ${user.department}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RoleBadge(roleType = user.roleType)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = roleDescription,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WhatsApp : ${user.whatsappNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WhatsAppDark,
                    fontWeight = FontWeight.SemiBold
                )

                Button(
                    onClick = onWhatsAppClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Contacter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MemberHierarchyCard(
    user: UserEntity,
    activeTasksCount: Int,
    isCurrent: Boolean,
    onWhatsAppClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isCurrent) 2.dp else 0.5.dp,
                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(name = user.name, roleType = user.roleType, size = 42)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.position,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Département : ${user.department}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = if (activeTasksCount > 0) Color(0xFFE65100) else Color(0xFF2E7D32),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (activeTasksCount > 0) "$activeTasksCount tâche(s) en cours" else "À jour",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (activeTasksCount > 0) Color(0xFFE65100) else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            WhatsAppActionButton(
                phoneNumber = user.whatsappNumber,
                prefilledMessage = "Bonjour ${user.name},",
                compact = true
            )
        }
    }
}
