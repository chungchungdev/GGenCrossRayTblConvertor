package com.chungchungdev

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import java.nio.file.Files


fun main() {
    val decodeOutputDir = "src/main/kotlin/ggendata/decoded/"
    val path = "src/main/kotlin/ggendata/MachineSpecList.tbl".toPath()
    decodeToFile(path, decodeOutputDir)
    val editedPath = "src/main/kotlin/ggendata/edited/MachineSpecList.tbl".toPath()
    val repackOutputDir = "src/main/kotlin/ggendata/repack/".toPath()
    encodeFromFile(path, editedPath, repackOutputDir)
}

fun decodeToFile(src: Path, destDir: String) {
    val filename = src.name
    FileSystem.SYSTEM.source(src).buffer().use { source ->
        val rawBytes = source.readByteArray()
        val startIndex = findContentStartIndex(rawBytes)
        val content = rawBytes.copyOfRange(startIndex, rawBytes.size)
        val line = mutableListOf<Byte>()
        val contentByLine = StringBuilder()
        for (i in content.indices) {
            if (content[i] != 0.toByte()) {
                line.add(content[i])
            }
            if (content[i] == 0.toByte()) {
                contentByLine
                    .append(line.toByteArray().toString(Charsets.UTF_8))
                    .append("\n")
                line.clear()
            }
            if (i == content.size - 1) {
                contentByLine.setLength(contentByLine.length - 1)
            }
        }
        FileSystem.SYSTEM.sink("$destDir$filename".toPath()).buffer().use { sink ->
            sink.writeUtf8(contentByLine.toString())
        }
    }
}

fun encodeFromFile(raw: Path, edited: Path, destDir: Path) {
    val filename = edited.name
    FileSystem.SYSTEM.source(raw).buffer().use { rawSource->
        var noiseBytes = getNoise(rawSource.readByteArray())
        FileSystem.SYSTEM.source(edited).buffer().use{editedSource->
            val editedLines = editedSource.readUtf8().split("\n")
            for (line in editedLines) {
                noiseBytes += line.toByteArray() + 0.toByte()
            }
            FileSystem.SYSTEM.sink("$destDir/$filename".toPath()).buffer().use {
                it.write(noiseBytes)
            }
        }
    }
}

private fun getNoise(bytes: ByteArray): ByteArray {
    val startIndex = findContentStartIndex(bytes)
    return bytes.copyOfRange(0, startIndex)
}

private fun findContentStartIndex(bytes: ByteArray): Int {
    val octetBytes = mutableListOf<Byte>()
    var startIndex = -1
    for (i in bytes.indices) {
        octetBytes.add(bytes[i])
        if (octetBytes.size == 8) {
            if (isNoiseEndOctet(octetBytes)) {
                startIndex = i + 1
                break
            }
            octetBytes.clear()
        }
    }
    return startIndex
}

private fun isNoiseEndOctet(bytes: MutableList<Byte>): Boolean {
    return bytes.fold(true) { acc, byte -> (byte.toUInt() == 0x00u) && acc }
}