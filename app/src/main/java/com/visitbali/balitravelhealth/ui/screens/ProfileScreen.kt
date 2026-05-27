package com.visitbali.balitravelhealth.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.ProfileUiState
import com.visitbali.balitravelhealth.viewmodel.ProfileViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToHealthProfile: () -> Unit = {},
    onLoggedOut: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showPhotoPickerOptions by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateProfilePicture(it) }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BaliNavigationBar(
                initialSelectedItem = 2,
                onHomeClick = onNavigateToHome,
                onGuideClick = onNavigateToGuide,
                onProfileClick = {}
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            ProfileContent(
                uiState = uiState,
                onEditToggle = { viewModel.toggleEditMode() },
                onCancel = { viewModel.cancelEdit() },
                onSave = { viewModel.saveProfile() },
                onUpdateName = { viewModel.updateName(it) },
                onUpdateDob = { viewModel.updateDob(it) },
                onUpdateCountry = { viewModel.updateCountry(it) },
                onUpdateGender = { viewModel.updateGender(it) },
                onPhotoClick = { if (uiState.isEditMode) showPhotoPickerOptions = true },
                onLogoutClick = { showLogoutDialog = true },
                onHealthProfileClick = onNavigateToHealthProfile,
            )
            
            if (showPhotoPickerOptions) {
                AlertDialog(
                    onDismissRequest = { showPhotoPickerOptions = false },
                    title = { Text("Update Profile Picture") },
                    text = { Text("Select a source") },
                    confirmButton = {
                        TextButton(onClick = { 
                            galleryLauncher.launch("image/*")
                            showPhotoPickerOptions = false 
                        }) {
                            Text("Gallery")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPhotoPickerOptions = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Logout") },
                    text = { Text("Are you sure you want to log out?") },
                    confirmButton = {
                        TextButton(onClick = { 
                            showLogoutDialog = false 
                            viewModel.logout(onLoggedOut)
                        }) {
                            Text("Logout", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onEditToggle: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateDob: (String) -> Unit,
    onUpdateCountry: (String) -> Unit,
    onUpdateGender: (String) -> Unit,
    onPhotoClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onHealthProfileClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(3.dp, Color.White, CircleShape)
                    .clickable { onPhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.profilePictureUri != null) {
                    AsyncImage(
                        model = uiState.profilePictureUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = initials(uiState.name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(uiState.name.ifBlank { "Traveler" }, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                val details = listOfNotNull(
                    ageText(uiState.dob).takeIf { it.isNotBlank() },
                    uiState.country.takeIf { it.isNotBlank() },
                ).joinToString(" • ")
                Text(details.ifBlank { "Complete your profile" }, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AssistChip(
                onClick = onEditToggle,
                label = { Text(if (uiState.isEditMode) "Editing" else "Edit") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp)) },
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        if (uiState.isEditMode) {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(Modifier.padding(20.dp)) {
                    ProfileField(label = "Name", value = uiState.name, isEditable = true, onValueChange = onUpdateName)
                    ProfileField(label = "Date of Birth", value = uiState.dob, isEditable = true, onValueChange = onUpdateDob)
                    ProfileField(label = "Country of Residence", value = uiState.country, isEditable = true, onValueChange = onUpdateCountry)
                    ProfileField(label = "Gender", value = uiState.gender, isEditable = true, onValueChange = onUpdateGender)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(26.dp)) {
                            Text("Cancel")
                        }
                        Button(onClick = onSave, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(26.dp)) {
                            if (uiState.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp)) else Text("Save")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Text("Account", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Email", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(uiState.email.ifBlank { "Not available" }, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFE85745), Color(0xFF9F2C22))
                        )
                    )
                    .padding(22.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.gapura), contentDescription = null, modifier = Modifier.size(38.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Bali Travel Health", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Icon(
                        imageVector = if (uiState.hasCompletedHealthRiskAssessment) {
                            Icons.Default.Verified
                        } else {
                            Icons.Default.Warning
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        uiState.name.ifBlank { "Traveler" },
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 14.sp,
                    )
                    Text(
                        if (uiState.hasCompletedHealthRiskAssessment) {
                            "Cleared for Bali!"
                        } else {
                            "Please take Health Risk Assessment first"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TravelDatePill(uiState.arrivalDate ?: "Arrival")
                        TravelDatePill(uiState.departureDate ?: "Departure")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onHealthProfileClick, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(27.dp)) {
            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Health Profile")
        }

        TextButton(onClick = onLogoutClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun TravelDatePill(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.18f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.26f)),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}

private fun initials(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return parts.take(2).joinToString("") { it.first().uppercase() }.ifBlank { "?" }
}

private fun ageText(dob: String): String {
    val iso = com.visitbali.balitravelhealth.data.util.ProfileFormatters.toApiDate(dob)
    return runCatching {
        val birth = java.time.LocalDate.parse(iso)
        "${java.time.Period.between(birth, java.time.LocalDate.now()).years} years"
    }.getOrDefault("")
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        if (isEditable) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Black,
                    focusedIndicatorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = true
            )
        } else {
            Text(text = value.ifEmpty { "{unspecified}" }, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = Color.Black, thickness = 1.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    BaliTravelHealthTheme {
        Scaffold(
            bottomBar = {
                BaliNavigationBar(initialSelectedItem = 2)
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                ProfileContent(
                    uiState = ProfileUiState(
                        name = "John Doe",
                        dob = "01/01/1990",
                        country = "United Kingdom",
                        gender = "Male"
                    ),
                    onEditToggle = {},
                    onCancel = {},
                    onSave = {},
                    onUpdateName = {},
                    onUpdateDob = {},
                    onUpdateCountry = {},
                    onUpdateGender = {},
                    onPhotoClick = {},
                    onLogoutClick = {}
                )
            }
        }
    }
}
