package com.example.whatsappai.data.local

import android.content.Context
import androidx.room.Room
import com.example.whatsappai.data.local.dao.ChatHistoryDao
import com.example.whatsappai.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "whatsapp_ai_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideChatHistoryDao(database: AppDatabase): ChatHistoryDao {
        return database.chatHistoryDao()
    }
}

