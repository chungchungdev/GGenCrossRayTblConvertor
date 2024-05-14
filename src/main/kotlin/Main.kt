package com.chungchungdev

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension


private val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
    val format = Json {
        prettyPrint = true
    }

    val names = listOf(
        "AbilitySpecList",
        "CharacterSpecList",
        "CreditList",
        "DlcTitle",
        "MachineSpecList",
        "messagestring",
        "MiscData",
        "SeriesProfileList",
        "SpecProfileList",
        "StageList"
    )

    val rawFilesNames =
        "src/main/data/raw".toPath().toNioPath()
            .listDirectoryEntries("*.tbl")
            .joinToString(",") { "\"${it.nameWithoutExtension}\"" }
            .also { println(it) }

    extract("CharacterSpecList",format)

}

fun oldRun() {
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
    //packer.pack(editedPath, repackOutputDir, format)
    //packer.pack(decodedUnchanged, repackOutputDir, format)
}

fun extract(filenameNoExtension: String, format: Json) {
    val rawPath = "src/main/data/raw/$filenameNoExtension.tbl".toPath()
    val decodeOutputDir = "src/main/data/decoded/".toPath()

    val extractor = GGCRTextExtractor(logger)
    extractor.extractFile(rawPath, decodeOutputDir, format)
}

fun repack(filenameNoExtension: String, format: Json) {
    val editedPath = "src/main/data/edited/$filenameNoExtension.json".toPath()
    val repackOutputDir = "src/main/data/repack/".toPath()

    val packer = GGCRTextRepacker(logger)
    packer.pack(editedPath, repackOutputDir, format)
}