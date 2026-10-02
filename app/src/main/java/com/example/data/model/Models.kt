package com.example.data.model

enum class RoleType(
    val title: String,
    val hierarchyLevel: Int,
    val isAdministrator: Boolean,
    val badgeSymbol: String
) {
    ADMIN_1(
        title = "Directeur Général (Admin 1)",
        hierarchyLevel = 1,
        isAdministrator = true,
        badgeSymbol = "👑"
    ),
    ADMIN_2(
        title = "Superviseur Général (Admin 2)",
        hierarchyLevel = 2,
        isAdministrator = true,
        badgeSymbol = "🛡️"
    ),
    ADMIN_3(
        title = "Coordinateur Projets (Admin 3)",
        hierarchyLevel = 3,
        isAdministrator = true,
        badgeSymbol = "📋"
    ),
    MEMBER(
        title = "Collaborateur / Membre",
        hierarchyLevel = 4,
        isAdministrator = false,
        badgeSymbol = "💼"
    );

    fun canManageTasksAndProjects(): Boolean = isAdministrator
    fun canEditSpreadsheets(): Boolean = isAdministrator
}

enum class TaskStatus(val label: String) {
    TODO("À faire"),
    IN_PROGRESS("En cours"),
    REVIEW("En revue"),
    DONE("Terminé")
}

enum class TaskPriority(val label: String) {
    LOW("Basse"),
    MEDIUM("Normale"),
    HIGH("Haute"),
    URGENT("Urgente")
}

enum class ProjectStatus(val label: String) {
    PLANNING("Élaboration"),
    ACTIVE("En cours"),
    ON_HOLD("En pause"),
    COMPLETED("Terminé")
}

enum class PostCategory(val label: String, val iconName: String) {
    ANNOUNCEMENT("Annonces Officielles", "Campaign"),
    PROJECT("Projets d'Entreprise", "Assignment"),
    OPERATIONS("Opérations & Tâches", "Work"),
    IDEA("Idées & Propositions", "Lightbulb"),
    GENERAL("Échanges Généraux", "Forum")
}

enum class EventCategory(val label: String) {
    MEETING("Réunion d'équipe"),
    DAILY_STANDUP("Point quotidien"),
    DEADLINE("Échéance livrable"),
    TRAINING("Formation / Atelier"),
    COMPANY_EVENT("Événement collectif")
}
