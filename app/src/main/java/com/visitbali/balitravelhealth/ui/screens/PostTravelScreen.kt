package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.TravelUiState
import com.visitbali.balitravelhealth.viewmodel.TravelViewModel

import androidx.compose.ui.res.stringResource
import com.visitbali.balitravelhealth.R

@Composable
fun PostTravelScreen(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAssessment: () -> Unit = {},
    viewModel: TravelViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    PostTravelContent(
        uiState = uiState,
        onBack = onBack,
        onNavigateToHome = onNavigateToHome,
        onNavigateToGuide = onNavigateToGuide,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToAssessment = onNavigateToAssessment,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTravelContent(
    uiState: TravelUiState,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAssessment: () -> Unit
) {
    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            BaliNavigationBar(
                initialSelectedItem = 0,
                onHomeClick = onNavigateToHome,
                onGuideClick = onNavigateToGuide,
                onProfileClick = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
            Surface(
                onClick = onBack,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Header
            Text(
                text = stringResource(R.string.post_travel_header_arriving_home),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = Color.White
                )
            )
            Text(
                text = stringResource(R.string.post_travel_header_subtitle),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = FontStyle.Italic,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Advice Section
            SectionHeader(title = stringResource(R.string.label_advice))
            Spacer(modifier = Modifier.height(12.dp))
            val latestAssessment = uiState.latestAssessment
            val adviceTitle = latestAssessment?.diagnosis ?: stringResource(R.string.post_travel_advice_default_title)
            val adviceContent = latestAssessment?.recommendation ?: stringResource(R.string.post_travel_advice_default_desc)
            AdviceCard(
                title = adviceTitle,
                description = adviceContent
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tools Section
            SectionHeader(title = stringResource(R.string.label_tools))
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ToolCard(
                    title = stringResource(R.string.post_travel_tool_health_screening),
                    icon = Icons.Default.Checklist,
                    color = Color(0xFF1E6AF3),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAssessment
                )
                // Spacer for the 2nd slot in the grid as per design
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 22.sp
        )
    )
}

@Composable
private fun AdviceCard(title: String, description: String) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF8C82))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun ToolCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(44.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

@Preview
@Composable
private fun PostTravelScreenPreview() {
    BaliTravelHealthTheme {
        PostTravelContent(
            uiState = TravelUiState(),
            onBack = {},
            onNavigateToAssessment = {}
        )
    }
}
