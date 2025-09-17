package no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.BottomBar
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotScreen(
    homeScreenViewModel: HomeScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    mapboxViewModel: MapboxViewModel,
    navController: NavHostController
) {
    val openAIViewModel: OpenAIViewModel = viewModel()
    val openAIUIState by openAIViewModel.openAIUIState.collectAsState()
    val homeScreenUIState by homeScreenViewModel.homeScreenUIState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val conversationHistory = openAIViewModel.conversationHistory
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(conversationHistory.size) {
        if (conversationHistory.isNotEmpty()) {
            listState.animateScrollToItem(conversationHistory.size - 1)
        }
        input = ""  // Clear input after sending
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row {
                            AsyncImage(
                                model = R.drawable.aanund,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onPrimary),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                "Turbotten Ånund",
                                modifier = Modifier.padding(start = 12.dp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = LogoPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )

                // Status bar showing bot is online
                ChatbotConnectionStatus(homeScreenUIState)
            }
        },
        bottomBar = {
            Column {
                ChatInputField(
                    openAIUIState = openAIUIState,
                    value = input,
                    onValueChange = { input = it },
                    onSend = {
                        keyboardController?.hide()
                        openAIViewModel.addUserMessage(input)
                        coroutineScope.launch {
                            openAIViewModel.getChatbotResponse(
                                input = input,
                                homeScreenViewModel = homeScreenViewModel
                            )
                        }
                    }
                )
                BottomBar(navController = navController)
            }
        }
    ) { contentPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(contentPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(conversationHistory) { message ->
                MessageBubble(
                    chatbotMessage = message,
                    hikeScreenViewModel = hikeScreenViewModel,
                    mapboxViewModel = mapboxViewModel,
                    navController = navController,
                    homeScreenViewModel = homeScreenViewModel
                )
            }
            
            // Extra space at bottom for better scrolling
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
