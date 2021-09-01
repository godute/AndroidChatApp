package com.example.androidchatapp.services

interface StorageInterface {
    fun onImageUploadComplete(filePath: String)
    fun onFileUploadComplete(filePath: String)
}