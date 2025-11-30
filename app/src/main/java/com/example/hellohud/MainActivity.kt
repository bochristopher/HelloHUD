package com.example.hellohud

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.hellohud.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isFocusSession = false

    private val timeRunnable = object : Runnable {
        override fun run() {
            updateTime()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButton()
        startClock()
    }

    private fun setupButton() {
        binding.actionButton.setOnClickListener {
            isFocusSession = !isFocusSession
            updateReminderText()
        }
    }

    private fun updateReminderText() {
        if (isFocusSession) {
            binding.breatheText.text = "Focus session started. Stay present."
        } else {
            binding.breatheText.text = "Remember to breathe"
        }
    }

    private fun startClock() {
        handler.post(timeRunnable)
    }

    private fun updateTime() {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        binding.timeText.text = currentTime
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timeRunnable)
    }
}