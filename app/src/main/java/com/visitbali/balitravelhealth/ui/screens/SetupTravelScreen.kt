package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.TravelUiState
import com.visitbali.balitravelhealth.viewmodel.TravelViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupTravelScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    viewModel: TravelViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SetupTravelScreenContent(
        uiState = uiState,
        onBack = onBack,
        onNext = onNext,
        onSkip = onSkip,
        onArrivalSelected = { viewModel.updateArrival(it) },
        onDepartureSelected = { viewModel.updateDeparture(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupTravelScreenContent(
    uiState: TravelUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onArrivalSelected: (LocalDate) -> Unit,
    onDepartureSelected: (LocalDate) -> Unit
) {
    var showArrivalPicker by remember { mutableStateOf(false) }
    var showDeparturePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    
    val isDatesFilled = uiState.arrivalDate != null && uiState.departureDate != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ready to travel?",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "The seasonal forecast helps you plan your activities according to Bali's tropical climate patterns.",
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            DateInput(
                label = uiState.arrivalDate?.format(dateFormatter) ?: "Arrival Date",
                modifier = Modifier.weight(1f),
                onClick = { showArrivalPicker = true }
            )

            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.width(20.dp).height(1.dp).background(Color.Gray))
            Spacer(modifier = Modifier.width(16.dp))

            DateInput(
                label = uiState.departureDate?.format(dateFormatter) ?: "Departure Date",
                modifier = Modifier.weight(1f),
                onClick = { showDeparturePicker = true }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Seasonal Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.seasonalForecast != null) {
                GifImage(
                    resId = uiState.seasonalForecast.iconResId,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = "Select dates to see seasonal outlook",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Great!\nIt'll be ${uiState.seasonalForecast?.seasonName ?: "{season}"} during these days!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        // Skip Button
        TextButton(
            onClick = onSkip,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Skip for now",
                fontSize = 16.sp,
                color = Color.DarkGray,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNext,
            enabled = isDatesFilled,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(56.dp)
                .width(140.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDatesFilled) Color(0xFFF8E7E9) else Color(0xFFF5F5F5),
                contentColor = if (isDatesFilled) Color(0xFF7C110C) else Color.Gray,
                disabledContainerColor = Color(0xFFF5F5F5),
                disabledContentColor = Color.LightGray
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (isDatesFilled) 4.dp else 0.dp,
                pressedElevation = 2.dp,
                hoveredElevation = 6.dp
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Next", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showArrivalPicker) {
        DatePickerModal(
            onDismiss = { showArrivalPicker = false },
            onDateSelected = { 
                onArrivalSelected(it)
                showArrivalPicker = false 
            }
        )
    }

    if (showDeparturePicker) {
        DatePickerModal(
            onDismiss = { showDeparturePicker = false },
            onDateSelected = { 
                onDepartureSelected(it)
                showDeparturePicker = false 
            },
            minDate = uiState.arrivalDate // Ensure departure cannot be before arrival
        )
    }
}

@Composable
fun GifImage(resId: Int, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(resId)
            .build(),
        contentDescription = null,
        imageLoader = imageLoader,
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Fit
    )
}

@Composable
fun DateInput(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(label, color = if (label.contains("Date")) Color.Gray else Color.Black, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDismiss: () -> Unit, 
    onDateSelected: (LocalDate) -> Unit,
    minDate: LocalDate? = null
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                if (minDate == null) return true
                // Convert LocalDate to UTC millis for comparison
                val minMillis = minDate.atStartOfDay(java.time.ZoneId.of("UTC")).toInstant().toEpochMilli()
                return utcTimeMillis >= minMillis
            }
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    // Convert UTC millis back to LocalDate
                    val date = java.time.Instant.ofEpochMilli(it)
                        .atZone(java.time.ZoneId.of("UTC"))
                        .toLocalDate()
                    onDateSelected(date)
                }
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

