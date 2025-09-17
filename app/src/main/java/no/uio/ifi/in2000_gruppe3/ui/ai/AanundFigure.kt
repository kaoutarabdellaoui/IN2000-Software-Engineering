package no.uio.ifi.in2000_gruppe3.ui.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.bottomSheetDrawer.SheetDrawerDetent
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@Composable
fun AanundFigure(
    homeScreenViewModel: HomeScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    mapboxViewModel: MapboxViewModel,
    navController: NavHostController
) {
    val homeScreenUiState by homeScreenViewModel.homeScreenUIState.collectAsState()
    val mapboxUiState by mapboxViewModel.mapboxUIState.collectAsState()
    val aanundMenuExpanded = remember { mutableStateOf(false) }
    var isDialogVisible by remember { mutableStateOf(false) }

    // Show dialog if it has not been shown before
    LaunchedEffect(mapboxUiState.isLoading) {
        if (!homeScreenUiState.hasShownAanundDialog) {
            if (!mapboxUiState.isLoading) {
                homeScreenViewModel.markAanundDialogShown()
                isDialogVisible = true
            }
        }
    }

    if (isDialogVisible) {
        homeScreenViewModel.setSheetState(SheetDrawerDetent.SEMIPEEK)

        // Clear hikes from map to get AI recommendations
        homeScreenViewModel.clearHikes()
        mapboxViewModel.clearPolylineAnnotations()

        Dialog(
            onDismissRequest = { isDialogVisible = false },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isDialogVisible = false
                        navController.navigate(Screen.Chatbot.route)
                    }
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hei, mitt navn er Ånund!\n" +
                                "Jeg er her for å hjelpe deg med å planlegge turer i Oslo/Akershus.\n\n" +
                                "Bruk søkefeltet for å finne turer i et bestemt område, eller trykk på kartet for å oppdage nye turmuligheter.\n\n" +
                                "Hvis du trenger inspirasjon, kan du trykke på meg! Jeg vil gi deg mine beste anbefalinger for de fineste turene å gå akkurat i dag.\n\n" +
                                "Du kan også trykke på meg for å chatte og få personlig hjelp med å planlegge turen din!",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                    )

                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp),
                        onClick = { navController.navigate(Screen.Chatbot.route) },
                        colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary)
                    ) {
                        Text(
                            text = "🤖 Chat med meg"
                        )
                    }
                }

                Icon(
                    painter = painterResource(id = R.drawable.aanund),
                    contentDescription = "AI icon",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-60).dp, y = (-60).dp)
                )

                IconButton(
                    onClick = { isDialogVisible = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.size(120.dp)
        ) {
            IconButton(
                onClick = { isDialogVisible = true },
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.aanund_white),
                    contentDescription = "AI icon white",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(90.dp) // must be 30dp smaller than the surface
                )

                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info icon",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { aanundMenuExpanded.value = true }
                        .offset(x = 30.dp, y = (-30).dp)
                )
            }

            AanundFigureDropdown(
                expanded = aanundMenuExpanded,
                homeScreenViewModel = homeScreenViewModel,
                hikeScreenViewModel = hikeScreenViewModel,
                mapBoxViewModel = mapboxViewModel,
                navController = navController
            )
        }
    }
}
