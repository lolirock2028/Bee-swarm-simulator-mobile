package com.beeswarm.macro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.beeswarm.macro.R
import com.beeswarm.macro.utils.Logger
import com.beeswarm.macro.utils.StatsTracker
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private var panelVisible = false

    companion object {
        const val CHANNEL_ID = "bee_swarm_overlay"
        const val NOTIFICATION_ID = 1001
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        setupOverlay()
        startStatsUpdater()
        Logger.i("OverlayService: Created")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Macro Overlay", NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bee Swarm Macro")
            .setContentText("Macro is running")
            .setSmallIcon(R.drawable.ic_bee)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun setupOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = LayoutInflater.from(this).inflate(R.layout.layout_floating_overlay, null)

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0; y = 200
        }

        // Make FAB draggable
        val fab = overlayView?.findViewById<FloatingActionButton>(R.id.overlayFab)
        val panel = overlayView?.findViewById<View>(R.id.overlayPanel)

        var initialX = 0; var initialY = 0
        var initialTouchX = 0f; var initialTouchY = 0f

        fab?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x; initialY = params.y
                    initialTouchX = event.rawX; initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(overlayView, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val moved = Math.abs(event.rawX - initialTouchX) > 10 ||
                                Math.abs(event.rawY - initialTouchY) > 10
                    if (!moved) {
                        panelVisible = !panelVisible
                        panel?.visibility = if (panelVisible) View.VISIBLE else View.GONE
                    }
                    true
                }
                else -> false
            }
        }

        // Stop button
        overlayView?.findViewById<MaterialButton>(R.id.overlayStop)?.setOnClickListener {
            MacroAccessibilityService.orchestrator?.stopAll()
            stopSelf()
        }

        // Pause button
        overlayView?.findViewById<MaterialButton>(R.id.overlayPause)?.setOnClickListener {
            val orch = MacroAccessibilityService.orchestrator
            if (orch?.isAnyRunning() == true) {
                orch.stopAll()
                (it as MaterialButton).text = "RESUME"
            } else {
                orch?.startAll()
                (it as MaterialButton).text = "PAUSE"
            }
        }

        windowManager.addView(overlayView, params)
    }

    private fun startStatsUpdater() {
        handler.post(object : Runnable {
            override fun run() {
                overlayView?.findViewById<TextView>(R.id.overlayRuntime)?.text = StatsTracker.getRuntime()
                overlayView?.findViewById<TextView>(R.id.overlayStatus)?.text =
                    if (StatsTracker.isRunning) "RUNNING" else "STOPPED"
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
        Logger.i("OverlayService: Destroyed")
        super.onDestroy()
    }
}
