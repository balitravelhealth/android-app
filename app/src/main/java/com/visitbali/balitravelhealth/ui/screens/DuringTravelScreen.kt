package com.visitbali.balitravelhealth.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.visitbali.balitravelhealth.data.model.BasicLifeSupportCatalog
import com.visitbali.balitravelhealth.data.model.BasicLifeSupportItem
import com.visitbali.balitravelhealth.data.model.BlsIcon
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.model.FacilityType
import com.visitbali.balitravelhealth.data.model.HealthcareFacility
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.ui.theme.BlsBlue
import com.visitbali.balitravelhealth.ui.theme.HealthcareRed
import com.visitbali.balitravelhealth.viewmodel.FacilityWithDistance
import com.visitbali.balitravelhealth.viewmodel.HealthcareFacilityViewModel
import com.visitbali.balitravelhealth.viewmodel.LifeSupportViewModel
import java.util.Locale

/**
 * Screen shown to travelers while they are in Bali.
 * Implements the Figma design at node 101:239 ("Traveling").
 */

private val FacilityContainerColor = Color(0xFFF0F0F0)
private val LifeSupportCardColor = BlsBlue
private val SubtitleColor = Color(0x75383838) // rgba(56,56,56,0.46)
private val SecondaryTextColor = Color(0xFF525252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuringTravelScreen(
    onBack: () -> Unit,
    onSeeMoreFacilities: () -> Unit = {},
    onNavigateToEmergencyFlows: () -> Unit = {},
    onNavigateToBlsDetail: (String) -> Unit = {},
    viewModel: HealthcareFacilityViewModel,
    lifeSupportViewModel: LifeSupportViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    DuringTravelContent(
        onBack = onBack,
        onSeeMoreFacilities = onSeeMoreFacilities,
        onNavigateToEmergencyFlows = onNavigateToEmergencyFlows,
        onNavigateToBlsDetail = onNavigateToBlsDetail,
        facilities = uiState.facilities.take(4),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DuringTravelContent(
    onBack: () -> Unit,
    onSeeMoreFacilities: () -> Unit,
    onNavigateToEmergencyFlows: () -> Unit,
    onNavigateToBlsDetail: (String) -> Unit,
    facilities: List<FacilityWithDistance>,
) {
    var selectedFacility by remember { mutableStateOf<HealthcareFacility?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 24.dp)
                    .then(if (selectedFacility != null) Modifier.blur(15.dp) else Modifier)
            ) {
                // Title block
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Traveling",
                    fontFamily = FontFamily(Font(R.font.inter_font)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 5.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Everything can be happen. Don’t worry",
                    fontFamily = FontFamily(Font(R.font.instrument_serif_italic)),
                    fontSize = 24.sp,
                    lineHeight = 31.sp,
                    color = SubtitleColor,
                    modifier = Modifier.padding(start = 5.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Health Facility section
                SectionHeader(text = "Health Facility")
                Spacer(modifier = Modifier.height(12.dp))
                HealthFacilitySection(
                    facilities = facilities,
                    onSeeMore = onSeeMoreFacilities,
                    onFacilityClick = { selectedFacility = it }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Basic Life Support section
                SectionHeader(text = "Basic Life Support")
                Spacer(modifier = Modifier.height(16.dp))
                
                // BLS Emergency Flow Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = HealthcareRed),
                    onClick = onNavigateToEmergencyFlows
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.medkit),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = Color.White,
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "EMERGENCY GUIDE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Basic Life Support Guide",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }

                LifeSupportGrid(
                    items = BasicLifeSupportCatalog.items,
                    onItemClick = onNavigateToBlsDetail,
                )
            }
        }

        // Background overlay for blur effect consistency and closing
        if (selectedFacility != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.12f))
                    .clickable { selectedFacility = null }
            )
            
            FacilityDetailDialog(
                facility = selectedFacility!!,
                onDismiss = { selectedFacility = null }
            )
        }
    }
}

