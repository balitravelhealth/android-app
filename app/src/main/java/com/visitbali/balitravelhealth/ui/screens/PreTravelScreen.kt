package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.TravelUiState
import com.visitbali.balitravelhealth.viewmodel.TravelViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PreTravelScreen(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAssessment: () -> Unit = {},
    onNavigateToVaccination: () -> Unit = {},
    viewModel: TravelViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    PreTravelContent(
        uiState = uiState,
        onBack = onBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToGuide = onNavigateToGuide,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToAssessment = onNavigateToAssessment,
        onNavigateToVaccination = onNavigateToVaccination,
        onDatesSelected = { start, end ->
            viewModel.saveAndSyncDates(start, end)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreTravelContent(
    uiState: TravelUiState,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAssessment: () -> Unit,
    onNavigateToVaccination: () -> Unit,
    onDatesSelected: (LocalDate, LocalDate) -> Unit
) {
    var showRangePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BaliNavigationBar(
                initialSelectedItem = 0,
                onHomeClick = onNavigateToHome,
                onGuideClick = onNavigateToGuide,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.pre_travel_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = stringResource(R.string.pre_travel_subtitle),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            SectionHeader(title = stringResource(R.string.label_advice))
            Spacer(modifier = Modifier.height(12.dp))
            val latestAssessment = uiState.latestAssessment
            val adviceTitle = latestAssessment?.diagnosis ?: stringResource(R.string.pre_travel_advice_default_title)
            val adviceContent = latestAssessment?.recommendation ?: stringResource(R.string.pre_travel_advice_default_desc)
            AdviceCard(
                title = adviceTitle,
                description = adviceContent
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(title = stringResource(R.string.label_tools))
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolCard(
                    title = stringResource(R.string.pre_travel_tool_health_risk),
                    icon = Icons.Default.Checklist,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAssessment
                )
                ToolCard(
                    title = stringResource(R.string.pre_travel_tool_vaccine_record),
                    icon = Icons.Default.Vaccines,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToVaccination
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(title = stringResource(R.string.label_schedule))
            Spacer(modifier = Modifier.height(16.dp))

            val arrivalDate = uiState.arrivalDate
            val departureDate = uiState.departureDate
            val scheduleTitle = if (arrivalDate != null && departureDate != null) {
                "${arrivalDate.format(dateFormatter)} - ${departureDate.format(dateFormatter)}"
            } else {
                stringResource(R.string.pre_travel_add_dates)
            }
            val scheduleSub = if (arrivalDate != null && departureDate != null) {
                stringResource(R.string.pre_travel_change_schedule)
            } else {
                stringResource(R.string.pre_travel_set_schedule)
            }

            ScheduleCard(
                title = scheduleTitle,
                subtitle = scheduleSub,
                onClick = { showRangePicker = true }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showRangePicker) {
        DateRangePickerModal(
            onDismiss = { showRangePicker = false },
            onDateRangeSelected = { start, end ->
                onDatesSelected(start, end)
                showRangePicker = false
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 22.sp
        )
    )
}

@Composable
private fun AdviceCard(title: String, description: String) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF8C82))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun ToolCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(44.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

@Composable
private fun ScheduleCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF5BA25B), Color(0xFF3E7A3E))
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit
) {
    val state = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startMillis = state.selectedStartDateMillis
                    val endMillis = state.selectedEndDateMillis
                    if (startMillis != null && endMillis != null) {
                        val start = Instant.ofEpochMilli(startMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                        val end = Instant.ofEpochMilli(endMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                        onDateRangeSelected(start, end)
                    }
                }
            ) {
                Text(stringResource(R.string.pre_travel_btn_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    ) {
        DateRangePicker(
            state = state,
            title = { Text(stringResource(R.string.picker_title_select_travel_dates), modifier = Modifier.padding(16.dp)) },
            showModeToggle = false,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun PreTravelScreenPreview() {
    BaliTravelHealthTheme {
        PreTravelContent(
            uiState = TravelUiState(),
            onBack = {},
            onNavigateToAssessment = {},
            onNavigateToVaccination = {},
            onDatesSelected = { _, _ -> }
        )
    }
}
