package com.example.whatsappai.data.local.converter

import androidx.room.TypeConverter
import com.example.whatsappai.data.model.WordFrequency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WordFrequencyListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<WordFrequency> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<WordFrequency>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<WordFrequency>?): String {
        if (list == null) return "[]"
        return gson.toJson(list)
    }
}

