package dev.primodev.huddleup.feature.eventcreation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.primodev.huddleup.R

@Composable
fun EventCreationSavingDialog() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(shape = RoundedCornerShape(24.dp)) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator()

                Text(text = stringResource(R.string.event_creation_saving_dialog_text))
            }
        }
    }
}

@Composable
fun EventCreationSavingErrorDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.event_creation_saving_error_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.event_creation_saving_error_dialog_text))
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.event_creation_saving_error_dialog_confirm))
            }
        }
    )
}

@Composable
fun EventCreationUnsavedChangesDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.event_creation_unsaved_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.event_creation_unsaved_dialog_text))
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.event_creation_unsaved_dialog_dismiss))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text(text = stringResource(R.string.event_creation_unsaved_dialog_confirm))
            }
        }
    )
}