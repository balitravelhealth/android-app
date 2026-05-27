package com.visitbali.balitravelhealth.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.visitbali.balitravelhealth.data.model.FacilityType
import com.visitbali.balitravelhealth.data.model.HealthcareFacility
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.FacilityUiState
import com.visitbali.balitravelhealth.viewmodel.FacilityWithDistance
import com.visitbali.balitravelhealth.viewmodel.HealthcareFacilityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareScreen(
    viewModel: HealthcareFacilityViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    HealthcareContent(
        uiState = uiState,
        onBack = onBack,
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        onClearSearch = { viewModel.clearSearch() },
        onFilterSelected = { viewModel.setTypeFilter(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareContent(
    uiState: FacilityUiState,
    onBack: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterSelected: (FacilityType?) -> Unit
) {
    var isMapView by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Healthcare Facilities", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isMapView = !isMapView }) {
                        Icon(
                            imageVector = if (isMapView) Icons.Default.Search else Icons.Default.Map,
                            contentDescription = if (isMapView) "List View" else "Map View"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChanged,
                onClear = onClearSearch
            )

            // Filter Chips
            FilterChips(
                activeFilter = uiState.activeFilter,
                onFilterSelected = onFilterSelected
            )

            if (isMapView) {
                HealthcareMapView(facilities = uiState.facilities)
            } else {
                HealthcareListView(facilities = uiState.facilities)
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search by name, specialty, or area...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

@Composable
fun FilterChips(
    activeFilter: FacilityType?,
    onFilterSelected: (FacilityType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = activeFilter == null,
            onClick = { onFilterSelected(null) },
            label = { Text("All") }
        )
        FacilityType.values().forEach { type ->
            FilterChip(
                selected = activeFilter == type,
                onClick = { onFilterSelected(type) },
                label = { 
                    Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) 
                }
            )
        }
    }
}

@Composable
fun HealthcareListView(facilities: List<FacilityWithDistance>) {
    if (facilities.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No facilities found", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(facilities) { item ->
                FacilityCard(item)
            }
        }
    }
}

@Composable
fun FacilityCard(item: FacilityWithDistance) {
    val facility = item.facility
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = facility.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = facility.specialty,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Badge(
                    containerColor = when (facility.type) {
                        FacilityType.GOVERNMENT -> Color(0xFFE8F5E9)
                        FacilityType.PRIVATE -> Color(0xFFE3F2FD)
                        FacilityType.CLINIC -> Color(0xFFFFF3E0)
                    },
                    contentColor = when (facility.type) {
                        FacilityType.GOVERNMENT -> Color(0xFF2E7D32)
                        FacilityType.PRIVATE -> Color(0xFF1565C0)
                        FacilityType.CLINIC -> Color(0xFFEF6C00)
                    }
                ) {
                    Text(
                        text = facility.type.name,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = facility.address,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (item.distanceKm != null) {
                Text(
                    text = String.format("%.1f km away", item.distanceKm),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
            }

            if (facility.hoursSummary != null) {
                Text(
                    text = facility.hoursSummary,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${facility.phone}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:${facility.latitude},${facility.longitude}?q=${Uri.encode(facility.name)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Map", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun HealthcareMapView(facilities: List<FacilityWithDistance>) {
    val bali = LatLng(-8.4095, 115.1889)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bali, 10f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false) // Permission handled in Home
    ) {
        facilities.forEach { item ->
            val facility = item.facility
            Marker(
                state = MarkerState(position = LatLng(facility.latitude, facility.longitude)),
                title = facility.name,
                snippet = facility.specialty
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthcareScreenPreview() {
    val mockFacilities = listOf(
        FacilityWithDistance(
            facility = HealthcareFacility(
                id = 1,
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
        ),
        FacilityWithDistance(
            facility = HealthcareFacility(
                id = 2,
                name = "Siloam Hospital Bali",
                officialName = "Siloam Hospitals Bali",
                specialty = "Orthopedics / Ortopedi",
                type = FacilityType.PRIVATE,
                address = "Jl. Sunset Road No. 818, Kuta, Bali",
                phone = "+62 361 779900",
                latitude = -8.7109,
                longitude = 115.1705
            ),
            distanceKm = 5.2
        )
    )

    BaliTravelHealthTheme {
        HealthcareContent(
            uiState = FacilityUiState(
                facilities = mockFacilities,
                isLoading = false,
                activeFilter = null,
                searchQuery = ""
            ),
            onBack = {},
            onSearchQueryChanged = {},
            onClearSearch = {},
            onFilterSelected = {}
        )
    }
}
