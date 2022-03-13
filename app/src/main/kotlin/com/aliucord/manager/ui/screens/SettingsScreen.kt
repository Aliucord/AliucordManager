/*
 * Copyright (c) 2022 Juby210 & zt
 * Licensed under the Open Software License version 3.0
 */


package com.aliucord.manager.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aliucord.manager.R
import com.aliucord.manager.preferences.Prefs
import com.aliucord.manager.ui.components.ListItem
import com.aliucord.manager.ui.components.TextField
import com.aliucord.manager.ui.components.settings.*
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Destination
@Composable
fun SettingsScreen() = Column(
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeDialog(
            onDismissRequest = { showThemeDialog = false },
            onConfirm = { Prefs.theme.set(it.ordinal) }
        )
    }

    ThemeSetting(onClick = { showThemeDialog = true })

    PreferenceItem(
        title = { Text(stringResource(R.string.use_black)) },
        description = { Text(stringResource(R.string.use_black_description)) },
        preference = Prefs.useBlack
    )

    Divider()

    PreferenceItem(
        title = { Text(stringResource(R.string.replace_bg)) },
        preference = Prefs.replaceBg
    )

    TextField(
        value = Prefs.appName.get(),
        onValueChange = Prefs.appName::set,
        placeholder = { Text(stringResource(R.string.app_name_setting)) },
        label = { Text(stringResource(R.string.app_name_setting)) },
    )


    val devModeOn = Prefs.devMode.get()

    PreferenceItem(
        title = { Text(stringResource(R.string.developer_mode)) },
        preference = Prefs.devMode
    )

    if (devModeOn) {
        TextField(
            value = Prefs.packageName.get(),
            onValueChange = Prefs.packageName::set,
            placeholder = { Text(stringResource(R.string.package_name)) },
            label = { Text(stringResource(R.string.package_name)) },
        )

        PreferenceItem(
            title = { Text(stringResource(R.string.debuggable)) },
            description = { Text(stringResource(R.string.debuggable_description)) },
            preference = Prefs.debuggable
        )

        val udfs = Prefs.useDexFromStorage

        PreferenceItem(
            title = { Text(stringResource(R.string.use_dex_from_storage)) },
            preference = Prefs.useDexFromStorage
        )

        val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) Prefs.dexLocation.set(uri.path!!)
        }

        val color = if (udfs.get()) {
            Color.Unspecified
        } else {
            MaterialTheme.colorScheme.onBackground.copy(ContentAlpha.disabled)
        }

        // TODO: Make installer use selected dex file
        ListItem(
            modifier = if (udfs.get()) Modifier.clickable {
                picker.launch(arrayOf("application/octet-stream"))
            } else Modifier,
            text = { Text(stringResource(R.string.dex_location), color = color) },
            secondaryText = { Text(Prefs.dexLocation.get(), color = color) }
        )
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = context.cacheDir::deleteRecursively
    ) {
        Text(stringResource(R.string.clear_files_cache), textAlign = TextAlign.Center)
    }
}
