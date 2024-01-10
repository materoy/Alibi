package app.myzel394.alibi.ui.components.RecorderScreen.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.myzel394.alibi.R
import app.myzel394.alibi.ui.components.RecorderScreen.atoms.TorchStatus
import app.myzel394.alibi.ui.components.RecorderScreen.molecules.RecordingControl
import app.myzel394.alibi.ui.components.RecorderScreen.molecules.RecordingStatus
import app.myzel394.alibi.ui.models.VideoRecorderModel
import app.myzel394.alibi.ui.utils.CameraInfo
import app.myzel394.alibi.ui.utils.KeepScreenOn
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch

@Composable
fun VideoRecordingStatus(
    videoRecorder: VideoRecorderModel,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val availableCameras = CameraInfo.queryAvailableCameras(context)

    KeepScreenOn()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box {}

        Column(
            verticalArrangement = Arrangement
                .spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )

            if (videoRecorder.isStartingRecording) {
                Box(
                    modifier = Modifier
                        .width(128.dp)
                        .height(
                            with(LocalDensity.current) {
                                MaterialTheme.typography.labelMedium.fontSize.toDp()
                            }
                        )
                        .shimmer()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.shapes.small
                        )
                )
            } else {
                Text(
                    stringResource(
                        R.string.form_value_selected,
                        if (CameraInfo.checkHasNormalCameras(availableCameras)) {
                            videoRecorder.cameraID.let {
                                if (it == CameraInfo.Lens.BACK.androidValue)
                                    stringResource(R.string.ui_videoRecorder_action_start_settings_cameraLens_back_label)
                                else
                                    stringResource(R.string.ui_videoRecorder_action_start_settings_cameraLens_front_label)
                            }
                        } else {
                            stringResource(
                                R.string.ui_videoRecorder_action_start_settings_cameraLens_label,
                                videoRecorder.cameraID
                            )
                        }
                    ),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }

        if (videoRecorder.isStartingRecording) {
            Text(
                stringResource(R.string.ui_videoRecorder_info_starting),
                style = MaterialTheme.typography.labelMedium,
            )
        } else {
            RecordingStatus(
                recordingTime = videoRecorder.recordingTime,
                progress = videoRecorder.progress,
                recordingStart = videoRecorder.recordingStart,
                maxDuration = videoRecorder.settings!!.maxDuration,
            )
        }

        Column(
            verticalArrangement = Arrangement
                .spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            if (!videoRecorder.isStartingRecording) {
                val cameraControl = videoRecorder.recorderService!!.cameraControl!!
                if (cameraControl.hasTorchAvailable()) {
                    val isTorchEnabled = cameraControl.isTorchEnabled()

                    TorchStatus(
                        enabled = isTorchEnabled,
                        onChange = {
                            if (isTorchEnabled) {
                                cameraControl.disableTorch()
                            } else {
                                cameraControl.enableTorch()
                            }
                        },
                    )
                }
            }

            Divider()

            if (!videoRecorder.isStartingRecording) {
                RecordingControl(
                    // There may be some edge cases where the app may crash if the
                    // user stops or pauses the recording too soon, so we simply add a
                    // small delay to prevent that
                    initialDelay = 1000L,
                    isPaused = videoRecorder.isPaused,
                    recordingTime = videoRecorder.recordingTime,
                    onDelete = {
                        scope.launch {
                            runCatching {
                                videoRecorder.stopRecording(context)
                            }
                            runCatching {
                                videoRecorder.destroyService(context)
                            }
                            videoRecorder.batchesFolder!!.deleteRecordings()
                        }
                    },
                    onPauseResume = {
                        if (videoRecorder.isPaused) {
                            videoRecorder.resumeRecording()
                        } else {
                            videoRecorder.pauseRecording()
                        }
                    },
                    onSave = {
                        videoRecorder.onRecordingSave(false)
                    }
                )
            } else {
                Box {}
            }
        }
    }
}