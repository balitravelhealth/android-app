package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.hbb20.countrypicker.models.CPCountry
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.viewmodel.SetupViewModel
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.ui.theme.Red40
import com.visitbali.balitravelhealth.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SetupScreen(
    viewModel: SetupViewModel = viewModel(),
    onBackClick: () -> Unit = {}, 
    onComplete: () -> Unit = {}
) {
    val context = LocalContext.current

    SetupScreenContent(
        onBackClick = onBackClick,
        onSaveProfile = { name, country, dob, gender ->
            viewModel.saveUserProfile( name, country, dob, gender, onComplete)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreenContent(
    onBackClick: () -> Unit = {},
    onSaveProfile: (String, String, String, String) -> Unit = { _, _, _, _ -> }
) {
    val countryPlaceholder = stringResource(R.string.setup_placeholder_country)
    val dobPlaceholder = stringResource(R.string.setup_placeholder_dob)
    val genderPlaceholder = stringResource(R.string.setup_placeholder_gender)

    var name by remember { mutableStateOf("") }
    var country by remember { mutableStateOf(countryPlaceholder) }
    var dob by remember { mutableStateOf(dobPlaceholder) }
    var gender by remember { mutableStateOf(genderPlaceholder) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showGenderDialog by remember { mutableStateOf(false) }

    val todayMillis = remember { System.currentTimeMillis() }
    val minDobMillis = remember {
        java.time.LocalDate.now()
            .minusYears(120)
            .atStartOfDay(java.time.ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis in minDobMillis..todayMillis
        }
    )
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val isFormComplete = name.isNotBlank() && 
            country != countryPlaceholder && 
            dob != dobPlaceholder && 
            gender != genderPlaceholder

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = stringResource(R.string.setup_title),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.setup_subtitle),
            fontSize = 12.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        SetupTextField(value = name, onValueChange = { if (it.length <= 64) name = it }, placeholder = stringResource(R.string.setup_hint_name))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Country Selection
        SetupDropdown(label = country, onClick = { 
            focusManager.clearFocus()
            context.launchCountryPickerDialog { selectedCountry: CPCountry? ->
                if (selectedCountry != null) {
                    country = selectedCountry.name
                }
            }
        })
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SetupDropdown(label = dob, onClick = { 
            focusManager.clearFocus()
            showDatePicker = true 
        })
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SetupDropdown(label = gender, onClick = { 
            focusManager.clearFocus()
            showGenderDialog = true 
        })

        Spacer(modifier = Modifier.weight(1f))
        
        ElevatedButton(
            onClick = {
                onSaveProfile(name, country, dob, gender)
            },
            enabled = isFormComplete,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(56.dp)
                .width(114.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = if (isFormComplete) Color(0xFFF8E7E9) else Color(0xFFF5F5F5),
                contentColor = if (isFormComplete) Red40 else Color.Gray,
                disabledContainerColor = Color(0xFFF5F5F5),
                disabledContentColor = Color.LightGray
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = if (isFormComplete) 4.dp else 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.btn_next),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dob = formatter.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.btn_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Gender Dialog
    if (showGenderDialog) {
        AlertDialog(
            onDismissRequest = { showGenderDialog = false },
            title = { Text(stringResource(R.string.setup_dialog_select_gender), color = Color.Black) },
            text = {
                val male = stringResource(R.string.setup_gender_male)
                val female = stringResource(R.string.setup_gender_female)
                Column {
                    listOf(male, female).forEach { item ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (item == gender),
                                    onClick = {
                                        gender = item
                                        showGenderDialog = false
                                    }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (item == gender),
                                onClick = {
                                    gender = item
                                    showGenderDialog = false
                                }
                            )
                            Text(text = item, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGenderDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color.Gray) },
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 16.sp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE0E0E0),
            unfocusedContainerColor = Color(0xFFE0E0E0),
            disabledContainerColor = Color(0xFFE0E0E0),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupDropdown(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.Black)
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetupScreenPreview() {
    BaliTravelHealthTheme {
        SetupScreenContent()
    }
}
