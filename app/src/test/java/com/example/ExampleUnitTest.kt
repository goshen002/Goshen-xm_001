package com.example

import com.example.data.entity.Task
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserEntity
import com.example.data.model.RoleType
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import com.example.util.WhatsAppHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCleanPhoneNumber() {
        assertEquals("33612345678", WhatsAppHelper.cleanPhoneNumber("+33 6 12 34 56 78"))
        assertEquals("243812345678", WhatsAppHelper.cleanPhoneNumber("+243 (81) 234-5678"))
    }

    @Test
    fun testHierarchyRolesPermissions() {
        assertTrue(RoleType.ADMIN_1.canManageTasksAndProjects())
        assertTrue(RoleType.ADMIN_2.canManageTasksAndProjects())
        assertTrue(RoleType.ADMIN_3.canManageTasksAndProjects())
        assertFalse(RoleType.MEMBER.canManageTasksAndProjects())

        assertTrue(RoleType.ADMIN_1.isAdministrator)
        assertTrue(RoleType.ADMIN_2.isAdministrator)
        assertTrue(RoleType.ADMIN_3.isAdministrator)
        assertFalse(RoleType.MEMBER.isAdministrator)
    }

    @Test
    fun testTaskEntityFields() {
        val task = Task(
            title = "Préparer rapport financier",
            description = "Consigner les bilans dans le tableau Google Sheets",
            assignedUser = "member_3",
            deadline = "15 Oct 2026",
            completionStatus = "À faire",
            isCompleted = false
        )

        assertEquals("Préparer rapport financier", task.title)
        assertEquals("Consigner les bilans dans le tableau Google Sheets", task.description)
        assertEquals("member_3", task.assignedUser)
        assertEquals("15 Oct 2026", task.deadline)
        assertEquals("À faire", task.completionStatus)
        assertFalse(task.isCompleted)
    }

    @Test
    fun testTaskNotificationFormatting() {
        val admin1 = UserEntity(
            id = "admin_1",
            name = "Henock B.",
            roleType = RoleType.ADMIN_1,
            position = "Directeur Général",
            whatsappNumber = "+243812345678"
        )
        val member = UserEntity(
            id = "member_1",
            name = "Amina Diallo",
            roleType = RoleType.MEMBER,
            position = "Lead Développeuse",
            whatsappNumber = "+221771234567"
        )
        val task = TaskEntity(
            id = 1,
            title = "Déploiement Plateforme",
            description = "Intégration du webhook WhatsApp",
            assignedToUserId = "member_1",
            assignedByUserId = "admin_1",
            priority = TaskPriority.URGENT,
            status = TaskStatus.TODO,
            dueDate = "10 Oct 2026"
        )

        val formatted = WhatsAppHelper.formatTaskNotification(task, member, admin1)
        assertTrue(formatted.contains("GOSHEN XM"))
        assertTrue(formatted.contains("Déploiement Plateforme"))
        assertTrue(formatted.contains("Amina Diallo"))
        assertTrue(formatted.contains("Henock B."))
        assertTrue(formatted.contains("Urgente"))
    }
}
