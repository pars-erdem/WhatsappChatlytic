package com.example.whatsappai.domain.model

import java.util.Date

data class ChatHistoryItem(
    val id: Long = 0,
    val fileName: String,
    val fileSize: Long,
    val uploadDate: Date,
    val personalitySummary: String,
    val communicationPatterns: String,
    val topWords: List<WordFrequency>
)

data class WordFrequency(
    val word: String,
    val frequency: Int
)

