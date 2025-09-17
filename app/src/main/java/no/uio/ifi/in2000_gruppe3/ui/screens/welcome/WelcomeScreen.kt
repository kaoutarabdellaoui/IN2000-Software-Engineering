package no.uio.ifi.in2000_gruppe3.ui.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000_gruppe3.R
import no.uio.ifi.in2000_gruppe3.ui.navigation.Screen
import no.uio.ifi.in2000_gruppe3.ui.theme.LogoPrimary

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Icon(
                painter = painterResource(id = R.drawable.logo_slogan),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(375.dp),
                tint = Color.Unspecified
            )

            Text(
                text = "Planlegg perfekte turer i Oslo og Akershus \n med sanntidsvær og AI",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = LogoPrimary
            )

            Spacer(modifier = Modifier.height(220.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                onClick = { navController.navigate(Screen.ProfileSelect.route) },
                colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary)
            ) {
                Text(
                    text = "Logg inn",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                onClick = { navController.navigate(Screen.Home.route) },
                colors = ButtonDefaults.buttonColors(containerColor = LogoPrimary)
            ) {
                Text(
                    text = "Fortsett som standard bruker",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
