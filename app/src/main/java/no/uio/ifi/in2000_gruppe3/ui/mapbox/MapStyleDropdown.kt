package no.uio.ifi.in2000_gruppe3.ui.mapbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapbox.maps.Style

@Composable
fun MapStyleDropdown(
    expanded: MutableState<Boolean>,
    mapboxViewModel: MapboxViewModel,
    modifier: Modifier
) {
    val mapboxUIState by mapboxViewModel.mapboxUIState.collectAsState()
    val mapStyle = mapboxUIState.mapStyle

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(12.dp))
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = "🌲 Natur",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                mapboxViewModel.updateMapStyle(Style.OUTDOORS)
                expanded.value = false
            },
            modifier = Modifier.background(
                if (mapStyle == Style.OUTDOORS) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
        )
        DropdownMenuItem(
            text = {
                Text(
                    text = "🛰️ Satellitt",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                mapboxViewModel.updateMapStyle(Style.STANDARD_SATELLITE)
                expanded.value = false
            },
            modifier = Modifier.background(
                if (mapStyle == Style.STANDARD_SATELLITE) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
        )
    }
}
