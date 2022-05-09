// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Localizable.strings combiner",
        state = rememberWindowState(width = 400.dp, height = 350.dp)
    ) {
        val count = remember { mutableStateOf(0) }
        val filePathOutput = remember { mutableStateOf("") }
        val filePathSource = remember { mutableStateOf("") }

        MaterialTheme {
            Column(
                Modifier.fillMaxSize(), Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = filePathOutput.value,
                    onValueChange = { filePathOutput.value = it },
                    placeholder = { Text(text = "File path for output file...") },
                    label = { Text("Output file") })
                OutlinedTextField(
                    value = filePathSource.value,
                    onValueChange = { filePathSource.value = it },
                    placeholder = { Text(text = "File path for source file...") },
                    label = { Text("Source file") }
                )
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        readFile(filePathOutput.value)
                    }) {
                    Text("Combine")
                }
                Text("Waiting...")
            }
        }
    }
}

fun readFile(path: String)  {
    val file = File(path)
    try {
        BufferedReader(FileReader(file)).use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                println(line)
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
