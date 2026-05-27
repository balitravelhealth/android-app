package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.TravelViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreTravelScreen(
    onBack: () -> Unit,
    onNavigateToHealthcare: () -> Unit = {},
    onNavigateToAssessment: () -> Unit = {},
    onNavigateToVaccination: () -> Unit = {},
    onNavigateToDestinations: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    viewModel: TravelViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    
    PreTravelContent(
        uiState = uiState,
        onBack = onBack,
        onNavigateToHealthcare = onNavigateToHealthcare,
        onNavigateToAssessment = onNavigateToAssessment,
        onNavigateToVaccination = onNavigateToVaccination,
        onNavigateToDestinations = onNavigateToDestinations,
        onNavigateToServices = onNavigateToServices,
        onDatesSelected = { start, end ->
            viewModel.saveAndSyncDates(start, end)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreTravelContent(
    uiState: com.visitbali.balitravelhealth.viewmodel.TravelUiState,
    onBack: () -> Unit,
    onNavigateToHealthcare: () -> Unit = {},
    onNavigateToAssessment: () -> Unit = {},
    onNavigateToVaccination: () -> Unit = {},
    onNavigateToDestinations: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    onDatesSelected: (LocalDate, LocalDate) -> Unit
) {
    var showRangePicker by remember { mutableStateOf(value = false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Get Prepared",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily(Font(R.font.inter_font)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            )
            Text(
                text = "Prevention is better than cure",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily(Font(R.font.instrument_serif_italic)),
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            AdviceCardStack()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tools",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolCard(
                    title = "Health Risk\nAssessment",
                    color = Color(0xFF1565C0),
                    iconRes = R.drawable.ic_assessment,
                    modifier = Modifier.weight(1f).clickable { onNavigateToAssessment() }
                )
                ToolCard(
                    title = "Vaccine\nRecord",
                    color = Color(0xFFD49110),
                    iconRes = R.drawable.ic_vaccine,
                    modifier = Modifier.weight(1f).clickable { onNavigateToVaccination() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolCard(
                    title = "Destination\nHealth Risks",
                    color = Color(0xFF2E7D32),
                    iconRes = R.drawable.ic_placepin,
                    modifier = Modifier.weight(1f).clickable { onNavigateToDestinations() }
                )
                ToolCard(
                    title = "Travel\nServices",
                    color = MaterialTheme.colorScheme.primary,
                    iconRes = R.drawable.ic_hospital,
                    modifier = Modifier.weight(1f).clickable { onNavigateToServices() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Schedule",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily(Font(R.font.inter_font)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val arrivalDate = uiState.arrivalDate
            val departureDate = uiState.departureDate
            val scheduleText = if (arrivalDate != null && departureDate != null) {
                "${arrivalDate.format(dateFormatter)} - ${departureDate.format(dateFormatter)}"
            } else {
                "Enter Your Trip Date"
            }

            ScheduleCard(
                text = scheduleText,
            ) { showRangePicker = true }
        }
    }

    if (showRangePicker) {
        DateRangePickerModal(
            onDismiss = { showRangePicker = false }
        ) { start, end ->
            onDatesSelected(start, end)
            showRangePicker = false
        }
    }
}

@Composable
fun AdviceCardStack() {
    var cardIndex by remember { mutableIntStateOf(0) }
    val totalCards = 3
    // Correct sequence: Grey (0), Red (1), Orange (2)
    val colors = listOf(Color(0xFFE0E0E0), Color(0xFFB71C1C), Color(0xFFD49110))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center,
    ) {
        // We render based on their Z-order
        for (i in 0 until totalCards) {
            // Determine which color/content this specific card "slot" has
            // based on current rotation index
            val actualCardIndex = (i + cardIndex) % totalCards
            val isFront = i == 0
            
            // Animation for the flip/send-to-back effect
            val transition = updateTransition(targetState = isFront, label = "CardTransition_$actualCardIndex")
            
            val rotation by transition.animateFloat(
                transitionSpec = { tween(durationMillis = 600, easing = FastOutSlowInEasing) },
                label = "Rotation"
            ) { front -> if (front) 0f else -5f }

            val offset by transition.animateIntOffset(
                transitionSpec = { tween(durationMillis = 600, easing = FastOutSlowInEasing) },
                label = "Offset"
            ) { front -> 
                if (front) IntOffset(0, 0) 
                else IntOffset((i * 15), (i * 15)) 
            }

            // The actual card
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight()
                    .offset { offset }
                    .graphicsLayer {
                        rotationZ = rotation
                        cameraDistance = 12f * density
                    }
                    .zIndex((totalCards - i).toFloat()) // Higher Z for front cards
                    .clickable(enabled = isFront) {
                        cardIndex = (cardIndex + 1) % totalCards
                    },
                shape = RoundedCornerShape(24.dp),
                color = colors[actualCardIndex],
                shadowElevation = 4.dp
            ) {
                if (isFront) {
                    AdviceContent()
                }
            }
        }
    }
}

@Composable
fun AdviceContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Text(
                text = "Advice",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.inter_font))
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pre_background),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "There's no advice for now.",
                fontFamily = FontFamily(Font(R.font.inter_font)),
                color = Color.DarkGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreTravelScreenPreview() {
    BaliTravelHealthTheme {
        PreTravelContent(
            uiState = com.visitbali.balitravelhealth.viewmodel.TravelUiState(
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now().plusDays(7)
            ),
            onBack = {},
            onNavigateToHealthcare = {},
            onNavigateToAssessment = {},
            onNavigateToVaccination = {},
            onNavigateToDestinations = {},
            onNavigateToServices = {},
            onDatesSelected = { _, _ -> }
        )
    }
}

@Composable
fun ScheduleCard(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(24.dp)),
        color = Color(0xFF689F38)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Icon with 20% opacity
            Icon(
                painter = painterResource(id = R.drawable.ic_during_light),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-48).dp, y = 30.dp)
                    .graphicsLayer { alpha = 0.2f },
                tint = Color.White
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.inter_font)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )

                Surface(
                    onClick = onClick,
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Set Schedule",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Black
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
                    if ((startMillis != null) && (endMillis != null)) {
                        val start = Instant.ofEpochMilli(startMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                        val end = Instant.ofEpochMilli(endMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                        onDateRangeSelected(start, end)
                    }
                },
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = state,
            title = { Text("Select Travel Dates", modifier = Modifier.padding(16.dp)) },
            showModeToggle = false,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ToolCard(title: String, color: Color, iconRes: Int, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp)),
        color = color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.inter_font)),
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            )
        }
    }
}
