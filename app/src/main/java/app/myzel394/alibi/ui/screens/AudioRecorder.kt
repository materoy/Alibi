package app.myzel394.alibi.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import app.myzel394.alibi.services.bindToRecorderService
import app.myzel394.alibi.ui.components.AudioRecorder.molecules.RecordingStatus
import app.myzel394.alibi.ui.components.AudioRecorder.molecules.StartRecording
import app.myzel394.alibi.ui.enums.Screen
import app.myzel394.alibi.ui.utils.rememberFileSaverDialog
import app.myzel394.alibi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecorder(
    navController: NavController,
) {
    val saveFile = rememberFileSaverDialog("audio/aac")
    val (connection, service) = bindToRecorderService()

    var showRecorderStatus by remember {
        mutableStateOf(service?.isRecording ?: false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Settings.route)
                        },
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) {padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (showRecorderStatus && service?.recordingTime?.value != null)
                RecordingStatus(
                    service = service,
                    onSaveFile = {
                        saveFile(it)
                        showRecorderStatus = false
                    }
                )
            else
                StartRecording(
                    connection = connection,
                    service = service,
                    onStart = {
                        showRecorderStatus = true
                    }
                )
        }
    }
}