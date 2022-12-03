package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.*
import java.util.*


@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = "Settings"

        select_photo_button_settings.setOnClickListener {
            Log.d("MainActivity", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        settings_button_edit.setOnClickListener {

            uploadImageToFirebaseStorage()
        }
    }
    var selectedPhotoUri: Uri? = null

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    // Log.d("RegisterActivity", "Successfully upload image: ${it.metadata.path}")
                    updateData(it.toString())
                }
            }
            .addOnFailureListener {

            }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_view_settings.setImageBitmap(bitmap)

            select_photo_button_settings.alpha =0f
        }
    }

    private fun updateData(profileImage: String) {
        database = FirebaseDatabase.getInstance().getReference("users")
        val user = mapOf<String,String>(
            "profileImageUrl" to profileImage,
            "uid" to FirebaseAuth.getInstance().uid.toString(),
            "username" to new_username_edittext_settings.text.toString(),

        )

        database.child(FirebaseAuth.getInstance().uid.toString()).updateChildren(user).addOnCompleteListener{
            Toast.makeText(this, "Success",Toast.LENGTH_SHORT).show()
        }
    }
}