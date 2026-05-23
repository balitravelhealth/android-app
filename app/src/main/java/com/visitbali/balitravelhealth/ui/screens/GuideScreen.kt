package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.visitbali.balitravelhealth.R
import com.visitbali.balitravelhealth.data.model.GuideItem
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.GuideViewModel
import kotlin.math.min

@Composable
fun GuideScreen(
    onNavigateToHome: () -> Unit = {},
    viewModel: GuideViewModel
) {
    val guides by viewModel.guides.collectAsState()
    GuideScreenContent(
        guides = guides,
        onNavigateToHome = onNavigateToHome
    )
}

@Composable
private fun GuideScreenContent(
    guides: List<GuideItem>,
    onNavigateToHome: () -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    val headerHeight = 320.dp
    
    // Calculate parallax progress based on scroll
    val scrollOffset by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                1000f // Past header
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = { 
            BaliNavigationBar(
                initialSelectedItem = 1,
                onHomeClick = onNavigateToHome,
                onGuideClick = {} 
            ) 
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            
            // Background Parallax Header
            GuideHeader(
                modifier = Modifier.graphicsLayer {
                    translationY = -scrollOffset * 0.5f // Parallax speed
                    alpha = min(1f, 1f - (scrollOffset / 600f)) // Fade out
                }
            )

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                // Invisible spacer item to allow header to show through
                item {
                    Spacer(modifier = Modifier.height(headerHeight))
                }
                
                items(guides) { guide ->
                    Surface(color = Color.White) {
                        Column {
                            GuideListItem(guide = guide)
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 120.dp, end = 24.dp),
                                color = Color.Black.copy(alpha = 0.1f),
                                thickness = 1.dp
                            )
                        }
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
            .height(320.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Yellow, Color.White),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Medkit Icon
            Icon(
                painter = painterResource(id = R.drawable.medkit),
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                tint = Color.Unspecified
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Emergency guide",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 24.dp).align(Alignment.Start)
            )
        }
    }
}

@Composable
private fun GuideListItem(guide: GuideItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gray placeholder for image
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.LightGray)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = guide.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = guide.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GuideScreenPreview() {
    BaliTravelHealthTheme {
        GuideScreenContent(
            guides = List(4) {
                GuideItem(
                    id = it.toString(),
                    title = "Emergency guide",
                    description = "What to do first in an urgent health situation.",
                    sortOrder = it
                )
            }
        )
    }
}
