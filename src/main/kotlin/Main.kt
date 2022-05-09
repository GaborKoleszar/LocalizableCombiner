// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.*

var numberOfAdditionalLines = 0

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Localizable.strings combiner",
        state = rememberWindowState(width = 400.dp, height = 460.dp)
    ) {
        val count = remember { mutableStateOf(0) }
        val filePathMainFile = remember { mutableStateOf("") }
        val filePathAdditionalFile = remember { mutableStateOf("") }
        val filePathOutputFile = remember { mutableStateOf("") }
        val infoBoxValue = remember { mutableStateOf("Waiting...") }

        MaterialTheme {
            Column(
                Modifier.fillMaxSize().padding(12.dp), Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = filePathMainFile.value,
                    onValueChange = { filePathMainFile.value = it },
                    placeholder = { Text(text = "File path for the main file...") },
                    label = { Text("Into this file") })
                OutlinedTextField(
                    value = filePathAdditionalFile.value,
                    onValueChange = { filePathAdditionalFile.value = it },
                    placeholder = { Text(text = "File path for extra resources file...") },
                    label = { Text("Add these resources") }
                )
                OutlinedTextField(
                    value = filePathOutputFile.value,
                    onValueChange = { filePathOutputFile.value = it },
                    placeholder = { Text(text = "File path for output file...") },
                    label = { Text("Output of result") }
                )
                Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        val listOut = readFile(filePathMainFile.value)
                        val listSource = readFile(filePathAdditionalFile.value)
                        val outputList = combineTwoLists(listOut, listSource)
                        if (filePathOutputFile.value.isNotEmpty()) {
                            writeIntoFile(outputList, filePathOutputFile.value)
                            infoBoxValue.value =
                                "Main file has ${listOut.size} lines, additional has ${listSource.size} lines\n" +
                                        "output has ${outputList.size} lines, $numberOfAdditionalLines keys added\n"
                        } else {
                            infoBoxValue.value = "Please fill in the fields"
                        }
                    }) {
                    Text("Combine")
                }
                Text(
                    infoBoxValue.value,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun writeIntoFile(resourceList: List<Resource>, filepath: String) {
    //Clean source file
    PrintWriter(filepath).close()
    val writer = File(filepath).printWriter()
    writer.use { out ->
        for (line in resourceList)
            out.println(line.toString())
    }
    writer.close()
}

fun combineTwoLists(mainList: List<Resource>, additionalList: List<Resource>): List<Resource> {
    val keyListOfMain = mutableListOf<String>()
    val outputList = mutableListOf<Resource>()

    for (line in mainList) {
        keyListOfMain.add(line.key)
        outputList.add(line)
    }

    for (line in additionalList) {
        if (!keyListOfMain.contains(line.key)) {
            outputList.add(line)
            numberOfAdditionalLines++
        }
    }
    return outputList
}

fun readFile(path: String): List<Resource> {
    val file = File(path)
    try {
        val resourceList = mutableListOf<Resource>()
        BufferedReader(FileReader(file)).use { br ->
            var line: String?
            while (br.readLine().also { line = it } != null) {
                resourceList.add(parseLine(line!!))
            }
        }
        return resourceList
    } catch (e: IOException) {
        e.printStackTrace()
        return emptyList()
    }
}

fun parseLine(line: String): Resource {
    val splitLine = line.split("=").toMutableList()
    //Handle = signs in resource value part
    if (splitLine.size > 2) {
        val builder: StringBuilder = StringBuilder()
        for (i in 1 until splitLine.size) {
            if (i > 1) builder.append("=")
            builder.append(splitLine[i])
        }
        splitLine[1] = builder.toString()
        println(splitLine[1])
    }
    return Resource(splitLine[0], splitLine[1])
}
