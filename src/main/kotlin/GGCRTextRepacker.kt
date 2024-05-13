package com.chungchungdev

import com.chungchungdev.model.TblItem
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GGCRTextRepacker(
    private val logger: KLogger
) {
    // get start index
    // count the bytes of the strings items

    fun pack(src: Path, outDir: Path, format: Json) {
        val outPath = "$outDir/${src.name.split(".")[0]}.tbl".toPath()

        FileSystem.SYSTEM.source(src).buffer().use { source ->
            val lines = source.readUtf8().let { format.decodeFromString<List<String>>(it) }

            FileSystem.SYSTEM.sink(outPath).buffer().use { sink ->
                // Write file signature
                sink.write(GGCRFile.FileSignature.STRING_TBL.toByteArray())
                // Write second octet
                sink.write(lines[0].toOctetBytes())
                //logger.debug { lines[0].split(",").map { it.toUByte().toByte() } }
                val startIndex = lines[1]
                    .toOctetBytes()
                    .also { sink.write(it) } // write third octet
                    .intFromBigEndianOctetBytes()

                var currentLineIndex = 2
                var nextItemBytesStartIndex = startIndex
                while (currentLineIndex < lines.size - 1) {
                    (nextItemBytesStartIndex + lines[currentLineIndex].toByteArray(Charsets.UTF_8).size + 1)
                        .also { nextItemBytesStartIndex = it }
                        .toBigEndianOctetBytes()
                        .also { sink.write(it) }
                    currentLineIndex++
                }
                sink.write(ByteArray(8) { 0x00 }) // separator of index bytes and content bytes

                currentLineIndex = 2
                while (currentLineIndex < lines.size) {
                    lines[currentLineIndex]
                    sink.writeString(lines[currentLineIndex], Charsets.UTF_8)
                    sink.writeByte(0x00) // separator
                    currentLineIndex++
                }
            }
        }
    }

    /*fun writeIndexBytes(contentStartIndex: Int, rawBytes: ByteArray) {
        val dataSize = rawBytes.size
        require(contentStartIndex < dataSize) { "Array index out of bound. $contentStartIndex/$dataSize" }


    }*/
}