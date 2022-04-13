package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_authors.*

@Suppress("DEPRECATION")
class AuthorsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authors)
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.backauth).setOnClickListener {
            backauth.isClickable = false
            startActivity(intent)
        }
    }
}