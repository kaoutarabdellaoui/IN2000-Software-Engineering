package no.uio.ifi.in2000_gruppe3.ui.mapSearchbar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.mapbox.MapboxViewModel

@Composable
fun SearchBarForMap(
    mapboxViewModel: MapboxViewModel
) {
    val mapboxUIState by mapboxViewModel.mapboxUIState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val hasSuggestions = mapboxUIState.searchQuery.isNotBlank() && mapboxUIState.searchResponse.isNotEmpty()

    TextField(
        value = mapboxUIState.searchQuery,
        onValueChange = { newQuery ->
            mapboxViewModel.updateSearchQuery(newQuery)
        },
        singleLine = true,
        placeholder = { Text("Hvor vil du gå tur?") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp)
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(30.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(30.dp))
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Enter && hasSuggestions) {
                    mapboxViewModel.getSelectedSearchResultPoint(
                        suggestion = mapboxUIState.searchResponse.first()
                    )
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
                true
            },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (hasSuggestions) {
                    mapboxViewModel.getSelectedSearchResultPoint(
                        suggestion = mapboxUIState.searchResponse.first()
                    )
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.logo_without),
                contentDescription = "Logo",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 10.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}
