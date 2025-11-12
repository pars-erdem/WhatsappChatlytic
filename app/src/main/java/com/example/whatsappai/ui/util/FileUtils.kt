package com.example.whatsappai.ui.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

object FileUtils {
    private const val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5MB
    private const val ALLOWED_MIME_TYPE = "text/plain"
    private val ALLOWED_EXTENSIONS = listOf("txt")

    suspend fun readFileContent(context: Context, uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            fileName = cursor.getString(nameIndex)
        }
        return fileName
    }

    fun getFileSize(context: Context, uri: Uri): Long? {
        var fileSize: Long? = null
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            fileSize = cursor.getLong(sizeIndex)
        }
        return fileSize
    }

    fun validateFile(context: Context, uri: Uri): FileValidationResult {
        val fileName = getFileName(context, uri) ?: return FileValidationResult.Error("Could not read file name")
        val fileSize = getFileSize(context, uri) ?: return FileValidationResult.Error("Could not read file size")

        // Check file extension
        val extension = fileName.substringAfterLast('.', "").lowercase()
        if (extension !in ALLOWED_EXTENSIONS) {
            return FileValidationResult.Error("Only .txt files are allowed")
        }

        // Check file size
        if (fileSize > MAX_FILE_SIZE) {
            return FileValidationResult.Error("File size must be less than 5MB")
        }

        // Check MIME type
        val mimeType = context.contentResolver.getType(uri)
        if (mimeType != null && mimeType != ALLOWED_MIME_TYPE) {
            return FileValidationResult.Error("Only text files are allowed")
        }

        return FileValidationResult.Success(fileName, fileSize)
    }
}

sealed class FileValidationResult {
    data class Success(val fileName: String, val fileSize: Long) : FileValidationResult()
    data class Error(val message: String) : FileValidationResult()
}

