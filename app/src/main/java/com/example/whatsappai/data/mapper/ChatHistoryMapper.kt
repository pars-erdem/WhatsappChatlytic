package com.example.whatsappai.data.mapper

import com.example.whatsappai.data.local.entity.ChatHistoryEntity
import com.example.whatsappai.data.model.ChatAnalysisResponse
import com.example.whatsappai.data.model.WordFrequency as DataWordFrequency
import com.example.whatsappai.domain.model.ChatHistoryItem
import com.example.whatsappai.domain.model.WordFrequency as DomainWordFrequency
import java.util.Date

fun ChatHistoryEntity.toDomain(): ChatHistoryItem {
    return ChatHistoryItem(
        id = id,
        fileName = fileName,
        fileSize = fileSize,
        uploadDate = uploadDate,
        personalitySummary = personalitySummary,
        communicationPatterns = communicationPatterns,
        topWords = topWords.map { it.toDomain() }
    )
}

fun ChatHistoryItem.toEntity(): ChatHistoryEntity {
    return ChatHistoryEntity(
        id = id,
        fileName = fileName,
        fileSize = fileSize,
        uploadDate = uploadDate,
        personalitySummary = personalitySummary,
        communicationPatterns = communicationPatterns,
        topWords = topWords.map { it.toData() }
    )
}

fun ChatAnalysisResponse.toDomain(fileName: String, fileSize: Long): ChatHistoryItem {
    return ChatHistoryItem(
        fileName = fileName,
        fileSize = fileSize,
        uploadDate = Date(),
        personalitySummary = personalitySummary,
        communicationPatterns = communicationPatterns,
        topWords = topWords.map { it.toDomain() }
    )
}

fun DataWordFrequency.toDomain(): DomainWordFrequency {
    return DomainWordFrequency(
        word = word,
        frequency = frequency
    )
}

fun DomainWordFrequency.toData(): DataWordFrequency {
    return DataWordFrequency(
        word = word,
        frequency = frequency
    )
}

