package com.example.whatsappai.data.model

import com.google.gson.annotations.SerializedName

data class ChatAnalysisResponse(
    @SerializedName("personality_summary")
    val personalitySummary: String,
    @SerializedName("communication_patterns")
    val communicationPatterns: String,
    @SerializedName("top_words")
    val topWords: List<WordFrequency>
)

data class WordFrequency(
    @SerializedName("word")
    val word: String,
    @SerializedName("frequency")
    val frequency: Int
)

