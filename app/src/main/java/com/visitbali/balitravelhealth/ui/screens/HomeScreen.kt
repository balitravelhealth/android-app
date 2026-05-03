package com.visitbali.balitravelhealth.ui.screens

import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.*
import com.visitbali.balitravelhealth.R
import androidx.compose.animation.core.tween
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.visitbali.balitravelhealth.ui.theme.BaliTravelHealthTheme
import com.visitbali.balitravelhealth.viewmodel.HomeUiState
import com.visitbali.balitravelhealth.viewmodel.HomeViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToPreTravel: () -> Unit = {},
    onNavigateToDuringTravel: () -> Unit = {},
    onNavigateToPostTravel: () -> Unit = {},
    onNavigateToNursingCare: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    HomeScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.refreshData() },
        onNavigateToPreTravel = onNavigateToPreTravel,
        onNavigateToDuringTravel = onNavigateToDuringTravel,
        onNavigateToPostTravel = onNavigateToPostTravel,
        onNavigateToNursingCare = onNavigateToNursingCare,
        onNavigateToGuide = onNavigateToGuide,
        onNavigateToProfile = onNavigateToProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onNavigateToPreTravel: () -> Unit = {},
    onNavigateToDuringTravel: () -> Unit = {},
    onNavigateToPostTravel: () -> Unit = {},
    onNavigateToNursingCare: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // Permission launcher
    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            onRefresh()
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Scaffold(
        bottomBar = { 
            BaliNavigationBar(
                initialSelectedItem = 0,
                onHomeClick = { /* Already here */ },
                onGuideClick = onNavigateToGuide,
                onProfileClick = onNavigateToProfile
            ) 
        },
    ) { paddingValues ->
        val blurAlphaProgress by remember {
            derivedStateOf {
                min(1f, scrollState.value.toFloat() / 600f)
            }
        }
        val blurRadius = (blurAlphaProgress * 15).dp
        val headerHeight = 351.dp
        val imgAvatarPlaceholder = "https://www.figma.com/api/mcp/asset/f0d043ca-a4ad-43f1-86a0-a4a306a0a9d1"

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(paddingValues)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                
                // Background Header with Parallax & Blur
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(headerHeight)
                        .graphicsLayer {
                            translationY = -scrollState.value * 0.5f // Parallax effect
                            alpha = 1f - blurAlphaProgress * 0.3f // Fade out slightly
                        }
                        .blur(blurRadius)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bali_default),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Header Overlay Text
                    HeaderContent(
                        uiState = uiState,
                        avatarUrl = imgAvatarPlaceholder,
                        modifier = Modifier.graphicsLayer {
                            alpha = 1f - blurAlphaProgress * 1.5f // Fade out faster
                        }
                    )
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Spacer to push content down
                    Spacer(modifier = Modifier.height(headerHeight - 40.dp)) // Overlap slightly
                    
                    // Main Rectangle Content
                    Surface(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Indicator for scrolling
                            Box(
                                modifier = Modifier
                                    .size(width = 40.dp, height = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray)
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            CardsGridContent(
                                onPreTravelClick = onNavigateToPreTravel,
                                onDuringTravelClick = onNavigateToDuringTravel,
                                onPostTravelClick = onNavigateToPostTravel,
                                onNursingCareClick = onNavigateToNursingCare
                            )
                            
                            // Extra space to ensure scrolling
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderContent(
    uiState: HomeUiState,
    avatarUrl: String,
    modifier: Modifier = Modifier
) {
    val userName = uiState.userName
    val arrivalDate = uiState.arrivalDate
    val isInBali = uiState.isInBali

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
    ) {
        val (avatar, welcome, headline, alarmIcon, subText) = createRefs()

        GenericAvatar(
            imageUrl = avatarUrl,
            modifier = Modifier.constrainAs(avatar) {
                top.linkTo(parent.top, margin = 64.dp)
                start.linkTo(parent.start)
            }
        )

        Column(
            modifier = Modifier.constrainAs(welcome) {
                top.linkTo(avatar.top)
                bottom.linkTo(avatar.bottom)
                start.linkTo(avatar.end, margin = 9.dp)
            }
        ) {
            Text(text = "Welcome back,", style = MaterialTheme.typography.labelMedium, color = Color.White)
            Text(text = userName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        }

        val headlineText = if (isInBali) "Welcome to Bali!\nStay Healthy" else "Keep Healthy\nWhile in Bali"

        Text(
            text = headlineText,
            style = MaterialTheme.typography.headlineLarge.copy(lineHeight = 34.sp, letterSpacing = 0.64.sp),
            color = Color.White,
            modifier = Modifier.constrainAs(headline) {
                top.linkTo(avatar.bottom, margin = 52.dp)
                start.linkTo(parent.start)
            }
        )

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp).constrainAs(alarmIcon) {
                top.linkTo(headline.bottom, margin = 20.dp)
                start.linkTo(parent.start)
            }
        )

        val subTextContent = when {
            isInBali && (uiState.daysUntilDeparture ?: Long.MAX_VALUE) <= 2 -> 
                "You'll be leaving Bali \nin ${uiState.departureDate}"
            isInBali -> "Enjoy your stay in Bali!"
            else -> "You’re going to Bali \nin $arrivalDate"
        }

        Text(
            text = subTextContent,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.constrainAs(subText) {
                top.linkTo(alarmIcon.top)
                bottom.linkTo(alarmIcon.bottom)
                start.linkTo(alarmIcon.end, margin = 10.dp)
            }
        )
    }
}

