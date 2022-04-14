package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_faq.*

@Suppress("DEPRECATION")
class FaqActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
    }
    override fun onStart() {
        super.onStart()
        val intent = Intent(this, HelpActivity::class.java)
        findViewById<TextView>(R.id.backfaq).setOnClickListener {
            backfaq.isClickable = false
            startActivity(intent)
        }
    }
}