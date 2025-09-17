package no.uio.ifi.in2000_gruppe3.ui.screens.profile.profileSelectScreen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.navigation.BottomBar
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSelectScreen(
    profileScreenViewModel: ProfileScreenViewModel,
    navController: NavHostController
) {
    val profileUIState by profileScreenViewModel.profileScreenUIState.collectAsState()
    var profile by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profiler",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_slogan),
                contentDescription = "App Logo",
                modifier = Modifier.fillMaxWidth()
                    .size(200.dp)
                    .padding(bottom = 16.dp)
            )

            Row (
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = profile,
                    onValueChange = { profile = it },
                    placeholder = { Text("Skriv inn ønsket brukernavn") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(30.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(30.dp))
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Enter) {
                                if (profile.isNotBlank()) {
                                    Log.d("UserScreen", "Adding profile $profile")
                                    profileScreenViewModel.addProfile(profile)
                                    profileScreenViewModel.selectProfile(profile)
                                    profile = ""

                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                }
                            }
                            false
                        },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (profile.isNotBlank()) {
                                Log.d("UserScreen", "Adding profile $profile")
                                profileScreenViewModel.addProfile(profile)
                                profileScreenViewModel.selectProfile(profile)
                                profile = ""

                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                )

                Spacer(modifier = Modifier.width(4.dp))

                Button(
                    modifier = Modifier.weight(0.4f),
                    onClick = {
                        if (profile.isNotBlank()) {
                            Log.d("UserScreen", "Adding profile $profile")
                            profileScreenViewModel.addProfile(profile)
                            profileScreenViewModel.selectProfile(profile)
                            profile = ""

                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary)
                ) {
                    Text(text = "Legg til")
                }
            }

            // List of profiles
            if (profileUIState.profiles.isEmpty()) {
                Text(
                    text = "Ingen brukere her gitt 🤔",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(profileUIState.profiles) { profile ->
                        ProfileCard(
                            profile = profile,
                            profileScreenViewModel = profileScreenViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
