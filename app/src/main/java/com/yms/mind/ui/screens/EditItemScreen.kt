package com.yms.mind.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yms.mind.R
import com.yms.mind.data.model.Priority
import com.yms.mind.data.model.TodoItem
import com.yms.mind.viewmodels.EditViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun getLocalDateTimeAsString(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime.format(formatter)
}

@Composable
fun EditItemScreen(
    viewModel: EditViewModel,
    item: TodoItem?,
    onNavigationClick: () -> Unit
) {
    val todoTextState = remember { mutableStateOf(item?.text ?: "") }
    val todoDeadlineState = remember { mutableStateOf(item?.deadline) }
    val todoPriorityState = remember { mutableStateOf(item?.priority ?: Priority.BASIC) }

    val onSaveClick: () -> Unit = {
        viewModel.saveTask(
            idArg = item?.id,
            text = todoTextState.value,
            priority = todoPriorityState.value,
            deadline = todoDeadlineState.value
        )
        onNavigationClick()
    }

    val onDeleteClick: () -> Unit = {
        item?.id?.let { viewModel.deleteTask(it) }
        onNavigationClick()
    }

    Screen(
        text = todoTextState.value,
        priority = todoPriorityState.value,
        deadline = todoDeadlineState.value,
        deleteEnabled = { item != null },
        onNavigateBackClick = onNavigationClick,
        onSaveClick = onSaveClick,
        onDeleteClick = onDeleteClick,
        onTextChange = { text -> todoTextState.value = text },
        onImportanceChange = { importance -> todoPriorityState.value = importance },
        onDeadlineChange = { deadline -> todoDeadlineState.value = deadline ?: todoDeadlineState.value }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    text: String = "",
    priority: Priority = Priority.IMPORTANT,
    deadline: LocalDateTime? = null,
    deleteEnabled: () -> Boolean = { true },

    onNavigateBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onTextChange: (text: String) -> Unit = {},
    onImportanceChange: (importance: Priority) -> Unit = {},
    onDeadlineChange: (deadline: LocalDateTime?) -> Unit = {},
) {
    var newText by remember { mutableStateOf(text) }
    var selectedDate by remember { mutableStateOf(deadline) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(id = R.string.back_button)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text(
                            text = stringResource(id = R.string.save).uppercase(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            var dropDownMenuVisible by remember { mutableStateOf(false) }
            var datePickerVisible by remember { mutableStateOf(false) }
            var selectedPriority by remember { mutableStateOf(priority) }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                OutlinedTextField(
                    value = newText,
                    onValueChange = {
                        newText = it
                        onTextChange(newText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    placeholder = { Text(stringResource(id = R.string.edit_todo_hint)) },
                )
            }
            Spacer(Modifier.height(16.dp))

            Box {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable { dropDownMenuVisible = true }
                ) {
                    Text(
                        text = stringResource(id = R.string.importance),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = when (selectedPriority) {
                            Priority.LOW -> stringResource(id = R.string.importance_low)
                            Priority.BASIC -> stringResource(id = R.string.importance_no)
                            Priority.IMPORTANT -> stringResource(id = R.string.importance_high)
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                DropdownMenu(
                    expanded = dropDownMenuVisible,
                    onDismissRequest = { dropDownMenuVisible = false }
                ) {
                    DropdownMenuItem(text = {
                        Text(text = stringResource(id = R.string.importance_no))
                    }, onClick = {
                        onImportanceChange(Priority.BASIC)
                        selectedPriority = Priority.BASIC
                        dropDownMenuVisible = false }
                    )
                    DropdownMenuItem(text = {
                        Text(text = stringResource(id = R.string.importance_low))
                    }, onClick = {
                        onImportanceChange(Priority.LOW)
                        selectedPriority = Priority.LOW
                        dropDownMenuVisible = false }
                    )
                    DropdownMenuItem(text = {
                        Text(text = stringResource(id = R.string.importance_high))
                    }, onClick = {
                        onImportanceChange(Priority.IMPORTANT)
                        selectedPriority = Priority.IMPORTANT
                        dropDownMenuVisible = false }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            if (datePickerVisible) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { datePickerVisible = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selected = datePickerState.selectedDateMillis
                                selectedDate = selected?.let { millis ->
                                    Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDateTime()
                                }
                                onDeadlineChange(selectedDate)
                                datePickerVisible = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.apply))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { datePickerVisible = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Row {
                val selectedDateString = selectedDate?.let { getLocalDateTimeAsString(it) }
                Column(
                    Modifier
                        .clickable { datePickerVisible = true }
                ) {
                    Text(
                        text = stringResource(id = R.string.do_until),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = if (!selectedDateString.isNullOrBlank()) selectedDateString
                                else "",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = !selectedDateString.isNullOrBlank() || datePickerVisible,
                    onCheckedChange = {
                        if (it) datePickerVisible = true
                        else {
                            onDeadlineChange(selectedDate)
                            selectedDate = null
                            datePickerVisible = false
                        }
                    }
                )
            }

            if (deleteEnabled()) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                TextButton(onClick = onDeleteClick) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(id = R.string.delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(text = stringResource(
                        id = R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

//@Composable
//@Preview(name = "Light Theme", showBackground = true)
//private fun EditItemScreenLightPreview() {
//    AppTheme(useDarkTheme = false) {
//        EditItemScreen(
//            viewModel = TodoViewModel(),
//            item = TodoItem(
//                id = "1",
//                text = "Preview  Item",
//                priority = Priority.NORMAL,
//                status = false,
//                deadline = null,
//                creationDate = LocalDateTime.now(),
//                modificationDate = null
//            ),
//            onNavigationClick = {}
//        )
//    }
//}
//
//@Composable
//@Preview(name = "Dark Theme", showBackground = true)
//private fun EditItemScreenDarkPreview() {
//    AppTheme(useDarkTheme = true) {
//        EditItemScreen(
//            viewModel = TodoViewModel(),
//            item = TodoItem(
//                id = "1",
//                text = "Preview Item",
//                priority = Priority.NORMAL,
//                status = false,
//                deadline = "2024-07-01",
//                creationDate = "2024-07-01",
//                modificationDate = "2024-07-01"
//            ),
//            onNavigationClick = {}
//        )
//    }
//}