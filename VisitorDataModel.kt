package com.gokuldev.hostellock

data class VisitorDataModel(
    val id: Long,
    val name: String,
    val registerNumber: String,
    val date: String,
    val time: String,
    val capturedImage: ByteArray
)