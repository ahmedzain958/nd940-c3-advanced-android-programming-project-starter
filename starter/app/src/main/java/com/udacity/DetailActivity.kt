package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val notify = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        val download = intent.extras?.getString(FILE_NAME)
        val status = intent.extras?.getString(STATUS)
        filename_text.text = download
        status_text.text = status
        when(status){
            "succeeded" -> status_text.setTextColor(getColor(R.color.colorAccent))
            "failed" -> status_text.setTextColor(getColor(R.color.colorAccent))
        }
        done_button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        notify?.cancelAll()
    }

}
