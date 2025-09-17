package no.uio.ifi.in2000_gruppe3.ui.networkSnackbar

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NetworkSnackbar(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onNetworkStatusChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(true) }
    var showInitialCheck by remember { mutableStateOf(true) }

    fun checkAndUpdateConnection(): Boolean {
        val newStatus = checkInternetConnection(context)
        if (newStatus != isConnected || showInitialCheck) {
            isConnected = newStatus
            onNetworkStatusChange(newStatus)
            showInitialCheck = false
        }
        return newStatus
    }

    // Check at launch
    LaunchedEffect(Unit) {
        checkAndUpdateConnection()
    }

    // Periodic check every 5 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            checkAndUpdateConnection()
        }
    }

    LaunchedEffect(isConnected) {
        if (!isConnected) {
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Ingen internettforbindelse",
                    actionLabel = "Prøv igjen"
                )

                if (result == SnackbarResult.ActionPerformed) {
                    val currentStatus = checkAndUpdateConnection()
                    if (!currentStatus) {
                        snackbarHostState.showSnackbar(
                            message = "Fortsatt ingen internettforbindelse",
                            actionLabel = "Prøv igjen"
                        )
                    }
                }
            }
        }
    }
}

private fun checkInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}