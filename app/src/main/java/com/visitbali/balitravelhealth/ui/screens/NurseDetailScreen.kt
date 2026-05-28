package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.viewmodel.NursingCareViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseDetailScreen(
    nurse: Nurse,
    arrivalDate: LocalDate?,
    departureDate: LocalDate?,
    onBack: () -> Unit,
    onBookingComplete: () -> Unit,
    viewModel: NursingCareViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFailureDialog by remember { mutableStateOf(false) }

    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            delay(4000)
            viewModel.resetBookingState()
            onBookingComplete()
        }
    }

    LaunchedEffect(uiState.bookingError) {
        if (uiState.bookingError != null) showFailureDialog = true
    }

    if (uiState.bookingSuccess) {
        BookingSuccessScreen()
    } else {
        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Button(
                        onClick = {
                            if (selectedDate != null) {
                                viewModel.bookAppointment(
                                    nurseId = nurse.id,
                                    tanggalKunjungan = selectedDate!!.format(isoFormatter),
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(28.dp),
                        enabled = selectedDate != null && !uiState.isLoading,
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(stringResource(R.string.nurse_detail_btn_book), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            containerColor = Color.White,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color(0xFFF5F5F5)),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(160.dp),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_nurse_light),
                                contentDescription = null,
                                modifier = Modifier.padding(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.7f), CircleShape),
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.cd_close))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                ) {
                    Text(
                        text = nurse.nama,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    if (!nurse.spesialisasi.isNullOrEmpty()) {
                        Text(
                            text = nurse.spesialisasi,
                            fontSize = 18.sp,
                            color = Color.Gray,
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    DetailActionRow(
                        icon = Icons.Default.CalendarToday,
                        title = stringResource(R.string.label_schedule),
                        subtitle = selectedDate?.format(displayFormatter) ?: stringResource(R.string.nurse_detail_tap_to_select_date),
                        onClick = { showDatePicker = true },
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        NurseDatePickerModal(
            arrivalDate = arrivalDate,
            departureDate = departureDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
        )
    }

    if (showFailureDialog) {
        AlertDialog(
            onDismissRequest = {
                showFailureDialog = false
                viewModel.resetBookingState()
            },
            title = { Text(stringResource(R.string.nurse_detail_booking_failed_title)) },
            text = { Text(uiState.bookingError ?: stringResource(R.string.error_unknown)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showFailureDialog = false
                        viewModel.resetBookingState()
                        if (selectedDate != null) {
                            viewModel.bookAppointment(
                                nurseId = nurse.id,
                                tanggalKunjungan = selectedDate!!.format(isoFormatter),
                            )
                        }
                    },
                ) { Text(stringResource(R.string.nurse_detail_btn_try_again)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showFailureDialog = false
                        viewModel.resetBookingState()
                    },
                ) { Text(stringResource(R.string.btn_cancel)) }
            },
        )
    }
}

@Composable
fun BookingSuccessScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4392DC),
                        Color(0xFFD65AE7),
                        Color(0xFFD9A554),
                        Color(0xFF69D572),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
                .background(Color.White.copy(alpha = 0.4f)),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.checkmark))
                LottieAnimation(
                    composition = composition,
                    iterations = 1,
                    modifier = Modifier.size(80.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.nurse_detail_booking_confirmed),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp,
                style = MaterialTheme.typography.headlineLarge.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.1f),
                        blurRadius = 8f,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun DetailActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
            Text(text = subtitle, fontSize = 14.sp, color = Color.Gray)
        }

        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NurseDatePickerModal(
    arrivalDate: LocalDate?,
    departureDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                val today = LocalDate.now()
                val isAfterOrEqualToday = !date.isBefore(today)
                val isBeforeDeparture = departureDate?.let { !date.isAfter(it) } ?: true
                return isAfterOrEqualToday && isBeforeDeparture
            }
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDate()
                        onDateSelected(date)
                    }
                },
                enabled = datePickerState.selectedDateMillis != null,
            ) { Text(stringResource(R.string.btn_ok)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) } },
    ) {
        DatePicker(state = datePickerState)
    }
}
