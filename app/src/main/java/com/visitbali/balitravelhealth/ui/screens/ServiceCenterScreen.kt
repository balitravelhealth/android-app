package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.data.dto.ExpertSymptom
import com.visitbali.balitravelhealth.data.dto.HealthResponse
import com.visitbali.balitravelhealth.data.dto.LocationClassificationResponse
import com.visitbali.balitravelhealth.data.dto.NearbyFacility
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.ServiceCenterUiState
import com.visitbali.balitravelhealth.viewmodel.ServiceCenterViewModel

@Composable
fun ServiceCenterScreen(
    onBack: () -> Unit,
    viewModel: ServiceCenterViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    ServiceCenterContent(
        uiState = uiState,
        onBack = onBack,
        onRefresh = { viewModel.refresh() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceCenterContent(
    uiState: ServiceCenterUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Travel Services") },
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
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(
                        "Backend-powered services",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Live health, location, nearby facility, and expert-system data from Bali Health.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                item {
                    StatusCard(uiState.health)
                }

                item {
                    LocationCard(uiState.location)
                }

                item {
                    NearbyFacilitiesCard(uiState.nearbyFacilities)
                }

                item {
                    ExpertSymptomsCard(
                        preTravelSymptoms = uiState.preTravelSymptoms,
                        postTravelSymptoms = uiState.postTravelSymptoms,
                    )
                }

                uiState.error?.let { error ->
                    item {
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusCard(health: HealthResponse?) {
    EndpointCard(
        icon = Icons.Default.CloudDone,
        title = "Gateway Health",
        subtitle = health?.status ?: "Unknown",
    ) {
        Text(
            health?.message ?: "No health message returned.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        health?.database?.let {
            AssistChip(onClick = {}, label = { Text("Database: $it") })
        }
    }
}

@Composable
private fun LocationCard(location: LocationClassificationResponse?) {
    EndpointCard(
        icon = Icons.Default.LocationOn,
        title = "Location Classification",
        subtitle = location?.label ?: "Denpasar fallback",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(location?.zone ?: "unknown") })
            location?.nearestFacilityKm?.let {
                AssistChip(onClick = {}, label = { Text(String.format("%.1f km to care", it)) })
            }
        }
    }
}

@Composable
private fun NearbyFacilitiesCard(facilities: List<NearbyFacility>) {
    EndpointCard(
        icon = Icons.Default.LocalHospital,
        title = "Nearby Backend Facilities",
        subtitle = "${facilities.size} facilities",
    ) {
        if (facilities.isEmpty()) {
            Text("No nearby facilities returned.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            facilities.take(6).forEach { facility ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(facility.nama, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(facility.jenis, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Text(String.format("%.1f km", facility.jarakKm), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ExpertSymptomsCard(
    preTravelSymptoms: List<ExpertSymptom>,
    postTravelSymptoms: List<ExpertSymptom>,
) {
    var selected by remember { mutableStateOf("pre_travel") }
    val symptoms = if (selected == "pre_travel") preTravelSymptoms else postTravelSymptoms
    EndpointCard(
        icon = Icons.AutoMirrored.Filled.Rule,
        title = "Expert Symptoms",
        subtitle = "${symptoms.size} symptoms",
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selected == "pre_travel",
                onClick = { selected = "pre_travel" },
                label = { Text("Pre Travel") },
            )
            FilterChip(
                selected = selected == "post_travel",
                onClick = { selected = "post_travel" },
                label = { Text("Post Travel") },
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (symptoms.isEmpty()) {
            Text("No symptoms returned.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            symptoms.take(12).forEach { symptom ->
                Text(
                    text = symptom.labelEn ?: symptom.labelId ?: symptom.kode ?: "Symptom ${symptom.id}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun EndpointCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ServiceCenterPreview() {
    BaliTravelHealthTheme {
        ServiceCenterContent(
            uiState = ServiceCenterUiState(
                health = HealthResponse(status = "ok", message = "Gateway is healthy", database = "ok"),
                location = LocationClassificationResponse("urban", "Denpasar", 1.4f),
                nearbyFacilities = listOf(NearbyFacility(1, "RS Bali Mandara", "hospital", -8.7, 115.2, 2.2f, "+62")),
                preTravelSymptoms = listOf(ExpertSymptom(1, "S_001", "Demam", "Fever", "pre_travel")),
            ),
            onBack = {},
            onRefresh = {},
        )
    }
}
