package no.uio.ifi.in2000_gruppe3.ui.hikeCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.jeziellago.compose.markdowntext.MarkdownText
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.data.date.calculateDaysAhead
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDay
import no.uio.ifi.in2000_gruppe3.ui.loaders.Loader
import no.uio.ifi.in2000_gruppe3.ui.locationForecast.LocationForecastSmallCard
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen.OpenAIViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.favoriteScreen.FavoritesScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities.ActivityScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoSecondary
import java.time.LocalDate

@Composable
fun HikeCard(
    homeScreenViewModel: HomeScreenViewModel,
    hikeScreenViewModel: HikeScreenViewModel,
    favoritesViewModel: FavoritesScreenViewModel,
    mapboxViewModel: MapboxViewModel,
    openAIViewModel: OpenAIViewModel,
    activityScreenViewModel: ActivityScreenViewModel,
    navController: NavHostController,
    checkedState: MutableState<Boolean>
) {
    val hikeUIState by hikeScreenViewModel.hikeScreenUIState.collectAsState()
    val openAIUIState by openAIViewModel.openAIUIState.collectAsState()
    val logUIState by activityScreenViewModel.activityScreenUIState.collectAsState()
    val isInLog = logUIState.hikeLog.contains(hikeUIState.feature.properties.fid)
    var averageWindSpeed by remember {
        mutableDoubleStateOf(
            homeScreenViewModel.daysAverageWindSpeed(
                hikeUIState.selectedDate
            )
        )
    }

    LaunchedEffect(hikeUIState.selectedDay) {
        val daysAhead = calculateDaysAhead(getTodaysDay(), hikeUIState.selectedDay)
        hikeScreenViewModel.updateSelectedDate(
            LocalDate.now().plusDays(daysAhead.toLong()).toString()
        )

        averageWindSpeed = homeScreenViewModel.daysAverageWindSpeed(hikeUIState.selectedDate)

        if (!hikeUIState.descriptionAlreadyLoaded) {
            hikeScreenViewModel.getHikeDescription(
                homeScreenViewModel = homeScreenViewModel,
                openAIViewModel = openAIViewModel
            )
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            item {
                HikeCardMapPreview(
                    mapboxViewModel,
                    hikeUIState.feature,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Informasjon om turen",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Ellipsis
                    )

                    WeekdaySelector(
                        hikeScreenViewModel = hikeScreenViewModel
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoItem(
                        icon = ImageVector.vectorResource(R.drawable.mountain),
                        label = "Type",
                        value = "Gåtur",
                        iconTint = MaterialTheme.colorScheme.primary
                    )
                    InfoItem(
                        icon = ImageVector.vectorResource(R.drawable.terrain_icon),
                        label = "Vanskelighet",
                        value = hikeUIState.feature.difficultyInfo.label,
                        iconTint = hikeUIState.feature.difficultyInfo.color
                    )
                    InfoItem(
                        icon = ImageVector.vectorResource(id = R.drawable.distance_icon),
                        label = "Lengde",
                        value = (hikeUIState.feature.properties.distance_meters.toFloat() / 1000.0).let { "%.2f km".format(it) },
                        iconTint = Color(0xFF4CAF50)
                    )
                    InfoItem(
                        icon = ImageVector.vectorResource(id = R.drawable.wind),
                        label = "Vindhastighet",
                        value = averageWindSpeed.let { "%.1f m/s".format(it) },
                        iconTint = LogoSecondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                MarkdownText(
                    markdown = openAIUIState.response,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                if (openAIUIState.isLoading || openAIUIState.isStreaming) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Loader()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LocationForecastSmallCard(
                    day = hikeUIState.selectedDay,
                    date = hikeUIState.selectedDate,
                    homeScreenViewModel = homeScreenViewModel,
                    hikeScreenViewModel = hikeScreenViewModel,
                    navController = navController
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary),
                    onClick = { navController.navigate(Screen.LocationForecast.route) }
                ) {
                    Text(text = "Se været andre dager")
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!isInLog) {
                    Button(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary),
                        onClick = { activityScreenViewModel.addToActivityLog(hikeUIState.feature.properties.fid) }
                    ) {
                        Text(text = "Legg til i loggen")
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ganger gått",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            LaunchedEffect(hikeUIState.feature.properties.fid) {
                                activityScreenViewModel.getTimesWalkedForHike(hikeUIState.feature.properties.fid)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        activityScreenViewModel.adjustTimesWalked(
                                            hikeUIState.feature.properties.fid,
                                            -1
                                        )
                                    },
                                    enabled = (logUIState.hikeTimesWalked[hikeUIState.feature.properties.fid] ?: 0) > 1,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LogoPrimary,
                                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f)

                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "-",
                                        color = if ((logUIState.hikeTimesWalked[hikeUIState.feature.properties.fid] ?: 0) > 1) {
                                            Color.White
                                        } else {
                                            Color.White.copy(alpha = 0.5f)
                                        }

                                    )
                                }

                                Text(
                                    text = "${logUIState.hikeTimesWalked[hikeUIState.feature.properties.fid] ?: 0}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .weight(1f),
                                    textAlign = TextAlign.Center
                                )

                                Button(
                                    onClick = {
                                        activityScreenViewModel.adjustTimesWalked(
                                            hikeUIState.feature.properties.fid,
                                            1
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(
                                            0xFF061C40
                                        )
                                    ),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "+")
                                }
                            }
                        }
                    }

                    Button(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1f)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary),
                        onClick = {
                            activityScreenViewModel.removeFromActivityLog(
                                hikeUIState.feature.properties.fid
                            )
                        }
                    ) {
                        Text(text = "Fjern fra logg")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            favoritesViewModel.setUser()
                            checkedState.value = !checkedState.value
                            if (checkedState.value) {
                                favoritesViewModel.addFavorite(hikeUIState.feature.properties.fid)
                            } else {
                                favoritesViewModel.deleteFavorite(hikeUIState.feature.properties.fid)
                            }
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (checkedState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (checkedState.value) Color.Red else Color.Gray,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = if (checkedState.value) "Fjern fra favoritter" else "Legg til i favoritter")
                }
            }
        }
    }
}
