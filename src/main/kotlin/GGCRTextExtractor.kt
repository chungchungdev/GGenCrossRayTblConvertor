package com.chungchungdev

import com.chungchungdev.model.TblItem
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import kotlin.io.path.nameWithoutExtension

class GGCRTextExtractor(
    private val logger: KLogger
) {
    // First octet = file signature, keep it
    // Second octet = table items count
    // Then, each octet is the start index of a string item
    // Octet containing zero only = end of position mapping
    // String items, each is separated by a 0x00 byte

    // result file also include start index of the third octet?
    fun extract(src: Path, out: Path) {

    }

    fun extractFile(src: Path, outDir: Path, format: Json) {
        val lines = extractStrings(src).map { TblItem(it, it) }
        val outputJson = format.encodeToString(lines)
        val outPath = "$outDir/${src.toNioPath().nameWithoutExtension}.json".toPath()
        logger.debug { "outPath: $outPath" }
        FileSystem.SYSTEM.sink(outPath).buffer().use { sink ->
            sink.writeUtf8(outputJson)
        }
    }

    private fun extractStrings(src: Path): List<String> {
        FileSystem.SYSTEM.source(src).buffer().use { source ->
            logger.trace { "Initial:" }
            val rawBytes = source.readByteArray()

            if (rawBytes.size <= 16) {
                return emptyList()
            }
            val secondOctet = rawBytes.copyOfRange(8, 16).joinToString(",") { it.toString() }
                .also { logger.debug { it } }
            val thirdOctet = rawBytes.copyOfRange(16, 24).joinToString(",") { it.toString() }
                .also { logger.debug { it } }

            var currIndexBytesStartIndex = 16
            var nextIndexBytesStartIndex = currIndexBytesStartIndex
            var currItemStartIndex = getItemStartIndex(currIndexBytesStartIndex, rawBytes)
            var nextItemStartIndex: Int
            val lines: MutableList<String> = mutableListOf(secondOctet, thirdOctet)


            logger.trace { "In loop:" }
            while (nextIndexBytesStartIndex + 8 < rawBytes.size) {
                nextIndexBytesStartIndex += 8
                nextItemStartIndex = getItemStartIndex(nextIndexBytesStartIndex, rawBytes)
                logger.trace { "itemStartIndex: $currItemStartIndex" }
                logger.trace { "nextStartIndex: $nextItemStartIndex" }
                if (nextItemStartIndex == 0) {
                    nextItemStartIndex = rawBytes.size
                }
                rawBytes.readUtf8Line(currItemStartIndex, nextItemStartIndex)
                    .also { lines.add(it) }
                currIndexBytesStartIndex += 8
                currItemStartIndex = getItemStartIndex(nextIndexBytesStartIndex, rawBytes)
                if (nextItemStartIndex == rawBytes.size) {
                    break
                }
                nextItemStartIndex = getItemStartIndex(nextIndexBytesStartIndex + 8, rawBytes)
            }
            //println(lines.joinToString("\n"))
            logger.trace { lines.fold(0) { acc, s -> acc + s.length } }
            logger.debug { "item count: ${lines.size} lines" }
            return lines
        }
    }

    private fun getItemStartIndex(indexByteStartIndex: Int, rawBytes: ByteArray): Int {
        rawBytes.copyOfRange(indexByteStartIndex, indexByteStartIndex + 4)
            .also { logger.trace { it.toHexString(HexFormat.Default) } }
        return (((rawBytes[indexByteStartIndex].toUInt() and 0xFFu)).also { logger.trace { it.toString(16) } } or
                ((rawBytes[indexByteStartIndex + 1].toUInt() and 0xFFu).also { logger.trace { it.toString(16) } } shl 8) or
                ((rawBytes[indexByteStartIndex + 2].toUInt() and 0xFFu).also { logger.trace { it.toString(16) } } shl 16) or
                ((rawBytes[indexByteStartIndex + 3].toUInt() and 0xFFu).also { logger.trace { it.toString(16) } } shl 24))
            .toInt()
    }
}