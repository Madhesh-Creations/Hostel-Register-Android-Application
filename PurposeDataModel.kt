package com.gokuldev.hostellock

data class PurposeDataModel(
    val id: Long,
    val name: String,
    val registerNumber: String,
    val date: String,
    val time: String,
    val purpose: String,
    val capturedImage: ByteArray
)