@Composable
fun CardsGridContent(
    onPreTravelClick: () -> Unit = {},
    onDuringTravelClick: () -> Unit = {},
    onPostTravelClick: () -> Unit = {},
    onNursingCareClick: () -> Unit = {}
) {
    val cards = listOf(
        HomeCardData(
            "Pre Travel",
            "Prepare your health\nbefore traveling",
            Color(0xFF90D1C6),
            R.drawable.ic_pre_light,
            R.drawable.ic_pre_background
        ),
        HomeCardData(
            "During Travel",
            "Track your health\nwhile traveling",
            Color(0xFFEDE075),
            R.drawable.ic_during_light,
            R.drawable.ic_during_background
        ),
        HomeCardData(
            "Post Travel",
            "Health check-up\nafter traveling",
            Color(0xFF8CC478),
            R.drawable.ic_post_light,
            R.drawable.ic_post_background
        ),
        HomeCardData(
            "Nursing Care",
            "Get Nursing Care Service\nwhile traveling",
            Color(0xFFBD5454),
            R.drawable.ic_nurse_light,
            R.drawable.ic_nurse_background
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            HomeCard(
                cards[0], 
                modifier = Modifier.weight(1f).clickable { onPreTravelClick() }
            )
            HomeCard(
                cards[1], 
                modifier = Modifier.weight(1f).clickable { onDuringTravelClick() }
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            HomeCard(
                cards[2], 
                modifier = Modifier.weight(1f).clickable { onPostTravelClick() }
            )
            HomeCard(
                cards[3], 
                modifier = Modifier.weight(1f).clickable { onNursingCareClick() }
            )
        }
    }
}

@Composable
fun HomeCard(data: HomeCardData, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = data.color),
        modifier = modifier.aspectRatio(1f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Icon with 20% opacity - Positioned bottom-right as per image
            Image(
                painter = painterResource(id = data.bgIconRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = 50.dp, y = 50.dp) // Slight offset to match design
                    .align(Alignment.BottomEnd)
                    .graphicsLayer { alpha = 0.2f },
                contentScale = ContentScale.Fit
            )

            ConstraintLayout(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val (icon, title, desc) = createRefs()

                Image(
                    painter = painterResource(id = data.iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .constrainAs(icon) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                )

                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.constrainAs(title) {
                        bottom.linkTo(desc.top, margin = 4.dp)
                        start.linkTo(parent.start)
                    }
                )

                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 16.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.constrainAs(desc) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                )
            }
        }
    }
}

@Composable
fun GenericAvatar(imageUrl: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(model = imageUrl, contentDescription = "Avatar", modifier = Modifier.size(24.dp))
    }
}

@Composable
fun BaliNavigationBar(
    initialSelectedItem: Int = 0,
    onHomeClick: () -> Unit = {},
    onGuideClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val imgAvatarPlaceholder = "https://www.figma.com/api/mcp/asset/f0d043ca-a4ad-43f1-86a0-a4a306a0a9d1"
    var selectedItem by remember { mutableIntStateOf(initialSelectedItem) }
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = { 
                selectedItem = 0
                onHomeClick()
            },
            icon = { 
                AnimatedNavigationIcon(
                    selected = selectedItem == 0,
                    lottieRes = R.raw.home_anim,
                    filledRes = R.drawable.ic_home_filled,
                    outlineRes = R.drawable.ic_home_outline
                )
            },
            label = { Text("Home", style = MaterialTheme.typography.labelMedium) }
        )
        NavigationBarItem(
            selected = selectedItem == 1, 
            onClick = { 
                selectedItem = 1
                onGuideClick()
            },
            icon = { 
                AnimatedNavigationIcon(
                    selected = selectedItem == 1,
                    lottieRes = R.raw.guide_anim,
                    filledRes = R.drawable.ic_guide_filled,
                    outlineRes = R.drawable.ic_guide_outline
                )
            }, 
            label = { Text("Guide", style = MaterialTheme.typography.labelMedium) }
        )
        NavigationBarItem(
            selected = selectedItem == 2, 
            onClick = { 
                selectedItem = 2
                onProfileClick()
            }, 
            icon = { 
                AsyncImage(
                    model = imgAvatarPlaceholder, // Logic to be replaced with actual user profile picture
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }, 
            label = { Text("Profile", style = MaterialTheme.typography.labelMedium) }
        )
    }
}

@Composable
fun AnimatedNavigationIcon(
    selected: Boolean,
    lottieRes: Int,
    filledRes: Int,
    outlineRes: Int
) {
    val isPreview = LocalInspectionMode.current
    
    if (isPreview) {
        Icon(
            painter = painterResource(id = if (selected) filledRes else outlineRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    } else {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            isPlaying = selected,
            iterations = 1,
            speed = 1.5f
        )

        Box(contentAlignment = Alignment.Center) {
            if (selected) {
                if (progress < 1f) {
                    // Play animation when selected
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(26.dp)
                    )
                } else {
                    // Show static filled icon when animation ends
                    Icon(
                        painter = painterResource(id = filledRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                // Show static outline icon when not selected
                Icon(
                    painter = painterResource(id = outlineRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

data class HomeCardData(
    val title: String,
    val description: String,
    val color: Color,
    val iconRes: Int,
    val bgIconRes: Int
)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BaliTravelHealthTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                userName = "John Doe",
                arrivalDate = "Oct 25, 2023",
                isInBali = false
            ),
            onRefresh = {}
        )
    }
}
