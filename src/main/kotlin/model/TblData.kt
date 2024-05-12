package com.chungchungdev.model

import kotlinx.serialization.Serializable

@Serializable
data class TblData(
    val startIndex: Int,
    val strings: List<TblItem>
)

@Serializable
data class TblItem(
    val raw: String,
    val edited: String = raw
)
