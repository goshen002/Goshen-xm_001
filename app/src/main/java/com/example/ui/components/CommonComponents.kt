package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserEntity
import com.example.data.model.RoleType
import com.example.ui.theme.Admin1Gold
import com.example.ui.theme.Admin2Blue
import com.example.ui.theme.Admin3Purple
import com.example.ui.theme.MemberTeal
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.util.WhatsAppHelper

@Composable
fun RoleBadge(roleType: RoleType, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (roleType) {
        RoleType.ADMIN_1 -> Triple(Admin1Gold.copy(alpha = 0.18f), Color(0xFFB8860B), "Admin 1 • Direction")
        RoleType.ADMIN_2 -> Triple(Admin2Blue.copy(alpha = 0.18f), Color(0xFF1976D2), "Admin 2 • Superviseur")
        RoleType.ADMIN_3 -> Triple(Admin3Purple.copy(alpha = 0.18f), Color(0xFF7B1FA2), "Admin 3 • Coordinateur")
        RoleType.MEMBER -> Triple(MemberTeal.copy(alpha = 0.15f), Color(0xFF00796B), "Collaborateur")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${roleType.badgeSymbol} $label",
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UserAvatar(
    name: String,
    roleType: RoleType,
    modifier: Modifier = Modifier,
    size: Int = 42
) {
    val baseColor = when (roleType) {
        RoleType.ADMIN_1 -> Admin1Gold
        RoleType.ADMIN_2 -> Admin2Blue
        RoleType.ADMIN_3 -> Admin3Purple
        RoleType.MEMBER -> MemberTeal
    }
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(baseColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (initials.isNotEmpty()) initials else "U",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.38f).sp
        )
    }
}

@Composable
fun WhatsAppActionButton(
    phoneNumber: String,
    prefilledMessage: String = "",
    label: String = "WhatsApp",
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val context = LocalContext.current
    if (compact) {
        IconButton(
            onClick = {
                WhatsAppHelper.openWhatsAppChat(context, phoneNumber, prefilledMessage)
            },
            modifier = modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(WhatsAppGreen.copy(alpha = 0.15f))
                .testTag("whatsapp_action_button")
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Contacter sur WhatsApp",
                tint = WhatsAppDark,
                modifier = Modifier.size(18.dp)
            )
        }
    } else {
        Button(
            onClick = {
                WhatsAppHelper.openWhatsAppChat(context, phoneNumber, prefilledMessage)
            },
            modifier = modifier.testTag("whatsapp_action_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = WhatsAppGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ProfileSwitcherDialog(
    users: List<UserEntity>,
    currentUser: UserEntity?,
    onSelectUser: (UserEntity) -> Unit,
    onAddNewProfile: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Changer de profil actif")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Testez la plateforme avec différents rôles hiérarchiques (Admins 1, 2, 3 ou Membres) ou connectez votre propre compte WhatsApp :",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(users) { user ->
                        val isSelected = user.id == currentUser?.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onSelectUser(user)
                                    onDismiss()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserAvatar(name = user.name, roleType = user.roleType, size = 36)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.name,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = user.position,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "WhatsApp: ${user.whatsappNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WhatsAppDark
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Actif",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onAddNewProfile()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Connecter un nouveau profil WhatsApp")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProfileDialog(
    onRegister: (name: String, phone: String, role: RoleType, position: String, department: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+") }
    var selectedRole by remember { mutableStateOf(RoleType.MEMBER) }
    var position by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Opérations") }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Connexion Compte WhatsApp", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Renseignez vos coordonnées d'entreprise et votre numéro WhatsApp pour recevoir vos tâches et communiquer avec le collectif.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom complet") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Numéro WhatsApp (+243..., +33...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = roleDropdownExpanded,
                    onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedRole.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rôle Hiérarchique") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = roleDropdownExpanded,
                        onDismissRequest = { roleDropdownExpanded = false }
                    ) {
                        RoleType.values().forEach { role ->
                            DropdownMenuItem(
                                text = { Text("${role.badgeSymbol} ${role.title}") },
                                onClick = {
                                    selectedRole = role
                                    roleDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Poste dans l'entreprise") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Département") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.length > 4) {
                        onRegister(name, phone, selectedRole, position.ifBlank { "Collaborateur" }, department)
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank() && phone.length > 4
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
