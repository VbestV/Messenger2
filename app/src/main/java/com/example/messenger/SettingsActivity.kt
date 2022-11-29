package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val user_uid = FirebaseAuth.getInstance().uid
        val old_username = FirebaseDatabase.getInstance().getReference("username")
        val old_iduser = FirebaseDatabase.getInstance().getReference("id_user")
    }
}