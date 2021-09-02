package com.example.androidchatapp.models

import com.example.androidchatapp.utils.FileUtils

data class FileInfo(
    val fileName: String,
    val fileType: FileUtils.FileType
)