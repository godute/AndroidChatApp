package com.example.androidchatapp.services

import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.androidchatapp.GlobalApplication
import com.example.androidchatapp.models.FileInfo
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
        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(uri)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded : ${it.metadata?.path}")
                it.metadata?.path?.let { it1 -> _storageListener.onImageUploadComplete(it1) }
            }
    }

    fun uploadFileToFirebaseStorage(roomId: String, fileInfo: FileInfo, byteArray: ByteArray) {
        val ref = FirebaseStorage.getInstance().getReference("/files/$roomId/${fileInfo.fileName}")
        ref.putBytes(byteArray)
            .addOnSuccessListener {
                Log.d(TAG, "File Successfully uploaded : ${it.metadata?.path}")
                it.metadata?.path?.let { it1 -> _storageListener.onFileUploadComplete(it1) }
            }
            .addOnFailureListener {
                Log.e(TAG, "File upload failed")
            }
    }

    fun downloadFileFromFirebaseStorage(filePath: String) {
        val ref = FirebaseStorage.getInstance().getReference(filePath)
        val fileName = filePath.substring(filePath.lastIndexOf("/")+1)
        ref.getBytes(1024*1024*10).addOnSuccessListener { byteArray->
            if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val contentResolver = GlobalApplication.getContext().contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                val uri = contentResolver.insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), contentValues)
                val os = contentResolver.openOutputStream(uri!!)
                os!!.write(byteArray)
                os!!.close()
                Toast.makeText(
                    GlobalApplication.getContext(),
                    "Download Success",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}