package no.uio.ifi.in2000_gruppe3.ui.screens.chatbotScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.mapbox.geojson.Point
import dev.jeziellago.compose.markdowntext.MarkdownText
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.hikeCard.SmallHikeCard
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.screens.hikeCardScreen.HikeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.screens.homeScreen.HomeScreenViewModel
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@Composable
fun MessageBubble(
    chatbotMessage: ChatbotMessage,
    hikeScreenViewModel: HikeScreenViewModel,
    mapboxViewModel: MapboxViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavHostController
) {
    val alignment = if (chatbotMessage.isFromUser) Alignment.End else Alignment.Start

    val userGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )

    val botGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.secondaryContainer
        )
    )

    val contentColor = if (chatbotMessage.isFromUser) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    val bubbleShape = if (chatbotMessage.isFromUser) RoundedCornerShape(24.dp, 24.dp, 4.dp, 24.dp)
    else RoundedCornerShape(24.dp, 24.dp, 24.dp, 4.dp)

    val paddingEnd = if (chatbotMessage.isFromUser) 8.dp else 48.dp
    val paddingStart = if (chatbotMessage.isFromUser) 48.dp else 8.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingStart, end = paddingEnd, top = 4.dp, bottom = 4.dp),
        horizontalAlignment = alignment
    ) {
        // Avatar for bot messages
        if (!chatbotMessage.isFromUser) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = R.drawable.aanund,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LogoPrimary),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Ånund",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        Card(
            shape = bubbleShape,
            modifier = Modifier
                .shadow(
                    elevation = 2.dp,
                    shape = bubbleShape,
                    spotColor = Color.Black.copy(alpha = 0.1f)
                )
                .padding(top = if (!chatbotMessage.isFromUser) 4.dp else 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = if (chatbotMessage.isFromUser) userGradient else botGradient
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                MarkdownText(
                    markdown = chatbotMessage.content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = contentColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        // Message status indicator for user messages
        if (chatbotMessage.isFromUser) {
            Text(
                text = "Sent",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 2.dp, end = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        if (chatbotMessage.feature != null) {
            SmallHikeCard(
                mapboxViewModel = mapboxViewModel,
                feature = chatbotMessage.feature,
                onClick = {
                    homeScreenViewModel.fetchForecast(
                        Point.fromLngLat(
                        chatbotMessage.feature.geometry.coordinates[0][0],
                        chatbotMessage.feature.geometry.coordinates[0][1]
                    ))
                    hikeScreenViewModel.updateHike(chatbotMessage.feature)
                    navController.navigate(Screen.HikeScreen.route)
                }
            )
        }
    }
}
