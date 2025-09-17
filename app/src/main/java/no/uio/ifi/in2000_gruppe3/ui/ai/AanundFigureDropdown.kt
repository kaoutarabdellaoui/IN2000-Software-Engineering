package no.uio.ifi.in2000_gruppe3.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.ui.bottomSheetDrawer.SheetDrawerDetent
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel

@Composable
fun AanundFigureDropdown(
    expanded: MutableState<Boolean>,
    homeScreenViewModel: HomeScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    mapBoxViewModel: MapboxViewModel,
    navController: NavHostController
) {
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "✨Mine anbefalinger✨",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                homeScreenViewModel.setSheetState(SheetDrawerDetent.SEMIPEEK)
                homeScreenViewModel.clearHikes()
                mapBoxViewModel.clearPolylineAnnotations()
                hikeScreenViewModel.updateRecommendedHikesLoaded(false)
                expanded.value = false
            }
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = "🤖 Chat med meg",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                expanded.value = false
                navController.navigate(Screen.Chatbot.route)
            }
        )
    }
}
