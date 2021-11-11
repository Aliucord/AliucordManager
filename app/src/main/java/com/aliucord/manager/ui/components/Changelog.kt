package com.aliucord.manager.ui.components

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.aliucord.manager.BuildConfig
import com.aliucord.manager.R
import com.aliucord.manager.ui.theme.discordBrand
import com.aliucord.manager.ui.theme.discordGreen
import com.aliucord.manager.ui.theme.discordRed
import com.aliucord.manager.utils.Plugin
import java.net.URL
import java.util.regex.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val hyperLinkPattern = Pattern.compile("\\[(.+?)]\\((.+?\\))")
@Suppress("RegExpRedundantEscape") // It is very much not redundant and causes a crash lol
private val headerStylePattern = Pattern.compile("\\{(improved|added|fixed)( marginTop)?\\}")
@SuppressLint("ComposableNaming") // Can't use MaterialTheme without Composable, but this is not a component
@Composable
private fun AnnotatedString.Builder.hyperlink(content: String) {
    var idx = 0
    with (hyperLinkPattern.matcher(content)) {
        while (find()) {
            val start = start()
            val end = end()
            val title = group(1)!!
            val url = group(2)!!

            append(content.substring(idx, start))
            withStyle(style = SpanStyle(color = MaterialTheme.colors.primary, textDecoration = TextDecoration.Underline)) {
                pushStringAnnotation(title, url)
                append(title)
                pop()
            }
            idx = end
        }
    }
    if (idx < content.length) append(content.substring(idx))
}

const val bulletPoint = "● "
@Composable
fun Changelog(plugin: Plugin, showDialog: MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }
    val imageBitmap = remember {
        mutableStateOf<ImageBitmap?>(null)
    }

    if (imageBitmap.value == null && plugin.manifest.changelogMedia != null) {
        LaunchedEffect(null) {
            coroutineScope.launch {
                try {
                    imageBitmap.value = URL(plugin.manifest.changelogMedia).openStream().use {
                        BitmapFactory.decodeStream(it)
                    }.asImageBitmap()
                } catch (err: Throwable) {
                    Log.e(BuildConfig.TAG, "Failed to load changelogMedia ${plugin.manifest.changelogMedia}", err)
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .fillMaxHeight(0.9f)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            plugin.manifest.name,
            style = MaterialTheme.typography.h5
        )
        Divider(color = MaterialTheme.colors.primary, modifier = Modifier.padding(vertical = 8.dp))
        imageBitmap.value?.let {
            Image(it, "", modifier = Modifier.fillMaxWidth())
        }
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxHeight(0.92f)
        ) {
            items(plugin.manifest.changelog!!.lines()) {
                var line = it.trim()
                if (line.isNotEmpty()) {
                    when (line[0]) {
                        '#' -> {
                            do {
                                line = line.substring(1);
                            } while (line.startsWith("#"))

                            Text(line.trimStart(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
                        }
                        '*' -> {
                            LinkText(
                                buildAnnotatedString {
                                    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary, fontWeight = FontWeight.Bold)) {
                                        append(bulletPoint)
                                    }
                                    hyperlink(line.substring(1))
                                },
                                Modifier.padding(bottom = 2.dp)
                            )
                        }
                        else -> {
                            when {
                                line.endsWith("marginTop}") -> {
                                    val color = headerStylePattern.matcher(line).run {
                                        val category = if (find()) {
                                            line = this.replaceFirst("")
                                            group(1)
                                        } else null
                                        when (category) {
                                            "improved" -> discordBrand
                                            "added" -> discordGreen
                                            "fixed" -> discordRed
                                            else -> MaterialTheme.colors.onSurface
                                        }
                                    }
                                    Text(line, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(top = 16.dp, bottom = 6.dp))
                                }
                                line.all { c -> c == '=' } -> {} // Discord ignores =======
                                else -> {
                                    LinkText(
                                        buildAnnotatedString {
                                            hyperlink(line)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Button(onClick = { showDialog.value = false }, modifier = Modifier.align(Alignment.End)) {
            Text(stringResource(R.string.close))
        }
    }

}

@Composable
private fun LinkText(annotatedString: AnnotatedString, modifier: Modifier = Modifier) {
    val urlHandler = LocalUriHandler.current
    ClickableText(
        text = annotatedString,
        style = TextStyle(color = LocalContentColor.current),
        onClick = { offset ->
            annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.let {
                urlHandler.openUri(it.item)
            }
        },
        modifier = modifier
    )
}