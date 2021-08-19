package com.example.androidchatapp.services

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

private const val TAG = "StorageService"
object StorageService {
    val storageRef = Firebase.storage.reference

    fun uploadImageToFirebaseStorage(uri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(uri)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded : ${it.metadata?.path}")
            }
    }
}