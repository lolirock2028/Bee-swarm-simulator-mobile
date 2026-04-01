package com.beeswarm.macro.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.macro.MacroOrchestrator
import com.beeswarm.macro.utils.GestureEngine
import com.beeswarm.macro.utils.Logger

class MacroAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MacroAccessibilityService? = null
        var orchestrator: MacroOrchestrator? = null
        const val ACTION_START = "com.beeswarm.macro.ACTION_START"
        const val ACTION_STOP = "com.beeswarm.macro.ACTION_STOP"
    }

    private lateinit var gestureEngine: GestureEngine

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        gestureEngine = GestureEngine(this)

        val metrics = resources.displayMetrics
        gestureEngine.updateScreenSize(metrics.widthPixels, metrics.heightPixels)

        orchestrator = MacroOrchestrator(gestureEngine)
        Logger.i("AccessibilityService: Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Monitor for Roblox app events if needed
    }

    override fun onInterrupt() {
        Logger.w("AccessibilityService: Interrupted")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                Logger.i("AccessibilityService: Start command received")
                orchestrator?.startAll()
            }
            ACTION_STOP -> {
                Logger.i("AccessibilityService: Stop command received")
                orchestrator?.stopAll()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        orchestrator?.stopAll()
        instance = null
        orchestrator = null
        Logger.i("AccessibilityService: Destroyed")
        super.onDestroy()
    }
}
