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
                onLogoutClick = { showLogoutDialog = true }
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
    onLogoutClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Header Image
        Image(
            painter = painterResource(id = R.drawable.bali_default),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentScale = ContentScale.Crop
        )

        // Top Controls
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Black)
            }
        }

        // Main Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 240.dp, start = 24.dp, end = 24.dp, bottom = 48.dp)
                .fillMaxHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 64.dp, bottom = 24.dp)
            ) {
                ProfileField(label = "Name", value = uiState.name, isEditable = uiState.isEditMode, onValueChange = onUpdateName)
                ProfileField(label = "Date of Birth", value = uiState.dob, isEditable = uiState.isEditMode, onValueChange = onUpdateDob)
                ProfileField(label = "Nationality", value = uiState.country, isEditable = uiState.isEditMode, onValueChange = onUpdateCountry)
                ProfileField(label = "Gender", value = uiState.gender, isEditable = uiState.isEditMode, onValueChange = onUpdateGender)

                Spacer(modifier = Modifier.height(20.dp))

                if (uiState.isEditMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = onSave,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Save")
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = onEditToggle,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Edit Profile")
                    }
                }
            }
        }

        // Profile Picture (Floating over card)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 180.dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .border(4.dp, Color.White, CircleShape)
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_nurse_light), // Placeholder
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.Gray
                )
            }
            
            if (uiState.isEditMode) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
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
