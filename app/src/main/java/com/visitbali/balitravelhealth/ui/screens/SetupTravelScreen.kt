package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.viewmodel.TravelUiState
import com.visitbali.balitravelhealth.viewmodel.TravelViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
        onRangeSelected = { start, end -> viewModel.saveAndSyncDates(start, end) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupTravelScreenContent(
    uiState: TravelUiState,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onRangeSelected: (LocalDate, LocalDate) -> Unit
) {
    var showRangePicker by remember { mutableStateOf(false) }
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.setup_travel_title),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.setup_travel_subtitle),
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
                label = uiState.arrivalDate?.format(dateFormatter) ?: stringResource(R.string.setup_travel_placeholder_arrival),
                modifier = Modifier.weight(1f),
                onClick = { showRangePicker = true }
            )

            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.width(20.dp).height(1.dp).background(Color.Gray))
            Spacer(modifier = Modifier.width(16.dp))

            DateInput(
                label = uiState.departureDate?.format(dateFormatter) ?: stringResource(R.string.setup_travel_placeholder_departure),
                modifier = Modifier.weight(1f),
                onClick = { showRangePicker = true }
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
                    text = stringResource(R.string.setup_travel_hint_select_dates),
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.setup_travel_season_headline, uiState.seasonalForecast?.seasonName ?: "{season}"),
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
                text = stringResource(R.string.setup_travel_btn_skip),
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
                Text(stringResource(R.string.btn_next), fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showRangePicker) {
        DateRangePickerModal(
            onDismiss = { showRangePicker = false }
        ) { start, end ->
            onRangeSelected(start, end)
            showRangePicker = false 
        }
    }
}

@Composable
fun GifImage(resId: Int, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                add(ImageDecoderDecoder.Factory())
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
                },
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null
            ) {
                Text(stringResource(R.string.btn_ok))
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
