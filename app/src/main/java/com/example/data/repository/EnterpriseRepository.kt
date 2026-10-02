package com.example.data.repository

import com.example.data.dao.CommunityDao
import com.example.data.dao.ProgrammeDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.SpreadsheetDao
import com.example.data.dao.TaskDao
import com.example.data.dao.UserDao
import com.example.data.entity.CommunityPostEntity
import com.example.data.entity.CommunityReplyEntity
import com.example.data.entity.ProgrammeEventEntity
import com.example.data.entity.ProjectEntity
import com.example.data.entity.SpreadsheetTableEntity
import com.example.data.entity.Task
import com.example.data.entity.TaskEntity
import com.example.data.entity.UserEntity
import com.example.data.model.EventCategory
import com.example.data.model.PostCategory
import com.example.data.model.ProjectStatus
import com.example.data.model.RoleType
import com.example.data.model.TaskPriority
import com.example.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EnterpriseRepository(
    private val userDao: UserDao,
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao,
    private val communityDao: CommunityDao,
    private val programmeDao: ProgrammeDao,
    private val spreadsheetDao: SpreadsheetDao
) {
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val allPosts: Flow<List<CommunityPostEntity>> = communityDao.getAllPosts()
    val allEvents: Flow<List<ProgrammeEventEntity>> = programmeDao.getAllEvents()
    val allSheets: Flow<List<SpreadsheetTableEntity>> = spreadsheetDao.getAllSheets()

    fun getRepliesForPost(postId: Long): Flow<List<CommunityReplyEntity>> =
        communityDao.getRepliesForPost(postId)

    // User management
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    // Task management
    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    // Project management
    suspend fun insertProject(project: ProjectEntity): Long = projectDao.insertProject(project)
    suspend fun updateProject(project: ProjectEntity) = projectDao.updateProject(project)
    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)

    // Community
    suspend fun insertPost(post: CommunityPostEntity): Long = communityDao.insertPost(post)
    suspend fun updatePost(post: CommunityPostEntity) = communityDao.updatePost(post)
    suspend fun deletePost(post: CommunityPostEntity) = communityDao.deletePost(post)
    suspend fun insertReply(reply: CommunityReplyEntity): Long = communityDao.insertReply(reply)

    // Programme
    suspend fun insertEvent(event: ProgrammeEventEntity): Long = programmeDao.insertEvent(event)
    suspend fun updateEvent(event: ProgrammeEventEntity) = programmeDao.updateEvent(event)
    suspend fun deleteEvent(event: ProgrammeEventEntity) = programmeDao.deleteEvent(event)

    // Spreadsheet
    suspend fun insertSheet(sheet: SpreadsheetTableEntity): Long = spreadsheetDao.insertSheet(sheet)
    suspend fun updateSheet(sheet: SpreadsheetTableEntity) = spreadsheetDao.updateSheet(sheet)
    suspend fun deleteSheet(sheet: SpreadsheetTableEntity) = spreadsheetDao.deleteSheet(sheet)

    suspend fun ensureInitialData() {
        val existingUsers = userDao.getAllUsers().first()
        if (existingUsers.isNotEmpty()) return

        // 1. Initial Hierarchy Users
        val admin1 = UserEntity(
            id = "admin_1",
            name = "Henock B. (Directeur)",
            roleType = RoleType.ADMIN_1,
            position = "Directeur Général & Fondateur",
            whatsappNumber = "+243812345678",
            avatarColorHex = "#D4AF37",
            department = "Direction Générale",
            bio = "Premier Administrateur : Définition des orientations stratégiques, attribution des tâches aux équipes et validation des projets."
        )
        val admin2 = UserEntity(
            id = "admin_2",
            name = "Sarah Kouamé",
            roleType = RoleType.ADMIN_2,
            position = "Superviseur & Directrice des Opérations",
            whatsappNumber = "+33612345678",
            avatarColorHex = "#2196F3",
            department = "Opérations & Stratégie",
            bio = "Deuxième Administrateur : Supervision opérationnelle des équipes, coordination des ressources et suivi d'exécution."
        )
        val admin3 = UserEntity(
            id = "admin_3",
            name = "Marc Dupont",
            roleType = RoleType.ADMIN_3,
            position = "Chef de Projets & Coordinateur Général",
            whatsappNumber = "+33698765432",
            avatarColorHex = "#9C27B0",
            department = "Gestion de Projets",
            bio = "Troisième Administrateur : Élaboration des plannings, suivi des livrables et gestion des feuilles de calcul Google Sheets."
        )
        val member1 = UserEntity(
            id = "member_1",
            name = "Amina Diallo",
            roleType = RoleType.MEMBER,
            position = "Lead Développeuse Full-Stack & Mobile",
            whatsappNumber = "+221771234567",
            avatarColorHex = "#009688",
            department = "Ingénierie & Tech",
            bio = "Développement des architectures applicatives et intégration des flux de données."
        )
        val member2 = UserEntity(
            id = "member_2",
            name = "David N'Diaye",
            roleType = RoleType.MEMBER,
            position = "Responsable Commercial & Partenariats",
            whatsappNumber = "+225071234567",
            avatarColorHex = "#FF9800",
            department = "Commerce & Relations",
            bio = "Gestion du portefeuille client entreprise et prospection de nouveaux marchés."
        )
        val member3 = UserEntity(
            id = "member_3",
            name = "Claire Moreau",
            roleType = RoleType.MEMBER,
            position = "Comptable & Contrôleuse de Gestion",
            whatsappNumber = "+33655443322",
            avatarColorHex = "#E91E63",
            department = "Finance & Trésorerie",
            bio = "Établissement des bilans de performance, budgets et tableaux financiers."
        )

        userDao.insertUsers(listOf(admin1, admin2, admin3, member1, member2, member3))

        // 2. Initial Projects
        val proj1Id = projectDao.insertProject(
            ProjectEntity(
                name = "Plateforme Client Digitale 2026",
                description = "Modernisation intégrale des interfaces et déploiement des canaux d'assistance WhatsApp pour les clients.",
                leadAdminId = "admin_1",
                budget = "45 000 $",
                status = ProjectStatus.ACTIVE,
                startDate = "01 Oct 2026",
                targetDate = "15 Nov 2026",
                googleSheetUrl = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit"
            )
        )
        val proj2Id = projectDao.insertProject(
            ProjectEntity(
                name = "Campagne Commerciale Q4",
                description = "Acquisition de 25 nouveaux comptes institutionnels et présentation des offres collectives.",
                leadAdminId = "admin_2",
                budget = "18 500 $",
                status = ProjectStatus.PLANNING,
                startDate = "10 Oct 2026",
                targetDate = "31 Déc 2026",
                googleSheetUrl = "https://docs.google.com/spreadsheets/d/1exampleQ4CommercialCampaignSheet"
            )
        )

        // 3. Initial Tasks assigned by Admins 1, 2, 3
        taskDao.insertTask(
            Task(
                title = "Développer l'API de synchronisation WhatsApp",
                description = "Créer les endpoints pour notifier automatiquement les membres de l'équipe lors de la création d'une tâche critique.",
                assignedUser = "member_1",
                assignedToUserId = "member_1",
                assignedByUserId = "admin_1",
                projectId = proj1Id,
                status = TaskStatus.IN_PROGRESS,
                completionStatus = TaskStatus.IN_PROGRESS.label,
                isCompleted = false,
                priority = TaskPriority.URGENT,
                deadline = "08 Oct 2026",
                dueDate = "08 Oct 2026"
            )
        )
        taskDao.insertTask(
            Task(
                title = "Finaliser le catalogue des offres B2B",
                description = "Rédiger le dossier de présentation commerciale pour les rendez-vous partenaires de la semaine prochaine.",
                assignedUser = "member_2",
                assignedToUserId = "member_2",
                assignedByUserId = "admin_2",
                projectId = proj2Id,
                status = TaskStatus.TODO,
                completionStatus = TaskStatus.TODO.label,
                isCompleted = false,
                priority = TaskPriority.HIGH,
                deadline = "12 Oct 2026",
                dueDate = "12 Oct 2026"
            )
        )
        taskDao.insertTask(
            Task(
                title = "Mettre à jour le tableau budgétaire du projet",
                description = "Vérifier les dépenses engagées du mois de septembre et les consigner dans le Google Sheets de l'entreprise.",
                assignedUser = "member_3",
                assignedToUserId = "member_3",
                assignedByUserId = "admin_3",
                projectId = proj1Id,
                status = TaskStatus.REVIEW,
                completionStatus = TaskStatus.REVIEW.label,
                isCompleted = false,
                priority = TaskPriority.MEDIUM,
                deadline = "06 Oct 2026",
                dueDate = "06 Oct 2026"
            )
        )
        taskDao.insertTask(
            Task(
                title = "Revue de code de sécurité et tests d'intégration",
                description = "Valider les protocoles de chiffrement pour les échanges de la communauté interne.",
                assignedUser = "member_1",
                assignedToUserId = "member_1",
                assignedByUserId = "admin_2",
                projectId = proj1Id,
                status = TaskStatus.TODO,
                completionStatus = TaskStatus.TODO.label,
                isCompleted = false,
                priority = TaskPriority.HIGH,
                deadline = "14 Oct 2026",
                dueDate = "14 Oct 2026"
            )
        )

        // 4. Initial Community Posts
        val post1Id = communityDao.insertPost(
            CommunityPostEntity(
                authorId = "admin_1",
                title = "Bienvenue sur Goshen XM : Lancement de l'espace collaboratif !",
                content = "Chers collaborateurs,\n\nBienvenue sur notre plateforme collective d'entreprise connectée à WhatsApp. Cet espace réunit notre communauté professionnelle, nos projets clés, le planning quotidien de nos activités et nos tableaux de bord de gestion partagés.\n\nLes Administrateurs 1, 2 et 3 coordonneront les attributions de tâches et la progression générale. N'hésitez pas à poser vos questions ici !",
                category = PostCategory.ANNOUNCEMENT,
                likesCount = 9,
                isPinned = true
            )
        )
        communityDao.insertReply(
            CommunityReplyEntity(
                postId = post1Id,
                authorId = "member_1",
                content = "Excellente initiative ! L'interconnexion avec nos contacts WhatsApp facilite grandement la réactivité au quotidien."
            )
        )
        communityDao.insertReply(
            CommunityReplyEntity(
                postId = post1Id,
                authorId = "admin_2",
                content = "Toutes les équipes opérationnelles ont été rattachées avec leurs postes respectifs. Les plannings sont prêts !"
            )
        )

        val post2Id = communityDao.insertPost(
            CommunityPostEntity(
                authorId = "admin_3",
                title = "Tableau Google Sheets : Nouveau modèle de suivi des heures et livrables",
                content = "Le tableau de bord Google Sheets interne a été mis à jour avec les colonnes de suivi des livrables et des jalons. Vous pouvez consulter vos lignes et mettre à jour vos indicateurs directement dans l'onglet Tableur.",
                category = PostCategory.OPERATIONS,
                likesCount = 5,
                isPinned = false
            )
        )
        communityDao.insertReply(
            CommunityReplyEntity(
                postId = post2Id,
                authorId = "member_3",
                content = "Bien reçu ! Les formules de calcul automatique du budget fonctionnent parfaitement."
            )
        )

        // 5. Initial Programme Events
        programmeDao.insertEvent(
            ProgrammeEventEntity(
                title = "Point Quotidien (Standup d'entreprise)",
                description = "Revue rapide des tâches prioritaires de la journée et coordination entre les 3 Administrateurs et les collaborateurs.",
                date = "05 Oct 2026",
                time = "09:00",
                locationOrLink = "Salle de Réunion A & WhatsApp Call",
                organizerId = "admin_1",
                category = EventCategory.DAILY_STANDUP
            )
        )
        programmeDao.insertEvent(
            ProgrammeEventEntity(
                title = "Comité de pilotage des Projets Q4",
                description = "Examen de la feuille de route, arbitrage des budgets et répartition des nouveaux livrables.",
                date = "07 Oct 2026",
                time = "14:30",
                locationOrLink = "Visioconférence Google Meet",
                organizerId = "admin_2",
                category = EventCategory.MEETING
            )
        )
        programmeDao.insertEvent(
            ProgrammeEventEntity(
                title = "Session de synchronisation Google Sheets & Données",
                description = "Formation courte sur l'actualisation des métriques dans le tableur d'entreprise.",
                date = "09 Oct 2026",
                time = "11:00",
                locationOrLink = "Espace Coworking",
                organizerId = "admin_3",
                category = EventCategory.TRAINING
            )
        )

        // 6. Initial Spreadsheet Tables (Google Sheets à l'intérieur)
        val defaultColumns1 = "Code,Tâche / Activité,Assigné à,Budget ($),Réalisé ($),Statut"
        val defaultRows1 = listOf(
            "ACT-01\tDéveloppement Module WhatsApp\tAmina Diallo\t12000\t11500\tEn cours",
            "ACT-02\tNégociation Contrats B2B\tDavid N'Diaye\t8500\t6200\tÀ faire",
            "ACT-03\tClôture Comptable Trimestrielle\tClaire Moreau\t4500\t4500\tTerminé",
            "ACT-04\tCoordination & Sécurité Cloud\tMarc Dupont\t6000\t5800\tEn revue",
            "ACT-05\tAudit Stratégique & Direction\tHenock B.\t14000\t9000\tEn cours"
        ).joinToString("\n")

        spreadsheetDao.insertSheet(
            SpreadsheetTableEntity(
                title = "Tableau de Bord Budgétaire & Projets",
                description = "Suivi exécutif des enveloppes budgétaires, des affectations par membre d'équipe et de l'état d'avancement.",
                columnsRaw = defaultColumns1,
                rowsRaw = defaultRows1,
                externalGoogleSheetUrl = "https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit",
                lastModifiedBy = "Admin 1 (Directeur)",
                lastModifiedTimestamp = System.currentTimeMillis()
            )
        )

        val defaultColumns2 = "Semaine,Livrable Clé,Poste Responsable,Contact WhatsApp,Priorité,Validation Admin"
        val defaultRows2 = listOf(
            "S41\tArchitecture Mobile\tLead Développeuse\t+221771234567\tHaute\tValidé Admin 1",
            "S41\tPipeline Prospects B2B\tResp Commercial\t+225071234567\tMoyenne\tEn attente Admin 2",
            "S42\tRapport Prévisionnel\tComptabilité\t+33655443322\tHaute\tValidé Admin 3",
            "S42\tPlanning Déploiement\tChef de Projets\t+33698765432\tUrgente\tValidé Admin 1"
        ).joinToString("\n")

        spreadsheetDao.insertSheet(
            SpreadsheetTableEntity(
                title = "Matrice de Suivi Hebdomadaire des Livrables",
                description = "Tableau dynamique de pointage des livrables des postes clés et validation hiérarchique.",
                columnsRaw = defaultColumns2,
                rowsRaw = defaultRows2,
                externalGoogleSheetUrl = "https://docs.google.com/spreadsheets/d/1exampleWeeklyDeliverablesSheet",
                lastModifiedBy = "Admin 3 (Coordinateur)",
                lastModifiedTimestamp = System.currentTimeMillis() - 86400000L
            )
        )
    }
}
