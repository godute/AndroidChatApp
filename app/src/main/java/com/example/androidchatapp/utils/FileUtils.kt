package com.example.androidchatapp.utils

object FileUtils {
    enum class FileType {
        PDF, DOC, PPT
    }

    fun convertFile(mimeType: String): FileType {
        when(mimeType) {
            "application/pdf" -> return FileType.PDF
            "application/msword" -> return FileType.DOC
            else -> return FileType.PPT
        }
    }
}