@Composable
private fun FacilityDetailDialog(
    facility: HealthcareFacility,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Image with Close Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bali_default),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .padding(16.dp)
                                .size(36.dp)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Black
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_hospital),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = facility.type.name.lowercase(),
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = facility.name,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Speciality : ${facility.specialty}",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Open 24 hours : ${if (facility.isOpen24Hours) "Yes" else "No"}",
                            fontSize = 14.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Info List
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                        ) {
                            InfoRow(icon = Icons.Default.Phone, text = facility.phone)
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                            InfoRow(icon = Icons.Default.Place, text = facility.address)
                            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                            InfoRow(
                                icon = Icons.Default.Language,
                                text = facility.website ?: "No website available",
                                modifier = if (facility.website != null) {
                                    Modifier.clickable {
                                        try {
                                            val url = if (!facility.website.startsWith("http://") && !facility.website.startsWith("https://")) {
                                                "https://${facility.website}"
                                            } else {
                                                facility.website
                                            }
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                        }
                                    }
                                } else Modifier
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(facility.address)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.weight(1.1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Get Directions", fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${facility.phone}"))
                                    context.startActivity(dialIntent)
                                },
                                modifier = Modifier.weight(0.9f),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D576B)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Facility", fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontFamily = FontFamily(Font(R.font.inter_font)),
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 31.sp,
        color = Color.Black,
        modifier = Modifier.padding(start = 5.dp)
    )
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HealthFacilitySection(
    facilities: List<FacilityWithDistance>,
    onSeeMore: () -> Unit,
    onFacilityClick: (HealthcareFacility) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = FacilityContainerColor,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (facilities.isEmpty()) {
                Text(
                    text = "No facilities loaded yet",
                    modifier = Modifier.padding(18.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            } else {
                facilities.forEach { item ->
                    HealthFacilityRow(
                        item = item,
                        onClick = { onFacilityClick(item.facility) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1F1F1F),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { onSeeMore() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Show more facilities",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthFacilityRow(
    item: FacilityWithDistance,
    onClick: () -> Unit
) {
    val facility = item.facility
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(35.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hospital icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hospital),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = facility.name,
                    fontFamily = FontFamily(Font(R.font.inter_font)),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = facility.address,
                    fontFamily = FontFamily(Font(R.font.inter_font)),
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = SecondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val distanceText = if (item.distanceKm != null) {
                String.format(Locale.getDefault(), "%.1f km", item.distanceKm)
            } else ""

            Text(
                text = distanceText,
                fontFamily = FontFamily(Font(R.font.inter_font)),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = SecondaryTextColor
            )
        }
    }
}

@Composable
private fun LifeSupportGrid(
    items: List<BasicLifeSupportItem>,
    onItemClick: (String) -> Unit,
) {
    val rows = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                rowItems.forEach { item ->
                    LifeSupportCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(item.id) },
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LifeSupportCard(
    item: BasicLifeSupportItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .aspectRatio(1.05f)
            .clickable(onClick = onClick),
        color = LifeSupportCardColor,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            Icon(
                blsIcon(item.icon),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = item.title,
                fontFamily = FontFamily(Font(R.font.inter_font)),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 22.sp,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicLifeSupportDetailScreen(
    itemId: String,
    onBack: () -> Unit,
) {
    val item = BasicLifeSupportCatalog.find(itemId) ?: BasicLifeSupportCatalog.items.first()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                modifier = Modifier.size(128.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        blsIcon(item.icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(72.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(item.title, fontSize = 30.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Step-by-step guidance coming soon.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun blsIcon(icon: BlsIcon) = when (icon) {
    BlsIcon.Heart -> Icons.Default.Favorite
    BlsIcon.Wind -> Icons.Default.Air
    BlsIcon.Bandage -> Icons.Default.MedicalServices
    BlsIcon.Flame -> Icons.Default.LocalFireDepartment
    BlsIcon.Fall -> Icons.Default.Accessibility
    BlsIcon.Shock -> Icons.Default.ElectricBolt
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun DuringTravelScreenPreview() {
    val mockFacilities = listOf(
        FacilityWithDistance(
            facility = HealthcareFacility(
                name = "Prof. Ngoerah Hospital",
                officialName = "RSUP Prof. Dr. I.G.N.G. Ngoerah",
                specialty = "Heart Care / Kardiologi",
                type = FacilityType.GOVERNMENT,
                address = "Jl. Diponegoro No. 45, Denpasar, Bali",
                phone = "+62 361 227911",
                latitude = -8.6684,
                longitude = 115.2190
            ),
            distanceKm = 2.5
        )
    )

    BaliTravelHealthTheme {
        DuringTravelContent(
            onBack = {},
            onSeeMoreFacilities = {},
            onNavigateToEmergencyFlows = {},
            onNavigateToBlsDetail = {},
            facilities = mockFacilities,
        )
    }
}
