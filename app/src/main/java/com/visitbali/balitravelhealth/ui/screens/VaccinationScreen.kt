package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.dto.Vaccination
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.VaccinationUiState
import com.visitbali.balitravelhealth.viewmodel.VaccinationViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun VaccinationScreen(
    onBack: () -> Unit = {},
    viewModel: VaccinationViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    VaccinationContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onAddClick = { showAddDialog = true },
        onDelete = { viewModel.deleteVaccination(it) },
    )

    if (showAddDialog) {
        AddVaccinationDialog(
            isSubmitting = uiState.isSubmitting,
            onDismiss = { showAddDialog = false },
            onConfirm = { jenis, tanggal, dosis, catatan ->
                viewModel.addVaccination(jenis, tanggal, dosis, catatan)
                showAddDialog = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VaccinationContent(
    uiState: VaccinationUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onDelete: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccination Record") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFFD49110),
                contentColor = Color.White,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add vaccination")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White,
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color(0xFFD49110))
                }
            }
            uiState.vaccinations.isEmpty() -> {
                EmptyVaccinationState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    onAddClick = onAddClick,
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.vaccinations, key = { it.id }) { vaccination ->
                        VaccinationCard(
                            vaccination = vaccination,
                            onDelete = { onDelete(vaccination.id) },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun VaccinationCard(
    vaccination: Vaccination,
    onDelete: () -> Unit,
) {
    val displayDate = runCatching {
        LocalDate.parse(vaccination.tanggal)
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    }.getOrDefault(vaccination.tanggal)

    var showConfirmDelete by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_vaccine),
                contentDescription = null,
                tint = Color(0xFFD49110),
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vaccination.jenisVaksin,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
                val dosis = vaccination.dosis
                if (!dosis.isNullOrBlank()) {
                    Text(
                        text = "Dose: $dosis",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD49110),
                    )
                }
                val catatan = vaccination.catatan
                if (!catatan.isNullOrBlank()) {
                    Text(
                        text = catatan,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                }
            }
            IconButton(onClick = { showConfirmDelete = true }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray,
                )
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Remove Vaccination") },
            text = {
                Text("Remove \"${vaccination.jenisVaksin}\" from your records?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDelete = false
                    onDelete()
                }) {
                    Text("Remove", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun EmptyVaccinationState(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_vaccine),
            contentDescription = null,
            tint = Color(0xFFD49110).copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No vaccinations recorded",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color.DarkGray,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Keep track of your vaccinations\nfor a safer trip to Bali.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD49110)),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Vaccination")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddVaccinationDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (jenisVaksin: String, tanggal: String, dosis: String?, catatan: String?) -> Unit,
) {
    var jenisVaksin by remember { mutableStateOf("") }
    var tanggal by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            tanggal = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null,
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState, showModeToggle = false)
        }
        return // Don't render the AlertDialog while the date picker is open
    }

    val displayDate = if (tanggal.isNotEmpty()) {
        runCatching {
            LocalDate.parse(tanggal)
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
        }.getOrDefault(tanggal)
    } else ""

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text("Add Vaccination") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = jenisVaksin,
                    onValueChange = { jenisVaksin = it },
                    label = { Text("Vaccine Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                    shape = RoundedCornerShape(12.dp),
                )
                OutlinedTextField(
                    value = displayDate,
                    onValueChange = {},
                    label = { Text("Date *") },
                    readOnly = true,
                    singleLine = true,
                    placeholder = { Text("Pick a date") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text("Pick")
                        }
                    },
                )
                OutlinedTextField(
                    value = dosis,
                    onValueChange = { dosis = it },
                    label = { Text("Dose (e.g. 1st, Booster)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(12.dp),
                )
                OutlinedTextField(
                    value = catatan,
                    onValueChange = { catatan = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        jenisVaksin,
                        tanggal,
                        dosis.ifBlank { null },
                        catatan.ifBlank { null },
                    )
                },
                enabled = jenisVaksin.isNotBlank() && tanggal.isNotBlank() && !isSubmitting,
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun VaccinationScreenPreview() {
    BaliTravelHealthTheme {
        VaccinationContent(
            uiState = VaccinationUiState(
                vaccinations = listOf(
                    Vaccination(1, 1, "Hepatitis A", "2024-03-15", "1st dose", "Pre-travel", "2024-03-15T00:00:00Z"),
                    Vaccination(2, 1, "Typhoid", "2024-03-15", null, null, "2024-03-15T00:00:00Z"),
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onAddClick = {},
            onDelete = {},
        )
    }
}
