package com.lifeforge.app.shield

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.lifeforge.app.LifeForgeApp
import com.lifeforge.app.data.ShieldPreferences

class ShieldOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private lateinit var preferences: ShieldPreferences
    private var frictionTimer: CountDownTimer? = null

    override fun onCreate() {
        super.onCreate()
        preferences = ShieldPreferences(this)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_HIDE_OVERLAY) {
            removeOverlay()
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, createNotification())
        showOverlay()
        return START_STICKY
    }

    private fun showOverlay() {
        if (overlayView != null) return

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )
        layoutParams.gravity = Gravity.CENTER

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#E60B0F19")) // 90% opaque deep night dark
            setPadding(60, 60, 60, 60)
        }

        // Title
        val titleView = TextView(this).apply {
            text = "🌙 Bedtime Sanctuary Active"
            textSize = 24f
            setTextColor(Color.parseColor("#38BDF8")) // Sky blue
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 30)
        }
        layout.addView(titleView)

        // Family Pledge
        val pledgeView = TextView(this).apply {
            text = "“${preferences.familyPledge}”"
            textSize = 18f
            setTextColor(Color.parseColor("#F8FAFC"))
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            gravity = Gravity.CENTER
            setPadding(20, 20, 20, 40)
        }
        layout.addView(pledgeView)

        // Reason & Routine reminder
        val subTextView = TextView(this).apply {
            text = "Target: 7 to 8 Hours Deep Sleep\nWake up at ${String.format("%02d:%02d", preferences.wakeHour, preferences.wakeMinute)} AM refreshed for gym, learning & your daughters."
            textSize = 15f
            setTextColor(Color.parseColor("#94A3B8"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
        }
        layout.addView(subTextView)

        // Friction Emergency Unlock Button
        val unlockButton = Button(this).apply {
            text = "Emergency 10-Min Bypass"
            setBackgroundColor(Color.parseColor("#1E293B"))
            setTextColor(Color.parseColor("#EF4444"))
        }

        val countdownView = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.parseColor("#F59E0B"))
            gravity = Gravity.CENTER
            visibility = View.GONE
            setPadding(0, 20, 0, 0)
        }

        unlockButton.setOnClickListener {
            unlockButton.isEnabled = false
            countdownView.visibility = View.VISIBLE
            val totalSeconds = preferences.frictionSeconds

            frictionTimer?.cancel()
            frictionTimer = object : CountDownTimer((totalSeconds * 1000).toLong(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secLeft = millisUntilFinished / 1000
                    countdownView.text = "Waiting ${secLeft}s deliberate pause to prevent impulse scrolling..."
                }

                override fun onFinish() {
                    countdownView.text = "Temporary 10-minute bypass granted."
                    removeOverlay()
                    // Re-show after 10 mins
                    postDelayedReactivate()
                }
            }.start()
        }

        layout.addView(unlockButton)
        layout.addView(countdownView)

        overlayView = layout
        try {
            windowManager?.addView(overlayView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun postDelayedReactivate() {
        android.os.Handler(mainLooper).postDelayed({
            showOverlay()
        }, 10 * 60 * 1000L) // 10 minutes
    }

    private fun removeOverlay() {
        frictionTimer?.cancel()
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            overlayView = null
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, LifeForgeApp.SHIELD_CHANNEL_ID)
            .setContentTitle("Guardian Overlay Active")
            .setContentText("Protecting your sleep and bedtime routine.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_SHOW_OVERLAY = "com.lifeforge.app.ACTION_SHOW_OVERLAY"
        const val ACTION_HIDE_OVERLAY = "com.lifeforge.app.ACTION_HIDE_OVERLAY"
        private const val NOTIFICATION_ID = 9002
    }
}
