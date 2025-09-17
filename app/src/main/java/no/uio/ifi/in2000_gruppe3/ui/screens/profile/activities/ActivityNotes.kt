package no.uio.ifi.in2000_gruppe3.ui.screens.profile.activities

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000_gruppe3.data.hikeAPI.models.Feature
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@Composable
fun ActivityNotes(
    activityScreenViewModel: ActivityScreenViewModel,
    feature: Feature
) {
    val logScreenUIState by activityScreenViewModel.activityScreenUIState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var noteText by remember { mutableStateOf(logScreenUIState.hikeNotes[feature.properties.fid] ?: "") }
    var isExpanded by remember { mutableStateOf(false) }
    var showLimitMessage by remember { mutableStateOf(false) }

    val maxCharLimit = 500

    LaunchedEffect(feature.properties.fid, logScreenUIState.hikeNotes[feature.properties.fid]) {
        if (logScreenUIState.hikeNotes[feature.properties.fid] == null) {
            activityScreenViewModel.getNotesForHike(feature.properties.fid)
        } else {
            noteText = logScreenUIState.hikeNotes[feature.properties.fid] ?: ""
        }
    }
                                               
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, LogoPrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = LogoPrimary)
        ) {
            Text(
                text = if (noteText.isBlank()) "Legg til notater..." else "Endre notat",
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Open/Close notes",
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { newText ->
                    if(newText.length <= maxCharLimit) {
                        noteText = newText
                        showLimitMessage = false
                    } else {
                        showLimitMessage = true
                    }
                },
                placeholder = { Text("Hva syntes du om turen...?") },
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (noteText.isNotBlank()) {
                            activityScreenViewModel.addNotesToActivityLog(feature.properties.fid, noteText)
                            keyboardController?.hide()
                        }
                    }
                ),
                supportingText = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (showLimitMessage) {
                            Text(
                                text = "Maks grense symboler nådd!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Text(
                            "${noteText.length}/$maxCharLimit",
                            color = when {
                                noteText.length > maxCharLimit * 0.9 -> MaterialTheme.colorScheme.error
                                noteText.length > maxCharLimit * 0.8 -> Color(0xFFFFA000)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        isExpanded = false
                        keyboardController?.hide()
                    }
                ) {
                    Text(
                        text = "Avbryt",
                        color = LogoPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        activityScreenViewModel.addNotesToActivityLog(feature.properties.fid, noteText)
                        keyboardController?.hide()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary)
                ) {
                    Text("Lagre")
                }
            }
        }
    }
}
