package com.visitbali.balitravelhealth.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EmergencyShare
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.visitbali.balitravelhealth.data.dto.EmergencyGuideFlowSummary
import com.visitbali.balitravelhealth.data.dto.FlowChoice
import com.visitbali.balitravelhealth.data.dto.FlowNode
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.EmergencyGuideFlowViewModel
import com.visitbali.balitravelhealth.viewmodel.GuideFlowUiState

import androidx.compose.ui.res.stringResource
import com.visitbali.balitravelhealth.R

@Composable
fun EmergencyGuideFlowScreen(
    initialFlowId: Int? = null,
    onBack: () -> Unit = {},
    viewModel: EmergencyGuideFlowViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialFlowId) {
        if (initialFlowId != null && uiState.activeFlow?.id != initialFlowId) {
            viewModel.startFlow(initialFlowId)
        }
    }

    BackHandler {
        if (uiState.activeFlow != null) {
            if (!viewModel.navigateBack()) viewModel.closeFlow()
        } else {
            onBack()
        }
    }

    EmergencyGuideFlowContent(
        uiState = uiState,
        onBack = {
            if (uiState.activeFlow != null) {
                if (!viewModel.navigateBack()) viewModel.closeFlow()
            } else {
                onBack()
            }
        },
        onFlowClick = { viewModel.startFlow(it.id) },
        onNavigate = { viewModel.navigate(it) },
        onCloseFlow = { viewModel.closeFlow() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmergencyGuideFlowContent(
    uiState: GuideFlowUiState,
    onBack: () -> Unit,
    onFlowClick: (EmergencyGuideFlowSummary) -> Unit,
    onNavigate: (String) -> Unit,
    onCloseFlow: () -> Unit,
) {
    val inFlow = uiState.activeFlow != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            inFlow && uiState.currentNode != null -> uiState.activeFlow!!.title
                            else -> stringResource(R.string.emergency_flows_title)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        AnimatedContent(
            targetState = inFlow,
            transitionSpec = {
                if (targetState) {
                    (slideInVertically { it } + fadeIn()) togetherWith (slideOutVertically { -it } + fadeOut())
                } else {
                    (slideInVertically { -it } + fadeIn()) togetherWith (slideOutVertically { it } + fadeOut())
                }
            },
            label = "FlowContent",
        ) { showFlow ->
            if (showFlow) {
                when {
                    uiState.isLoadingFlow -> {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = Color(0xFFBD5454))
                        }
                    }
                    uiState.currentNode != null -> {
                        FlowNodePage(
                            node = uiState.currentNode,
                            historySize = uiState.nodeHistory.size,
                            onChoiceClick = { choice ->
                                if (choice.nextId != null) {
                                    onNavigate(choice.nextId)
                                }
                            },
                            onRestart = onCloseFlow,
                            modifier = Modifier.padding(padding),
                        )
                    }
                    else -> {}
                }
            } else {
                FlowListPage(
                    flows = uiState.flows,
                    isLoading = uiState.isLoadingList,
                    onFlowClick = onFlowClick,
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun FlowListPage(
    flows: List<EmergencyGuideFlowSummary>,
    isLoading: Boolean,
    onFlowClick: (EmergencyGuideFlowSummary) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFBD5454))
            }
        }
        flows.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.EmergencyShare,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.emergency_flows_no_guides), color = Color.Gray)
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Text(
                        text = stringResource(R.string.emergency_flows_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                items(flows) { flow ->
                    FlowSummaryCard(flow = flow, onClick = { onFlowClick(flow) })
                }
            }
        }
    }
}

@Composable
private fun FlowSummaryCard(
    flow: EmergencyGuideFlowSummary,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.EmergencyShare,
                contentDescription = null,
                tint = Color(0xFFBD5454),
                modifier = Modifier.size(40.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = flow.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
                val description = flow.deskripsi.orEmpty()
                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2,
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray,
            )
        }
    }
}

@Composable
private fun FlowNodePage(
    node: FlowNode,
    historySize: Int,
    onChoiceClick: (FlowChoice) -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isTerminal = node.choices.isEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Step indicator
        if (historySize > 0) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.emergency_flow_step_format, historySize + 1),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }
        }

        // Node title
        Text(
            text = node.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 34.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Instruction image (if any)
        if (!node.imageUrl.isNullOrBlank()) {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                AsyncImage(
                    model = node.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Instruction text
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Text(
                text = node.instruction,
                style = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(20.dp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (isTerminal) {
            // End of flow
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmergencyShare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.emergency_flow_completed),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBD5454)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text(stringResource(R.string.emergency_flow_btn_return), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        } else {
            // Choices header
            Text(
                text = stringResource(R.string.emergency_flow_choice_prompt),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )

            // Choices
            node.choices.forEach { choice ->
                val (choiceColor, choiceContentColor) = when (choice.variant) {
                    "yes" -> Color(0xFF2E7D32) to Color.White
                    "no" -> Color(0xFFC62828) to Color.White
                    else -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
                }

                Button(
                    onClick = { onChoiceClick(choice) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = choiceColor,
                        contentColor = choiceContentColor,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = choice.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmergencyGuideFlowScreenPreview() {
    BaliTravelHealthTheme {
        FlowListPage(
            flows = listOf(
                EmergencyGuideFlowSummary(1, "Basic Life Support", "BLS", "Step-by-step CPR guide", "", ""),
                EmergencyGuideFlowSummary(2, "Choking Emergency", "choking", "How to help a choking person", "", ""),
            ),
            isLoading = false,
            onFlowClick = {},
        )
    }
}
