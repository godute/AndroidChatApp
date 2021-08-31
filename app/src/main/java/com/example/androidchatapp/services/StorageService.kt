package com.example.androidchatapp.services

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

private const val TAG = "StorageService"
object StorageService {
    private lateinit var _storageListener: StorageInterface
    val storageRef = Firebase.storage.reference

    fun setStorageListener(listener: StorageInterface)  {
        _storageListener = listener
    }

    fun uploadImageToFirebaseStorage(uri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(uri)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded : ${it.metadata?.path}")
                it.metadata?.path?.let { it1 -> _storageListener.onFileUploadComplete(it1) }
            }
    }

    fun readImageFromFirebaseStorage(fileName: String) {
        val ref = FirebaseStorage.getInstance().getReference(fileName)

    }
}