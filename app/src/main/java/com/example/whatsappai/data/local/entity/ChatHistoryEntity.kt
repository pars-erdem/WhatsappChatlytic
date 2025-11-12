package com.example.whatsappai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.whatsappai.data.local.converter.DateConverter
import com.example.whatsappai.data.local.converter.WordFrequencyListConverter
import java.util.Date

@Entity(tableName = "chat_history")
@TypeConverters(DateConverter::class, WordFrequencyListConverter::class)
data class ChatHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val fileSize: Long,
    val uploadDate: Date,
    val personalitySummary: String,
    val communicationPatterns: String,
    val topWords: List<com.example.whatsappai.data.model.WordFrequency>
)

