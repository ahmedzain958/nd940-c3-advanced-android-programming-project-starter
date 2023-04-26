package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloader: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        downloader = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        createNotificationChannel(
            getString(R.string.channel),
            getString(R.string.notification_title)
        )
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            if (radioGroup.checkedRadioButtonId != -1) {
                custom_button.state(ButtonState.Clicked)
            }
            when (radioGroup.checkedRadioButtonId) {
                R.id.radioButton1 -> download("https://github.com/bumptech/glide")
                R.id.radioButton2 -> download("https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter")
                R.id.radioButton3 -> download("https://github.com/square/retrofit")
                else -> Toast.makeText(
                    this,
                    "Please select the file to download",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            val cursor = downloader.query(DownloadManager.Query().setFilterById(id!!))
            if (cursor.moveToNext()) {
                val currentState =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val notificationManager =
                    ContextCompat.getSystemService(context!!, NotificationManager::class.java)
                cursor.close()
                when (currentState) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        notificationManager?.sendNotification(
                            context,
                            context.getString(
                                when (radioGroup.checkedRadioButtonId) {
                                    R.id.radioButton1 -> R.string.option1
                                    R.id.radioButton2 -> R.string.option2
                                    else -> R.string.option3
                                }
                            ), "success"
                        )
                    }
                    DownloadManager.STATUS_FAILED -> {
                        notificationManager?.sendNotification(
                            context,
                            context.getString(
                                when (radioGroup.checkedRadioButtonId) {
                                    R.id.radioButton1 -> R.string.option1
                                    R.id.radioButton2 -> R.string.option2
                                    else -> R.string.option3
                                }
                            ),
                            "fail"
                        )
                    }
                }
            }
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        downloader.enqueue(request)
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifyChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_HIGH
            )
            notifyChannel.let {
                it.enableLights(true)
                it.setShowBadge(false)
                it.enableVibration(true)
                it.lightColor = Color.CYAN
                it.description = applicationContext.getString(R.string.notification_description)
            }
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notifyChannel)

        }
    }

    fun NotificationManager.sendNotification(context: Context, fileName: String, state: String) {

        val intent = Intent(context, DetailActivity::class.java).also {
            it.putExtra(FILE_NAME, fileName)
            it.putExtra(STATUS, state)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(
            context, context.getString(R.string.channel)
        )
            .setSmallIcon(R.drawable.download)
            .setContentText(fileName)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.download,
                context.getString(R.string.go_to_detail),
                pendingIntent
            )
        notify(REQUEST_CODE, builder.build())
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
