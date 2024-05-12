package com.chungchungdev

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import java.nio.file.Files
import java.nio.file.Paths


fun main() {
    val outputDir = "src/main/kotlin/ggendata/modified/"
    val path = "src/main/kotlin/ggendata/MachineSpecList.tbl".toPath(normalize = true)
    decodeToFile(path, outputDir)
}

fun decodeToFile(src: Path, destDir: String) {
    val filename = src.name.split(".")[0]
    val fileExtension = ".txt"
    val rawBytes = FileSystem.SYSTEM.source(src).buffer().readByteArray()
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
    val finalContent = contentByLine.toString()
        .also { println("final content char count: ${it.length}") }

    FileSystem.SYSTEM.sink("$destDir$filename-okio$fileExtension".toPath()).buffer().writeUtf8(finalContent)
    Files.writeString("$destDir$filename-nio$fileExtension".toPath().toNioPath(), finalContent, Charsets.UTF_8)

    val okioOutput = "src/main/kotlin/ggendata/modified/MachineSpecList-okio.txt".toPath()
        .let { FileSystem.SYSTEM.source(it).buffer().readUtf8() }
    val nioOutput = Paths.get("src/main/kotlin/ggendata/modified/MachineSpecList-nio.txt")
        .let { Files.readString(it, Charsets.UTF_8) }
    println("okio output same as source? ${okioOutput.contentEquals(finalContent)}")
    println("nio output same as source? ${nioOutput!!.contentEquals(finalContent)}")
}

fun encodeFromFile(file: Path) {

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