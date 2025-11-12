package com.example.whatsappai.data.remote.api

import com.example.whatsappai.data.model.ChatAnalysisResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ChatAnalysisApi {
    @Multipart
    @POST("analyze")
    suspend fun uploadAndAnalyze(
        @Part file: MultipartBody.Part
    ): Response<ChatAnalysisResponse>
}

