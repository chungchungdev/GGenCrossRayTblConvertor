package com.chungchungdev

import okio.BufferedSink
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.buffer
import java.nio.file.Files
import java.util.stream.Collectors
import kotlin.io.path.extension
import kotlin.io.path.isDirectory

object GGCRTextExtractor {
    // First octet = file signature, keep it
    // Second octet = unknown??? just keep it
    // Then, each octet is the start index of a string item
    // Octet containing zero only = end of position mapping
    // String items, each is separated by a 0x00 byte

    // result file also include start index of the third octet?
    fun extract(path: Path) {
        FileSystem.SYSTEM.source(path).buffer().use { source ->
            val items: MutableList<String> = mutableListOf()
            val contentLines: MutableList<String> = mutableListOf()
            val secondOctet = ByteArray(8)
            var consecutiveZeros = 0
            var inContent = false
            var contentStartIndex = -1
            var currentIndex = 0
            var previousByte: Byte = 0x01
            var currentByte: Byte

            // Keep second octet
            while (currentIndex in 0..15 && !source.exhausted()) {
                currentByte = source.readByte()
                if (currentIndex in 8..15) {
                    secondOctet[currentIndex - 8] = currentByte
                }
                currentIndex++
            }

            // Find content start index
            while (!inContent && !source.exhausted()) {
                currentByte = source.readByte()
                if (currentByte == 0.toByte() && currentByte == previousByte) {
                    consecutiveZeros++
                } else {
                    consecutiveZeros = 0
                }
                if (consecutiveZeros == 8) {
                    contentStartIndex = currentIndex + 1
                    inContent = true
                }
                previousByte = currentByte
                currentIndex++
            }

            //
            var lineBytes: ByteArray = byteArrayOf()
            while (!source.exhausted()) {
                currentByte = source.readByte()
                if (currentByte == 0.toByte()) {
                    contentLines.add(lineBytes.toString(Charsets.UTF_8))
                }
                lineBytes += currentByte
                currentIndex++
            }
        }
    }

    private fun getAllPaths(path: Path): List<Path> {
        val nioPath = path.toNioPath()
        return if (nioPath.isDirectory()) {
            Files.walk(nioPath, 1)
                .filter { it.extension == "tbl" }
                .collect(Collectors.toList())
                .map { it.toOkioPath() }
                .filter { it.containsTblFileSignature() }
        } else {
            path.takeIf { it.containsTblFileSignature() }
                ?.let { listOf(it) } ?: emptyList()
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun Path.containsTblFileSignature(): Boolean {
        FileSystem.SYSTEM.source(this).buffer().use { source ->
            val signature = source.readByteArray(8)
            return signature.contentEquals(TBL_FILE_SIGNATURE.toByteArray())
        }
    }

    private val TBL_FILE_SIGNATURE = ubyteArrayOf(0x54u, 0x52u, 0x54u, 0x53u, 0x00u, 0x01u, 0x01u, 0x00u)
}