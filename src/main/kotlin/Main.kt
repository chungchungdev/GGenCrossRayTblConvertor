package com.chungchungdev

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath


private val logger = KotlinLogging.logger { }

fun main() {
    val rawPath = "src/main/data/raw/MachineSpecList.tbl".toPath()
    val decodeOutputDir = "src/main/data/decoded/".toPath()
    val editedPath = "src/main/data/edited/MachineSpecList.txt".toPath()
    val repackOutputDir = "src/main/data/repack/".toPath()
    //decodeToFile(path, decodeOutputDir)
    //encodeFromFile(path, editedPath, repackOutputDir)

    val format = Json {
        prettyPrint = true
    }

    val extractor = GGCRTextExtractor(logger)
    extractor.extractFile(rawPath, decodeOutputDir, format)

    val decodedUnchanged = "src/main/data/edited/MachineSpecList_same.txt".toPath()
    val packer = GGCRTextRepacker(logger)
    //packer.pack(editedPath, repackOutputDir)
    //packer.pack(decodedUnchanged, repackOutputDir)
}