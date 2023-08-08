package app.myzel394.alibi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.myzel394.alibi.R
import app.myzel394.alibi.dataStore
import app.myzel394.alibi.db.AppSettings
import app.myzel394.alibi.services.bindToRecorderService
import app.myzel394.alibi.ui.components.SettingsScreen.atoms.BitrateTile
import app.myzel394.alibi.ui.components.SettingsScreen.atoms.EncoderTile
import app.myzel394.alibi.ui.components.SettingsScreen.atoms.ForceExactMaxDurationTile
import app.myzel394.alibi.ui.components.SettingsScreen.atoms.IntervalDurationTile
import app.myzel394.alibi.ui.components.SettingsScreen.atoms.MaxDurationTile
import app.myzel394.alibi.ui.components.SettingsScreen.atoms.OutputFormatTile
import app.myzel394.alibi.ui.components.SettingsScreen.atoms.SamplingRateTile
import app.myzel394.alibi.ui.components.atoms.GlobalSwitch
import app.myzel394.alibi.ui.components.atoms.MessageBox
import app.myzel394.alibi.ui.components.atoms.MessageType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    val (_, service) = bindToRecorderService()
    val isRecording = service?.isRecording ?: false
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(stringResource(R.string.ui_settings_title))
                },
                navigationIcon = {
                    IconButton(onClick = navController::popBackStack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val scope = rememberCoroutineScope()
            val dataStore = LocalContext.current.dataStore
            val settings = dataStore
                .data
                .collectAsState(initial = AppSettings.getDefaultInstance())
                .value

            // Show alert
            if (isRecording)
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    MessageBox(
                        type = MessageType.WARNING,
                        title = stringResource(R.string.ui_settings_hint_recordingActive_title),
                        message = stringResource(R.string.ui_settings_hint_recordingActive_message),
                    )
                }
            GlobalSwitch(
                label = stringResource(R.string.ui_settings_advancedSettings_label),
                checked = settings.showAdvancedSettings,
                onCheckedChange = {
                    scope.launch {
                        dataStore.updateData {
                            it.setShowAdvancedSettings(it.showAdvancedSettings.not())
                        }
                    }
                }
            )
            MaxDurationTile()
            IntervalDurationTile()
            ForceExactMaxDurationTile()
            AnimatedVisibility(visible = settings.showAdvancedSettings) {
                Column {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                    )
                    BitrateTile()
                    SamplingRateTile()
                    OutputFormatTile()
                    EncoderTile()
                }
            }
        }
    }
}
