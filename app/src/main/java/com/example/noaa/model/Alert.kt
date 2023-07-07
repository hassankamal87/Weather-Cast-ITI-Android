package com.example.noaa.model

data class Alert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)
