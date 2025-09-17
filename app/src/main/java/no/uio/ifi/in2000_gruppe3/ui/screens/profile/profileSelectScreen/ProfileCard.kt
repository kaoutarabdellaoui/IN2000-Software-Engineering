package no.uio.ifi.in2000_gruppe3.ui.screens.profile.profileSelectScreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000_gruppe3.data.database.Profile
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@Composable
fun ProfileCard(
    profile: Profile,
    profileScreenViewModel: ProfileScreenViewModel,
    navController: NavController
) {
    val profileUIState by profileScreenViewModel.profileScreenUIState.collectAsState()
    var expandedProfileId by remember { mutableStateOf<String?>(null) }
    val isSelected = profileUIState.username == profile.username

    // Dont show the default user in the list
    if (profile.username == "defaultUser") return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = {
            expandedProfileId = if (expandedProfileId == profile.username) null else profile.username
            Log.d("UserScreen", "Clicked on profile: ${profile.username}")
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "User Icon",
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = profile.username,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )

                if (isSelected) {
                    Text(
                        text = "Valgt bruker"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expandedProfileId == profile.username,
            onDismissRequest = { expandedProfileId = null },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        Log.d("UserScreen", "Selected profile: ${profile.username}")
                        profileScreenViewModel.selectProfile(profile.username)
                        expandedProfileId = null
                        navController.navigate(Screen.Profile.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary)
                ) {
                    Text("Velg bruker")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        profileScreenViewModel.deleteProfile(profile.username)
                        expandedProfileId = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(text = "Slett profil")
                }
            }
        }
    }
}
