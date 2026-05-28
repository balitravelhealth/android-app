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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.data.dto.AssessmentResult
import com.visitbali.balitravelhealth.data.dto.ExpertSymptom
import com.visitbali.balitravelhealth.data.dto.NearbyFacility
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.AssessmentUiState
import com.visitbali.balitravelhealth.viewmodel.AssessmentViewModel

@Composable
fun AssessmentScreen(
    kategori: String = "pre_travel",
    onBack: () -> Unit = {},
    viewModel: AssessmentViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(kategori) {
        viewModel.load(kategori)
    }

    AssessmentContent(
        kategori = kategori,
        uiState = uiState,
        onBack = onBack,
        onToggleSymptom = viewModel::toggleSymptom,
        onSubmit = { viewModel.submitAssessment(kategori) },
        onClearResult = viewModel::clearResult,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssessmentContent(
    kategori: String,
    uiState: AssessmentUiState,
    onBack: () -> Unit,
    onToggleSymptom: (Int) -> Unit,
    onSubmit: () -> Unit,
    onClearResult: () -> Unit,
) {
    val isPostTravel = kategori == "post_travel"
    val title = if (isPostTravel) "Health Screening" else "Health Risk Assessment"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        uiState.result?.let { result ->
            AssessmentResultPage(
                result = result,
                nearbyFacilities = uiState.nearbyFacilities,
                onDone = onClearResult,
                modifier = Modifier.padding(padding),
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(
                    if (isPostTravel) "Select any symptoms you feel after travel." else "Select symptoms or concerns before your trip.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Symptoms", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        if (uiState.symptoms.isEmpty()) {
                            Text("No symptoms returned by backend.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            uiState.symptoms.forEach { symptom ->
                                val selected = symptom.id in uiState.selectedSymptoms
                                FilterChip(
                                    selected = selected,
                                    onClick = { onToggleSymptom(symptom.id) },
                                    label = {
                                        Text(symptom.labelEn ?: symptom.labelId ?: symptom.kode ?: "Symptom ${symptom.id}")
                                    },
                                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                                )
                            }
                        }
                        Button(
                            onClick = onSubmit,
                            enabled = uiState.selectedSymptoms.isNotEmpty() && !uiState.isSubmitting,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                        ) {
                            if (uiState.isSubmitting) CircularProgressIndicator(modifier = Modifier.size(20.dp)) else Text("Analyze Symptoms")
                        }
                    }
                }
            }

            if (uiState.history.isNotEmpty()) {
                item {
                    Text("Assessment History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                items(uiState.history.take(5), key = { it.id }) { result ->
                    HistoryCard(result)
                }
            }

            uiState.error?.let {
                item { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}

@Composable
private fun AssessmentResultPage(
    result: AssessmentResult,
    nearbyFacilities: List<NearbyFacility>,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(result.diagnosis ?: "Assessment complete", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(result.recommendation ?: "No recommendation returned.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            val confidence = result.confidenceScore.coerceIn(0f, 1f)
            LinearProgressIndicator(progress = { confidence }, modifier = Modifier.fillMaxWidth())
            Text("${(confidence * 100).toInt()}% confidence", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (result.riskLevel == "high" && nearbyFacilities.isNotEmpty()) {
            item {
                Text("Nearby Medical Facilities", fontWeight = FontWeight.Bold)
            }
            items(nearbyFacilities, key = { it.id }) { facility ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.size(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(facility.nama, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${facility.jenis} - ${String.format("%.1f km", facility.jarakKm)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        item {
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(26.dp)) {
                Text("Done")
            }
        }
    }
}

@Composable
private fun HistoryCard(result: AssessmentResult) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(result.diagnosis ?: "Assessment", fontWeight = FontWeight.SemiBold)
                Text(result.riskLevel ?: "risk unknown", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AssistChip(onClick = {}, label = { Text(result.kategori ?: "-") })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AssessmentPreview() {
    BaliTravelHealthTheme {
        AssessmentContent(
            kategori = "pre_travel",
            uiState = AssessmentUiState(
                symptoms = listOf(ExpertSymptom(1, "S_001", "Demam", "Fever", "pre_travel")),
            ),
            onBack = {},
            onToggleSymptom = {},
            onSubmit = {},
            onClearResult = {},
        )
    }
}
