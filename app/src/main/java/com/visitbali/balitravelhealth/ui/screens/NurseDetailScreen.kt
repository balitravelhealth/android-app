package com.visitbali.balitravelhealth.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
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
    viewModel: NursingCareViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var meetingAddress by remember { mutableStateOf("Address") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var description by remember { mutableStateOf("") }
    
    var showMapModal by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showFailureDialog by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Handle Success Navigation
    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            delay(4000)
            viewModel.resetBookingState()
            onBookingComplete()
        }
    }

    // Handle Error Dialog
    LaunchedEffect(uiState.bookingError) {
        if (uiState.bookingError != null) {
            showFailureDialog = true
        }
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
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (selectedDate != null && meetingAddress != "Address") {
                                viewModel.bookAppointment(
                                    nurseId = nurse.id,
                                    meetingAddress = meetingAddress,
                                    appointmentDate = selectedDate!!.format(dateFormatter),
                                    description = description
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(28.dp),
                        enabled = selectedDate != null && meetingAddress != "Address" && !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Book", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            containerColor = Color.White
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Header Image/Avatar Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFF5F5F5))
                ) {
                    if (nurse.profilePhotoUrl != null) {
                        AsyncImage(
                            model = nurse.profilePhotoUrl,
                            contentDescription = nurse.fullName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(160.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_nurse_light),
                                    contentDescription = null,
                                    modifier = Modifier.padding(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = nurse.fullName,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${nurse.yearsOfExperience} years experience",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Rows
                    DetailActionRow(
                        icon = Icons.Default.NearMe,
                        title = "Where to meet?",
                        subtitle = meetingAddress,
                        onClick = { showMapModal = true }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(8.dp))

                    DetailActionRow(
                        icon = Icons.Default.CalendarToday,
                        title = "Schedule",
                        subtitle = selectedDate?.format(dateFormatter) ?: "DD/MM/YYYY Time",
                        onClick = { showDatePicker = true }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Description",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 255) description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        placeholder = { Text("Describe your condition...") },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Text(
                        text = "${description.length}/255",
                        modifier = Modifier.align(Alignment.End),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }

    if (showMapModal) {
        MapPinModal(
            onDismiss = { showMapModal = false },
            onAddressConfirmed = { address ->
                meetingAddress = address
                showMapModal = false
            }
        )
    }

    if (showDatePicker) {
        NurseDatePickerModal(
            arrivalDate = arrivalDate,
            departureDate = departureDate,
            onDismiss = { showDatePicker = false },
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            }
        )
    }

    if (showFailureDialog) {
        AlertDialog(
            onDismissRequest = { 
                showFailureDialog = false
                viewModel.resetBookingState()
            },
            title = { Text("Appointment Failed") },
            text = { Text(uiState.bookingError ?: "Unknown error occurred") },
            confirmButton = {
                TextButton(onClick = {
                    showFailureDialog = false
                    viewModel.resetBookingState()
                    if (selectedDate != null && meetingAddress != "Address") {
                        viewModel.bookAppointment(
                            nurseId = nurse.id,
                            meetingAddress = meetingAddress,
                            appointmentDate = selectedDate!!.format(dateFormatter),
                            description = description
                        )
                    }
                }) {
                    Text("Try again")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showFailureDialog = false
                    viewModel.resetBookingState()
                }) {
                    Text("Cancel")
                }
            }
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
                        Color(0xFF69D572)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Blur effect for the colorful background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(80.dp)
                .background(Color.White.copy(alpha = 0.4f))
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.checkmark))
                LottieAnimation(
                    composition = composition,
                    iterations = 1,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Appointment\nConfirmed",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp,
                style = MaterialTheme.typography.headlineLarge.copy(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.1f),
                        blurRadius = 8f
                    )
                )
            )
        }
    }
}

@Composable
private fun DetailActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black, CircleShape),
            contentAlignment = Alignment.Center
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

@SuppressLint("MissingPermission")
@Composable
private fun MapPinModal(
    onDismiss: () -> Unit,
    onAddressConfirmed: (String) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var addressInput by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    userLocation = latLng
                    markerPosition = latLng
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    userLocation = latLng
                    markerPosition = latLng
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    if (userLocation != null) {
                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
                        }
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            onMapClick = { markerPosition = it }
                        ) {
                            markerPosition?.let {
                                Marker(state = MarkerState(position = it))
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.White, CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text("Enter Detailed Address", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Hotel name, room number...") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onAddressConfirmed(addressInput) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        enabled = addressInput.isNotBlank()
                    ) {
                        Text("Confirm Location", color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NurseDatePickerModal(
    arrivalDate: LocalDate?,
    departureDate: LocalDate?,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                val today = LocalDate.now()
                
                // Allow from today up to departure date
                val isAfterOrEqualToday = !date.isBefore(today)
                val isBeforeDeparture = departureDate?.let { !date.isAfter(it) } ?: true
                
                return isAfterOrEqualToday && isBeforeDeparture
            }
        }
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
                enabled = datePickerState.selectedDateMillis != null
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
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NurseDetailScreenPreview() {
    val mockNurse = Nurse(
        id = "1",
        fullName = "Nurse Jane Doe",
        yearsOfExperience = 5,
        ratePerAppointment = java.math.BigDecimal(150000),
        createdAt = "",
        updatedAt = "",
        profilePhotoUrl = null
    )

}
