package com.visitbali.balitravelhealth.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.data.dto.Destination
import com.visitbali.balitravelhealth.data.dto.HealthRisk
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.DestinationUiState
import com.visitbali.balitravelhealth.viewmodel.DestinationViewModel

@Composable
fun DestinationScreen(
    onBack: () -> Unit = {},
    viewModel: DestinationViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = uiState.selectedDestination != null) {
        viewModel.clearSelection()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    DestinationContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = {
            if (uiState.selectedDestination != null) viewModel.clearSelection() else onBack()
        },
        onDestinationClick = { viewModel.selectDestination(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DestinationContent(
    uiState: DestinationUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBack: () -> Unit,
    onDestinationClick: (Destination) -> Unit,
) {
    val isDetailMode = uiState.selectedDestination != null

    // Preserve the last non-null destination so the exit animation can still render it
    var lastDestination by remember { mutableStateOf<Destination?>(null) }
    LaunchedEffect(uiState.selectedDestination) {
        uiState.selectedDestination?.let { lastDestination = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        lastDestination?.namaDaerah?.takeIf { isDetailMode }
                            ?: "Bali Destinations"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
    ) { padding ->
        AnimatedContent(
            targetState = isDetailMode,
            transitionSpec = {
                if (targetState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "DestinationTransition",
        ) { showDetail ->
            if (showDetail) {
                // Use lastDestination so the exit animation still has data even after
                // selectedDestination is cleared by clearSelection()
                val destination = lastDestination
                if (destination != null) {
                    HealthRiskListPage(
                        destination = destination,
                        risks = uiState.healthRisks,
                        isLoading = uiState.isLoadingRisks,
                        modifier = Modifier.padding(padding),
                    )
                }
            } else {
                DestinationListPage(
                    destinations = uiState.destinations,
                    isLoading = uiState.isLoadingList,
                    onDestinationClick = onDestinationClick,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun DestinationListPage(
    destinations: List<Destination>,
    isLoading: Boolean,
    onDestinationClick: (Destination) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        destinations.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No destinations available", color = Color.Gray)
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = "Explore health risks by destination",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                items(destinations) { destination ->
                    DestinationCard(
                        destination = destination,
                        onClick = { onDestinationClick(destination) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DestinationCard(
    destination: Destination,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(36.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = destination.namaDaerah,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray,
            )
        }
    }
}

@Composable
private fun HealthRiskListPage(
    destination: Destination,
    risks: List<HealthRisk>,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        risks.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No health risks reported", color = Color.Gray)
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFF1565C0),
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${risks.size} health risk(s) identified in ${destination.namaDaerah}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1565C0),
                            )
                        }
                    }
                }
                items(risks) { risk ->
                    HealthRiskCard(risk = risk)
                }
            }
        }
    }
}

@Composable
private fun HealthRiskCard(risk: HealthRisk) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = risk.namaRisiko,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Prevention",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
            )
            Text(
                text = risk.saranPencegahan,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 20.sp,
            )

            if (!risk.rekomendasiVaksinasi.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFF3E5F5),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Default.Vaccines,
                            contentDescription = null,
                            tint = Color(0xFF7B1FA2),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recommended: ${risk.rekomendasiVaksinasi}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7B1FA2),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DestinationScreenPreview() {
    BaliTravelHealthTheme {
        DestinationContent(
            uiState = DestinationUiState(
                destinations = listOf(
                    Destination(1, "Kuta Beach", "2024-01-01T00:00:00Z"),
                    Destination(2, "Ubud", "2024-01-01T00:00:00Z"),
                    Destination(3, "Seminyak", "2024-01-01T00:00:00Z"),
                ),
            ),
            onBack = {},
            onDestinationClick = {},
        )
    }
}
