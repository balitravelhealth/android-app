package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.HealthProfileUiState
import com.visitbali.balitravelhealth.viewmodel.HealthProfileViewModel

private val BLOOD_GROUPS = listOf("A", "B", "AB", "O", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
private val GENDER_OPTIONS = listOf("male" to "Male", "female" to "Female")

@Composable
fun HealthProfileScreen(
    onBack: () -> Unit = {},
    viewModel: HealthProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHost.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    HealthProfileContent(
        uiState = uiState,
        snackbarHost = snackbarHost,
        onBack = onBack,
        onEditToggle = { viewModel.toggleEditMode() },
        onCancel = { viewModel.cancelEdit() },
        onSave = { viewModel.saveProfile() },
        onTinggiChange = { viewModel.updateTinggi(it) },
        onBeratChange = { viewModel.updateBerat(it) },
        onGolDarahChange = { viewModel.updateGolonganDarah(it) },
        onAlergiChange = { viewModel.updateRiwayatAlergi(it) },
        onGenderChange = { viewModel.updateJenisKelamin(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HealthProfileContent(
    uiState: HealthProfileUiState,
    snackbarHost: SnackbarHostState,
    onBack: () -> Unit,
    onEditToggle: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onTinggiChange: (String) -> Unit,
    onBeratChange: (String) -> Unit,
    onGolDarahChange: (String) -> Unit,
    onAlergiChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(28.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Your Health Profile",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Used for personalized health recommendations",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    if (uiState.isEditMode) {
                        // — Edit mode —
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HealthField(
                                label = "Height (cm)",
                                value = uiState.tinggiCm,
                                onValueChange = onTinggiChange,
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f),
                            )
                            HealthField(
                                label = "Weight (kg)",
                                value = uiState.beratKg,
                                onValueChange = onBeratChange,
                                keyboardType = KeyboardType.Number,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Blood group dropdown
                        BloodGroupDropdown(
                            current = uiState.golonganDarah,
                            onSelect = onGolDarahChange,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gender dropdown
                        GenderDropdown(
                            current = uiState.jenisKelamin,
                            onSelect = onGenderChange,
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = uiState.riwayatAlergi,
                            onValueChange = onAlergiChange,
                            label = { Text("Allergy History") },
                            placeholder = { Text("e.g. Penicillin, shellfish…") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp),
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = onCancel,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = onSave,
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                enabled = !uiState.isSaving,
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                    )
                                } else {
                                    Text("Save")
                                }
                            }
                        }
                    } else {
                        // — View mode —
                        val profile = uiState.profile

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            HealthStat(
                                label = "Height",
                                value = profile?.tinggiCm?.let { "${it.toInt()} cm" } ?: "—",
                                modifier = Modifier.weight(1f),
                            )
                            HealthStat(
                                label = "Weight",
                                value = profile?.beratKg?.let { "${it.toInt()} kg" } ?: "—",
                                modifier = Modifier.weight(1f),
                            )
                            HealthStat(
                                label = "BMI",
                                value = profile?.let { p ->
                                    val h = p.tinggiCm ?: return@let "—"
                                    val w = p.beratKg ?: return@let "—"
                                    if (h > 0) String.format("%.1f", w / ((h / 100f) * (h / 100f))) else "—"
                                } ?: "—",
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        HealthInfoRow(
                            label = "Blood Group",
                            value = profile?.golonganDarah ?: "Not specified",
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        HealthInfoRow(
                            label = "Gender",
                            value = GENDER_OPTIONS.firstOrNull { it.first == profile?.jenisKelamin }?.second
                                ?: profile?.jenisKelamin ?: "Not specified",
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        HealthInfoRow(
                            label = "Allergy History",
                            value = profile?.riwayatAlergi ?: "None reported",
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onEditToggle,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        ) {
                            Text("Edit Health Profile")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next,
        ),
        shape = RoundedCornerShape(12.dp),
    )
}

@Composable
private fun HealthStat(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun HealthInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BloodGroupDropdown(current: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = current.ifBlank { "Not specified" },
            onValueChange = {},
            readOnly = true,
            label = { Text("Blood Group") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
            shape = RoundedCornerShape(12.dp),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Not specified") }, onClick = { onSelect(""); expanded = false })
            BLOOD_GROUPS.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group) },
                    onClick = { onSelect(group); expanded = false },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderDropdown(current: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = GENDER_OPTIONS.firstOrNull { it.first == current }?.second ?: "Not specified"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Gender") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
            shape = RoundedCornerShape(12.dp),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            GENDER_OPTIONS.forEach { (value, label) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = { onSelect(value); expanded = false },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HealthProfileScreenPreview() {
    BaliTravelHealthTheme {
        HealthProfileContent(
            uiState = HealthProfileUiState(
                tinggiCm = "170",
                beratKg = "65",
                golonganDarah = "O+",
                riwayatAlergi = "Penicillin",
                jenisKelamin = "male",
            ),
            snackbarHost = remember { SnackbarHostState() },
            onBack = {},
            onEditToggle = {},
            onCancel = {},
            onSave = {},
            onTinggiChange = {},
            onBeratChange = {},
            onGolDarahChange = {},
            onAlergiChange = {},
            onGenderChange = {},
        )
    }
}
