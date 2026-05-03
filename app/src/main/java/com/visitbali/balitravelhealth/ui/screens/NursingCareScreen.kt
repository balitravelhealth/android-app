package com.visitbali.balitravelhealth.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.dto.Appointment
import com.visitbali.balitravelhealth.data.model.Nurse
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.NursingCareViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NursingCareScreen(
    onBack: () -> Unit,
    onNurseClick: (Nurse) -> Unit,
    viewModel: NursingCareViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    NursingCareContent(
        nurses = uiState.nurses,
        appointment = uiState.appointment,
        onBack = onBack,
        onNurseClick = onNurseClick
    )
    
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.error!!, color = Color.Red)
        }
    }
}

@Composable
fun NurseCard(nurse: Nurse, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Avatar Placeholder using Material3 Icon Box style as requested
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (nurse.profilePhotoUrl != null) {
                    AsyncImage(
                        model = nurse.profilePhotoUrl,
                        contentDescription = nurse.fullName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_nurse_light),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nurse.fullName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Text(
                text = "${nurse.yearsOfExperience} years experience",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "from ${formatCurrency(nurse.ratePerAppointment)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

private fun formatCurrency(amount: BigDecimal): String {
    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return format.format(amount)
}

@Preview(showBackground = true)
@Composable
private fun NursingCareScreenPreview() {
    val mockNurses = listOf(
        Nurse(
            id = "1",
            fullName = "Nurse Jane Doe",
            yearsOfExperience = 5,
            ratePerAppointment = BigDecimal(150000),
            createdAt = "",
            updatedAt = ""
        )
    )
    val mockAppointment = Appointment(
        id = "app_1",
        nurseId = "1",
        nurseName = "Nurse Jane Doe",
        meetingAddress = "Jl. Monkey Forest, Ubud, Bali",
        appointmentDate = "15/05/2026 10:00 AM",
        description = "Need medical checkup",
        nursePhone = "+628123456789",
        nursePhotoUrl = null
    )
    BaliTravelHealthTheme {
        Surface(color = Color.White) {
            NursingCareContent(
                nurses = mockNurses,
                appointment = mockAppointment,
                onBack = {},
                onNurseClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NursingCareContent(
    nurses: List<Nurse>,
    appointment: Appointment?,
    onBack: () -> Unit,
    onNurseClick: (Nurse) -> Unit
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
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Nursing Care Service",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (appointment != null) {
                Text(
                    text = "Your Appointment",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                UserAppointmentCard(appointment = appointment)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(nurses) { nurse ->
                        NurseCard(nurse = nurse, onClick = { onNurseClick(nurse) })
                    }
                }
            }
        }
    }
}

@Composable
fun UserAppointmentCard(appointment: Appointment) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(28.dp))
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Nurse Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (appointment.nursePhotoUrl != null) {
                        AsyncImage(
                            model = appointment.nursePhotoUrl,
                            contentDescription = appointment.nurseName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_nurse_light),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = appointment.nurseName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Address Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.NearMe, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Meeting Point", fontSize = 16.sp, color = Color.Black)
            }
            
            Column(modifier = Modifier.padding(start = 56.dp)) {
                Text(
                    text = appointment.meetingAddress,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Schedule Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Schedule", fontSize = 16.sp, color = Color.Black)
            }
            
            Column(modifier = Modifier.padding(start = 56.dp)) {
                Text(
                    text = appointment.appointmentDate,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Contact Nurse", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                IconButton(
                    onClick = {
                        appointment.nursePhone?.let { phone ->
                            val url = "https://api.whatsapp.com/send?phone=$phone"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_whatsapp), 
                        contentDescription = "WhatsApp",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
