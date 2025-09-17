package no.uio.ifi.in2000_gruppe3.ui.mapSearchbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel

@Composable
fun SuggestionColumn(
    mapboxViewModel: MapboxViewModel
) {
    val mapboxUIState by mapboxViewModel.mapboxUIState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val hasSuggestions = mapboxUIState.searchQuery.isNotBlank() && mapboxUIState.searchResponse.isNotEmpty()

    if (hasSuggestions) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 10.dp),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(mapboxUIState.searchResponse) { suggestion ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            mapboxViewModel.getSelectedSearchResultPoint(
                                suggestion = suggestion
                            )
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(0.25f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val icon = getIconFromString(suggestion.makiIcon.toString(), context)
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        suggestion.distanceMeters?.let {
                            Text(
                                text = "%.2f km".format(it / 1000.0),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = suggestion.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        suggestion.formattedAddress?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
        }
    }
}

private fun getIconFromString(
    iconName: String,
    context: android.content.Context
): Int {
    var iconResourceId = context.resources.getIdentifier(
        iconName,
        "drawable",
        context.packageName
    )

    if (iconResourceId == 0) {
        iconResourceId = R.drawable.marker
    }

    return iconResourceId
}
