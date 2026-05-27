package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.EmergencyShare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.dto.EmergencyGuideFlowSummary
import com.visitbali.balitravelhealth.data.model.GuideCategory
import com.visitbali.balitravelhealth.data.model.GuideStep
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.GuideUiState
import com.visitbali.balitravelhealth.viewmodel.GuideViewModel
import kotlin.math.min

@Composable
fun GuideScreen(
    onNavigateToHome: () -> Unit = {},
    onFlowClick: (Int) -> Unit = {},
    onGuideClick: (String) -> Unit = {},
    viewModel: GuideViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    GuideScreenContent(
        uiState = uiState,
        onNavigateToHome = onNavigateToHome,
        onFlowClick = onFlowClick,
        onGuideClick = onGuideClick,
        onRetry = viewModel::refresh,
    )
}

@Composable
private fun GuideScreenContent(
    uiState: GuideUiState,
    onNavigateToHome: () -> Unit = {},
    onFlowClick: (Int) -> Unit = {},
    onGuideClick: (String) -> Unit = {},
    onRetry: () -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    val headerHeight = 300.dp
    val scrollOffset by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                1000f
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BaliNavigationBar(
                initialSelectedItem = 1,
                onHomeClick = onNavigateToHome,
                onGuideClick = {},
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            GuideHeader(
                modifier = Modifier.graphicsLayer {
                    translationY = -scrollOffset * 0.35f
                    alpha = min(1f, 1f - (scrollOffset / 620f))
                },
            )

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                item { Spacer(modifier = Modifier.height(headerHeight)) }

                item {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            if (uiState.error != null) {
                                GuideErrorBanner(message = uiState.error, onRetry = onRetry)
                            }

                            if (uiState.isLoading && uiState.content.flows.isEmpty() && uiState.content.categories.isEmpty()) {
                                GuideLoadingBlock()
                            }
                        }
                    }
                }

                if (uiState.basicLifeSupportFlows.isNotEmpty()) {
                    item {
                        GuideSectionHeader(
                            title = "Basic Life Support",
                            subtitle = "Endpoint content from emergency guide flows",
                        )
                    }
                    items(uiState.basicLifeSupportFlows, key = { "bls-${it.id}" }) { flow ->
                        GuideFlowCard(
                            flow = flow,
                            accent = MaterialTheme.colorScheme.primary,
                            onClick = { onFlowClick(flow.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        )
                    }
                }

                if (uiState.emergencyFlows.isNotEmpty()) {
                    item {
                        GuideSectionHeader(
                            title = "Emergency Decision Guides",
                            subtitle = "Interactive, step-by-step flows",
                        )
                    }
                    items(uiState.emergencyFlows, key = { "flow-${it.id}" }) { flow ->
                        GuideFlowCard(
                            flow = flow,
                            accent = MaterialTheme.colorScheme.tertiary,
                            onClick = { onFlowClick(flow.id) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        )
                    }
                }

                if (uiState.content.categories.isNotEmpty()) {
                    item {
                        GuideSectionHeader(
                            title = "Emergency Guide Steps",
                            subtitle = "Sequential guides fetched from the backend",
                        )
                    }
                    items(uiState.content.categories, key = { "category-${it.id}" }) { category ->
                        GuideCategoryRow(
                            category = category,
                            onClick = { onGuideClick(category.id) },
                        )
                    }
                }

                if (!uiState.isLoading &&
                    uiState.error == null &&
                    uiState.content.flows.isEmpty() &&
                    uiState.content.categories.isEmpty()
                ) {
                    item {
                        EmptyGuideState(
                            onRetry = onRetry,
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.62f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Surface(
                modifier = Modifier.size(104.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
                tonalElevation = 0.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.medkit),
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        tint = Color.Unspecified,
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "Guide",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Basic life support and emergency guidance",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GuideSectionHeader(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GuideFlowCard(
    flow: EmergencyGuideFlowSummary,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GuideThumb(
                imageUrl = null,
                icon = guideIcon(flow.kategori),
                accent = accent,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                AssistChip(
                    onClick = {},
                    label = { Text(flow.kategori.uppercase()) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.EmergencyShare,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                    },
                )
                Text(
                    text = flow.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                val description = flow.deskripsi.orEmpty()
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun GuideCategoryRow(
    category: GuideCategory,
    onClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GuideThumb(
                    imageUrl = category.imageUrl,
                    icon = guideIcon(category.iconName ?: category.id),
                    accent = MaterialTheme.colorScheme.secondary,
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = category.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${category.steps.size} steps",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(start = 106.dp, end = 20.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.56f),
            )
        }
    }
}

@Composable
private fun GuideThumb(
    imageUrl: String?,
    icon: ImageVector,
    accent: Color,
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(accent.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center,
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

@Composable
private fun GuideLoadingBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Fetching guide content",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun GuideErrorBanner(
    message: String,
    onRetry: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
            )
            IconButton(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}

@Composable
private fun EmptyGuideState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.MedicalServices,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(52.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Guide content is not available",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try again")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailScreen(
    categoryId: String,
    onBack: () -> Unit,
    viewModel: GuideViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val category = uiState.category(categoryId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.title ?: "Emergency Guide") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            uiState.isLoading && category == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            category == null -> {
                EmptyGuideState(
                    onRetry = viewModel::refresh,
                    modifier = Modifier
                        .padding(padding)
                        .padding(24.dp),
                )
            }
            else -> {
                GuideDetailContent(
                    category = category,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun GuideDetailContent(
    category: GuideCategory,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                if (!category.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = category.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = guideIcon(category.iconName ?: category.id),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(82.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = category.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        items(category.steps, key = { it.id }) { step ->
            GuideStepCard(step = step)
        }
    }
}

@Composable
private fun GuideStepCard(step: GuideStep) {
    ElevatedCard(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = step.number.toString(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
            }
            if (!step.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                AsyncImage(
                    model = step.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop,
                )
            }
            if (step.body.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = step.body,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun guideIcon(source: String): ImageVector {
    val key = source.lowercase()
    return when {
        key.contains("cpr") || key.contains("heart") || key.contains("napas") || key.contains("lungs") -> Icons.Default.Favorite
        key.contains("wind") || key.contains("tersedak") || key.contains("choking") -> Icons.Default.Air
        key.contains("fire") || key.contains("burn") || key.contains("flame") -> Icons.Default.LocalFireDepartment
        key.contains("luka") || key.contains("bandage") || key.contains("medical") -> Icons.Default.MedicalServices
        key.contains("sun") || key.contains("heat") -> Icons.Default.WbSunny
        key.contains("insect") || key.contains("bug") || key.contains("snake") -> Icons.Default.BugReport
        key.contains("water") || key.contains("drop") || key.contains("drowning") -> Icons.Default.WaterDrop
        key.contains("aed") || key.contains("shock") || key.contains("bolt") -> Icons.Default.ElectricBolt
        key.contains("phone") || key.contains("darurat") || key.contains("emergency") -> Icons.Default.Phone
        key.contains("shield") || key.contains("safe") -> Icons.Default.Shield
        key.contains("fall") || key.contains("fracture") -> Icons.Default.Accessibility
        else -> Icons.Default.EmergencyShare
    }
}

@Preview(showBackground = true)
@Composable
private fun GuideScreenPreview() {
    BaliTravelHealthTheme {
        GuideScreenContent(
            uiState = GuideUiState(),
        )
    }
}
