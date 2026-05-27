package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.dto.NursingRecord
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.viewmodel.NursingCareViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursingCareScreen(
    onBack: () -> Unit,
    onNurseClick: (Nurse) -> Unit,
    onRecordsClick: () -> Unit = {},
    viewModel: NursingCareViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNurses()
    }

    NursingCareContent(
        nurses = uiState.nurses,
        latestRecord = uiState.latestRecord,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onBack = onBack,
        onRecordsClick = onRecordsClick,
        onNurseClick = onNurseClick,
    )
}

@Composable
fun NurseCard(nurse: Nurse, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nurse_light),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nurse.nama,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black,
            )

            if (!nurse.spesialisasi.isNullOrEmpty()) {
                Text(
                    text = nurse.spesialisasi,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NursingCareContent(
    nurses: List<Nurse>,
    latestRecord: NursingRecord?,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onRecordsClick: () -> Unit,
    onNurseClick: (Nurse) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        containerColor = Color.White,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
            ) {
                Text(
                    text = "Nursing Care Service",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
                TextButton(onClick = onRecordsClick, modifier = Modifier.align(Alignment.Start)) {
                    Text("View care records")
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (latestRecord != null) {
                    Text(
                        text = "Your Appointment",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LatestRecordCard(record = latestRecord)
                } else if (isLoading && nurses.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Loading nurses...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else if (error != null && nurses.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Couldn't load nurses", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(text = error, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                } else if (nurses.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No nurses available", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Pull down to refresh, or check back soon.", color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(nurses) { nurse ->
                            NurseCard(nurse = nurse, onClick = { onNurseClick(nurse) })
                        }
                    }
                }
            }

            if (isLoading && nurses.isNotEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun LatestRecordCard(record: NursingRecord) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(28.dp)),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Nursing Record",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Visit Date", fontSize = 14.sp, color = Color.Gray)
                    Text(text = record.tanggalKunjungan, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            }

            if (!record.nursingAssessment.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Assessment", fontSize = 14.sp, color = Color.Gray)
                Text(text = record.nursingAssessment, fontSize = 15.sp, color = Color.Black)
            }

            if (!record.nursingDiagnosis.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Diagnosis", fontSize = 14.sp, color = Color.Gray)
                Text(text = record.nursingDiagnosis, fontSize = 15.sp, color = Color.Black)
            }
        }
    }
}
