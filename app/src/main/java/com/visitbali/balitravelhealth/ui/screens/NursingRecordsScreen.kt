package com.visitbali.balitravelhealth.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.data.dto.CareRecordRequest
import com.visitbali.balitravelhealth.data.dto.NursingRecord
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.NursingRecordsUiState
import com.visitbali.balitravelhealth.viewmodel.NursingRecordsViewModel

@Composable
fun NursingRecordsScreen(
    onBack: () -> Unit,
    viewModel: NursingRecordsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error, uiState.message) {
        uiState.error?.let { snackbar.showSnackbar(it) }
        uiState.message?.let { snackbar.showSnackbar(it) }
    }

    NursingRecordsContent(
        uiState = uiState,
        snackbarHostState = snackbar,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onUpdate = viewModel::updateRecord,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NursingRecordsContent(
    uiState: NursingRecordsUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onUpdate: (Int, CareRecordRequest) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nursing Records") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("My Care") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Nurse") })
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val records = if (selectedTab == 0) uiState.myRecords else uiState.nurseRecords
                if (records.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            if (selectedTab == 0) "No traveler records yet." else "No nurse-assigned records returned.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(records, key = { it.id }) { record ->
                            NursingRecordCard(
                                record = record,
                                editable = selectedTab == 1,
                                isSaving = uiState.isSaving,
                                onUpdate = onUpdate,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NursingRecordCard(
    record: NursingRecord,
    editable: Boolean,
    isSaving: Boolean,
    onUpdate: (Int, CareRecordRequest) -> Unit,
) {
    var assessment by remember(record.id) { mutableStateOf(record.nursingAssessment.orEmpty()) }
    var diagnosis by remember(record.id) { mutableStateOf(record.nursingDiagnosis.orEmpty()) }
    var planning by remember(record.id) { mutableStateOf(record.nursingPlanning.orEmpty()) }
    var implementation by remember(record.id) { mutableStateOf(record.nursingImplementation.orEmpty()) }
    var evaluation by remember(record.id) { mutableStateOf(record.nursingEvaluation.orEmpty()) }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Record #${record.id}", fontWeight = FontWeight.Bold)
                    Text(record.tanggalKunjungan, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("Nurse ${record.nurseId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (editable) {
                CareField("Assessment", assessment) { assessment = it }
                CareField("Diagnosis", diagnosis) { diagnosis = it }
                CareField("Planning", planning) { planning = it }
                CareField("Implementation", implementation) { implementation = it }
                CareField("Evaluation", evaluation) { evaluation = it }
                Button(
                    onClick = {
                        onUpdate(
                            record.id,
                            CareRecordRequest(
                                nursingAssessment = assessment.ifBlank { null },
                                nursingDiagnosis = diagnosis.ifBlank { null },
                                nursingPlanning = planning.ifBlank { null },
                                nursingImplementation = implementation.ifBlank { null },
                                nursingEvaluation = evaluation.ifBlank { null },
                            )
                        )
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.padding(4.dp))
                    Text(if (isSaving) "Saving..." else "Save Care Record")
                }
            } else {
                RecordText("Assessment", record.nursingAssessment)
                RecordText("Diagnosis", record.nursingDiagnosis)
                RecordText("Planning", record.nursingPlanning)
                RecordText("Implementation", record.nursingImplementation)
                RecordText("Evaluation", record.nursingEvaluation)
            }
        }
    }
}

@Composable
private fun CareField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun RecordText(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NursingRecordsPreview() {
    BaliTravelHealthTheme {
        NursingRecordsContent(
            uiState = NursingRecordsUiState(
                myRecords = listOf(NursingRecord(1, 4, "2026-05-27", "Stable", null, null, null, null)),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onRefresh = {},
            onUpdate = { _, _ -> },
        )
    }
}
