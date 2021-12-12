package com.github.amrmsaraya.clock.presentation.timer.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.github.amrmsaraya.clock.R
import com.github.amrmsaraya.clock.domain.entity.Timer
import com.github.amrmsaraya.timer.toTime

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NewTimerDialog(
    timer: Timer,
    editMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (Timer) -> Unit,
    onDelete: (Timer) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var title by remember { mutableStateOf(timer.title) }
    var timerMillis by remember { mutableStateOf(timer.timeMillis) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        DialogHeader(
            title = if (editMode) stringResource(R.string.edit_timer) else stringResource(R.string.new_timer),
            onDismiss = {
                keyboard?.hide()
                onDismiss()
            },
            onSave = {
                onSave(timer.copy(title = title, timeMillis = timerMillis))
                keyboard?.hide()
                onDismiss()
            }
        )

        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            onValueChange = { title = it },
            label = { Text(text = stringResource(R.string.title)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                leadingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() })
        )

        Spacer(modifier = Modifier.size(32.dp))

        SetupTimer(
            modifier = Modifier.height(80.dp),
            timer = timerMillis.toTime(),
            onTimeChange = { timerMillis = it }
        )


        if (editMode) {
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                onClick = {
                    onDelete(timer)
                    keyboard?.hide()
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.delete).uppercase())
            }
        }
    }
}

@Composable
private fun DialogHeader(
    title: String,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onDismiss() }
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(onClick = { onSave() }) {
            Icon(imageVector = Icons.Default.Done, contentDescription = null)
        }
    }
}