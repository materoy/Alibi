package app.myzel394.alibi.ui.components.RecorderScreen.atoms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import app.myzel394.alibi.R

@Composable
fun RecorderErrorDialog(
    onClose: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClose,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
            )
        },
        title = {
            Text(stringResource(R.string.ui_recorder_error_recording_title))
        },
        text = {
            Text(stringResource(R.string.ui_recorder_error_recording_description))
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text(stringResource(R.string.dialog_close_neutral_label))
            }
        }
    )
}