package com.example.hellohud

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.hellohud.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isFocusSession = false
    private var breathingAnimator: AnimatorSet? = null

    companion object {
        private const val INHALE_DURATION = 4000L  // 4 seconds
        private const val EXHALE_DURATION = 8000L  // 8 seconds
        private const val MIN_SCALE = 1.0f
        private const val MAX_SCALE = 2.5f
    }

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
        startBreathingAnimation()
    }

    private fun setupButton() {
        binding.actionButton.setOnClickListener {
            isFocusSession = !isFocusSession
            if (isFocusSession) {
                binding.actionButton.text = "End Focus Session"
            } else {
                binding.actionButton.text = "Start Focus Session"
            }
        }
    }

    private fun startBreathingAnimation() {
        val orb = binding.breatheOrb

        // Inhale animation (grow)
        val inhaleScaleX = ObjectAnimator.ofFloat(orb, "scaleX", MIN_SCALE, MAX_SCALE).apply {
            duration = INHALE_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
        val inhaleScaleY = ObjectAnimator.ofFloat(orb, "scaleY", MIN_SCALE, MAX_SCALE).apply {
            duration = INHALE_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Exhale animation (shrink)
        val exhaleScaleX = ObjectAnimator.ofFloat(orb, "scaleX", MAX_SCALE, MIN_SCALE).apply {
            duration = EXHALE_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
        val exhaleScaleY = ObjectAnimator.ofFloat(orb, "scaleY", MAX_SCALE, MIN_SCALE).apply {
            duration = EXHALE_DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Combine into inhale set
        val inhaleSet = AnimatorSet().apply {
            playTogether(inhaleScaleX, inhaleScaleY)
        }

        // Combine into exhale set
        val exhaleSet = AnimatorSet().apply {
            playTogether(exhaleScaleX, exhaleScaleY)
        }

        // Run breathing cycle
        breathingAnimator = AnimatorSet().apply {
            playSequentially(inhaleSet, exhaleSet)
        }

        // Update text based on breathing phase
        updateBreathingText()

        breathingAnimator?.start()

        // Restart animation when complete
        breathingAnimator?.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationEnd(animation: android.animation.Animator) {
                startBreathingAnimation()
            }
            override fun onAnimationCancel(animation: android.animation.Animator) {}
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
    }

    private fun updateBreathingText() {
        binding.breatheText.text = "Inhale"
        handler.postDelayed({
            binding.breatheText.text = "Exhale"
        }, INHALE_DURATION)
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
        breathingAnimator?.cancel()
    }
}