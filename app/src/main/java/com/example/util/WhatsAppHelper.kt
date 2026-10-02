package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.entity.CommunityPostEntity
import com.example.data.entity.ProgrammeEventEntity
import com.example.data.entity.SpreadsheetTableEntity
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserEntity
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WhatsAppHelper {

    fun cleanPhoneNumber(raw: String): String {
        return raw.replace("+", "")
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .trim()
    }

    fun openWhatsAppChat(context: Context, phoneNumber: String, message: String = "") {
        val cleanNumber = cleanPhoneNumber(phoneNumber)
        try {
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val uriString = if (cleanNumber.isNotEmpty()) {
                "https://wa.me/$cleanNumber?text=$encodedMessage"
            } else {
                "https://wa.me/?text=$encodedMessage"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            // Fallback to generic share
            shareGenericText(context, message)
        }
    }

    fun shareGenericText(context: Context, message: String, title: String = "Partager via WhatsApp") {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                setPackage("com.whatsapp")
            }
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(sendIntent)
        } catch (_: Exception) {
            // General share chooser
            val chooserIntent = Intent.createChooser(
                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                },
                title
            )
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        }
    }

    fun formatTaskNotification(
        task: TaskEntity,
        assignee: UserEntity?,
        assigner: UserEntity?
    ): String {
        val assigneeName = assignee?.name ?: "Collaborateur"
        val assigneePosition = assignee?.position ?: ""
        val assignerName = assigner?.name ?: "Administration"
        val assignerRole = assigner?.roleType?.title ?: "Admin"

        return """
            📢 *GOSHEN XM - ATTRIBUTION DE TÂCHE*
            ━━━━━━━━━━━━━━━━━━━━━
            📌 *Tâche :* ${task.title}
            👤 *Assigné à :* $assigneeName ${if (assigneePosition.isNotEmpty()) "($assigneePosition)" else ""}
            👑 *Assigné par :* $assignerName ($assignerRole)
            ⚡ *Priorité :* ${task.priority.label}
            📅 *Date d'échéance :* ${task.dueDate}
            📊 *Statut actuel :* ${task.status.label}
            
            📝 *Description des consignes :*
            ${task.description}
            
            ━━━━━━━━━━━━━━━━━━━━━
            _Merci de confirmer la prise en charge et de mettre à jour le statut sur la plateforme Goshen XM._
        """.trimIndent()
    }

    fun formatPostShare(post: CommunityPostEntity, author: UserEntity?): String {
        val authorName = author?.name ?: "Membre Goshen XM"
        val authorRole = author?.roleType?.title ?: ""

        return """
            💬 *GOSHEN XM - SUJET DE COMMUNAUTÉ ENTREPRISE*
            🏷️ *Catégorie :* ${post.category.label}
            ━━━━━━━━━━━━━━━━━━━━━
            *${post.title}*
            👤 _Publié par $authorName ($authorRole)_
            
            ${post.content}
            
            ━━━━━━━━━━━━━━━━━━━━━
            _Rejoignez la discussion sur l'application Goshen XM._
        """.trimIndent()
    }

    fun formatProgrammeAgenda(events: List<ProgrammeEventEntity>): String {
        val builder = StringBuilder()
        builder.append("🗓️ *GOSHEN XM - PROGRAMME & ACTIVITÉS DE L'ENTREPRISE*\n")
        builder.append("━━━━━━━━━━━━━━━━━━━━━\n\n")

        if (events.isEmpty()) {
            builder.append("Aucun événement programmé pour le moment.\n")
        } else {
            events.forEachIndexed { index, ev ->
                builder.append("${index + 1}. *${ev.title}*\n")
                builder.append("   ⏰ *Horaire :* ${ev.date} à ${ev.time}\n")
                builder.append("   🏷️ *Type :* ${ev.category.label}\n")
                builder.append("   📍 *Lieu :* ${ev.locationOrLink}\n")
                if (ev.description.isNotBlank()) {
                    builder.append("   ℹ️ ${ev.description}\n")
                }
                builder.append("\n")
            }
        }

        builder.append("━━━━━━━━━━━━━━━━━━━━━\n")
        builder.append("_Programme officiel synchronisé sur Goshen XM Workspace._")
        return builder.toString()
    }

    fun formatSpreadsheetExport(sheet: SpreadsheetTableEntity): String {
        val builder = StringBuilder()
        builder.append("📊 *TABLEAU GOOGLE SHEETS - ${sheet.title.uppercase()}*\n")
        if (sheet.description.isNotBlank()) {
            builder.append("ℹ️ _${sheet.description}_\n")
        }
        builder.append("👤 Modifié par : ${sheet.lastModifiedBy}\n")
        builder.append("━━━━━━━━━━━━━━━━━━━━━\n\n")

        val columns = sheet.columnsRaw.split(",").map { it.trim() }
        val rows = sheet.rowsRaw.split("\n").filter { it.isNotBlank() }

        rows.forEachIndexed { rIdx, rowLine ->
            val cells = rowLine.split("\t").ifEmpty { rowLine.split(",") }.map { it.trim() }
            builder.append("🔹 *Ligne #${rIdx + 1}*\n")
            columns.forEachIndexed { cIdx, colName ->
                val cellVal = cells.getOrNull(cIdx) ?: "-"
                builder.append("   • $colName : *$cellVal*\n")
            }
            builder.append("\n")
        }

        if (sheet.externalGoogleSheetUrl.isNotBlank()) {
            builder.append("🔗 *Lien Google Sheets en direct :*\n${sheet.externalGoogleSheetUrl}\n")
        }

        builder.append("━━━━━━━━━━━━━━━━━━━━━\n")
        builder.append("_Exporté depuis l'application d'entreprise Goshen XM._")
        return builder.toString()
    }
}
