package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.SpreadsheetTableEntity
import com.example.data.entity.UserEntity
import com.example.ui.theme.WhatsAppGreen
import com.example.util.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpreadsheetScreen(
    sheets: List<SpreadsheetTableEntity>,
    currentUser: UserEntity?,
    onUpdateCell: (SpreadsheetTableEntity, rowIndex: Int, colIndex: Int, newValue: String) -> Unit,
    onAddRow: (SpreadsheetTableEntity, rowCells: List<String>) -> Unit,
    onDeleteRow: (SpreadsheetTableEntity, rowIndex: Int) -> Unit,
    onCreateSheet: (title: String, desc: String, columns: List<String>, url: String) -> Unit,
    onUpdateSheetUrl: (SpreadsheetTableEntity, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSheetId by remember(sheets) {
        mutableStateOf(sheets.firstOrNull()?.id ?: 0L)
    }
    val currentSheet = sheets.firstOrNull { it.id == selectedSheetId } ?: sheets.firstOrNull()

    var showCreateSheetDialog by remember { mutableStateOf(false) }
    var showAddRowDialog by remember { mutableStateOf(false) }
    var editingCellInfo by remember { mutableStateOf<Triple<Int, Int, String>?>(null) } // (row, col, value)
    var showEditUrlDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val canAdmin = currentUser?.roleType?.isAdministrator == true

    Column(modifier = modifier.fillMaxSize()) {
        // Top Toolbar: Sheet Selector & New Sheet button
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F9D58), // Google Sheets Green
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Tableaux Google Sheets",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Édition de données & synchronisation WhatsApp",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (canAdmin) {
                        Button(
                            onClick = { showCreateSheetDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nouveau", fontSize = 12.sp)
                        }
                    }
                }

                // Dropdown to switch sheets
                if (sheets.size > 1) {
                    Spacer(modifier = Modifier.height(10.dp))
                    var sheetDropdownExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = sheetDropdownExpanded,
                        onExpandedChange = { sheetDropdownExpanded = !sheetDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = currentSheet?.title ?: "Choisir une feuille",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Feuille active") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sheetDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = sheetDropdownExpanded,
                            onDismissRequest = { sheetDropdownExpanded = false }
                        ) {
                            sheets.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.title) },
                                    onClick = {
                                        selectedSheetId = s.id
                                        sheetDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (currentSheet == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aucun tableau disponible.")
            }
        } else {
            val columns = remember(currentSheet.columnsRaw) {
                currentSheet.columnsRaw.split(",").map { it.trim() }
            }
            val rows = remember(currentSheet.rowsRaw) {
                currentSheet.rowsRaw.split("\n")
                    .filter { it.isNotBlank() }
                    .map { rowLine ->
                        val parts = rowLine.split("\t")
                        if (parts.size >= columns.size) parts
                        else parts + List(columns.size - parts.size) { "-" }
                    }
            }

            // Calculations for numeric columns
            val columnCalculations = remember(columns, rows) {
                columns.mapIndexed { cIdx, colName ->
                    val numericValues = rows.mapNotNull { row ->
                        row.getOrNull(cIdx)?.replace(" ", "")?.replace("$", "")?.replace("€", "")?.toDoubleOrNull()
                    }
                    if (numericValues.size >= 2) {
                        val sum = numericValues.sum()
                        Pair(colName, sum)
                    } else null
                }.filterNotNull()
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info Card with Google Sheets Link & WhatsApp Share
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentSheet.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (currentSheet.description.isNotBlank()) {
                                        Text(
                                            text = currentSheet.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Surface(
                                    color = Color(0xFF0F9D58).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Google Sheets",
                                        color = Color(0xFF0F9D58),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Open External Sheet + Export WhatsApp
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (currentSheet.externalGoogleSheetUrl.isNotBlank()) {
                                    Button(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentSheet.externalGoogleSheetUrl))
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF0F9D58),
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ouvrir en ligne", fontSize = 12.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        val exportText = WhatsAppHelper.formatSpreadsheetExport(currentSheet)
                                        WhatsAppHelper.shareGenericText(context, exportText, "Exporter le tableau sur WhatsApp")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = WhatsAppGreen,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp", fontSize = 12.sp)
                                }

                                if (canAdmin) {
                                    OutlinedButton(
                                        onClick = { showEditUrlDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Link, contentDescription = "Lien URL", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Grid Controls: "+ Ligne"
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Données (${rows.size} lignes) • Touchez une cellule pour modifier",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (canAdmin) {
                            Button(
                                onClick = { showAddRowDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Ajouter ligne", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Interactive Horizontal-Scrolling Grid
                item {
                    val horizontalScrollState = rememberScrollState()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                            // Header Row
                            Row(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(vertical = 8.dp)
                            ) {
                                // Line number header
                                Box(
                                    modifier = Modifier
                                        .width(42.dp)
                                        .padding(horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                columns.forEach { colName ->
                                    Box(
                                        modifier = Modifier
                                            .width(135.dp)
                                            .padding(horizontal = 6.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = colName,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (canAdmin) {
                                    Box(modifier = Modifier.width(36.dp))
                                }
                            }

                            // Data Rows
                            rows.forEachIndexed { rIdx, rowCells ->
                                val isEven = rIdx % 2 == 0
                                Row(
                                    modifier = Modifier
                                        .background(
                                            if (isEven) MaterialTheme.colorScheme.surface
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Row number
                                    Box(
                                        modifier = Modifier
                                            .width(42.dp)
                                            .padding(horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${rIdx + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Columns cells
                                    columns.forEachIndexed { cIdx, _ ->
                                        val cellValue = rowCells.getOrNull(cIdx) ?: "-"
                                        Box(
                                            modifier = Modifier
                                                .width(135.dp)
                                                .padding(horizontal = 6.dp)
                                                .border(
                                                    0.5.dp,
                                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .clickable {
                                                    editingCellInfo = Triple(rIdx, cIdx, cellValue)
                                                }
                                                .padding(6.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                text = cellValue,
                                                fontSize = 12.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    // Delete row action
                                    if (canAdmin) {
                                        IconButton(
                                            onClick = { onDeleteRow(currentSheet, rIdx) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Supprimer ligne",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            }
                        }
                    }
                }

                // Summary / Totals / Calculations Bar
                if (columnCalculations.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Calculate,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Calculs Automatiques des Colonnes Numériques",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                columnCalculations.forEach { (colName, sum) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total $colName :",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "%.2f $".format(sum),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Cell Edit Dialog
    editingCellInfo?.let { (rIdx, cIdx, currentVal) ->
        var editedVal by remember { mutableStateOf(currentVal) }
        val colName = currentSheet?.columnsRaw?.split(",")?.getOrNull(cIdx)?.trim() ?: "Cellule"

        AlertDialog(
            onDismissRequest = { editingCellInfo = null },
            title = {
                Text("Modifier $colName (Ligne #${rIdx + 1})", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Saisissez la nouvelle valeur pour cette cellule du tableau :",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedVal,
                        onValueChange = { editedVal = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentSheet != null) {
                            onUpdateCell(currentSheet, rIdx, cIdx, editedVal)
                        }
                        editingCellInfo = null
                    }
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCellInfo = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Add Row Dialog
    if (showAddRowDialog && currentSheet != null) {
        val columns = remember(currentSheet.columnsRaw) { currentSheet.columnsRaw.split(",").map { it.trim() } }
        val cellInputs = remember { columns.map { mutableStateOf("") } }

        AlertDialog(
            onDismissRequest = { showAddRowDialog = false },
            title = { Text("Ajouter une ligne au tableau", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(columns) { index, colName ->
                        OutlinedTextField(
                            value = cellInputs[index].value,
                            onValueChange = { cellInputs[index].value = it },
                            label = { Text(colName) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val rowData = cellInputs.map { it.value.ifBlank { "-" } }
                        onAddRow(currentSheet, rowData)
                        showAddRowDialog = false
                    }
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddRowDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Create New Sheet Dialog (Admins)
    if (showCreateSheetDialog) {
        var title by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var columnsInput by remember { mutableStateOf("Réf,Poste / Action,Responsable,Budget ($),Statut") }
        var urlInput by remember { mutableStateOf("https://docs.google.com/spreadsheets/d/...") }

        AlertDialog(
            onDismissRequest = { showCreateSheetDialog = false },
            title = { Text("Nouveau Tableau Google Sheets", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de la feuille") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    OutlinedTextField(
                        value = columnsInput,
                        onValueChange = { columnsInput = it },
                        label = { Text("Colonnes (séparées par des virgules)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Lien Google Sheets optionnel") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val cols = columnsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        if (title.isNotBlank() && cols.isNotEmpty()) {
                            onCreateSheet(title, desc, cols, urlInput)
                            showCreateSheetDialog = false
                        }
                    },
                    enabled = title.isNotBlank()
                ) {
                    Text("Créer la feuille")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateSheetDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Edit Google Sheets URL Dialog
    if (showEditUrlDialog && currentSheet != null) {
        var newUrl by remember { mutableStateOf(currentSheet.externalGoogleSheetUrl) }

        AlertDialog(
            onDismissRequest = { showEditUrlDialog = false },
            title = { Text("Lien Google Sheets en ligne", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Collez l'URL de votre feuille Google Sheets pour y accéder en 1 clic :",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text("URL Google Sheets") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateSheetUrl(currentSheet, newUrl)
                        showEditUrlDialog = false
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditUrlDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
