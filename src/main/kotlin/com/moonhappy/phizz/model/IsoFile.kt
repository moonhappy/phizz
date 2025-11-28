package com.moonhappy.phizz.model

import kotlinx.serialization.Serializable

@Serializable
data class IsoFile(
    val filename: String,
    val path: String,
    val size: Long
)
