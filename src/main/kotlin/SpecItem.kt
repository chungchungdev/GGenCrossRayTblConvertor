package com.chungchungdev

import kotlinx.serialization.Serializable

@Serializable
data class SpecItem(
    val index: Int,
    val string: String
)