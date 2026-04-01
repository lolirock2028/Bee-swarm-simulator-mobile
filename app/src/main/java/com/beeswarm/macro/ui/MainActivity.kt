package com.beeswarm.macro.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.beeswarm.macro.R
import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.service.MacroAccessibilityService
import com.beeswarm.macro.service.OverlayService
import com.beeswarm.macro.utils.Logger
import com.beeswarm.macro.utils.StatsTracker
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var runtimeText: TextView
    private lateinit var btnStart: MaterialButton
    private lateinit var btnStop: MaterialButton
    private lateinit var btnPause: MaterialButton
    private lateinit var statPollen: TextView
    private lateinit var statQuests: TextView
    private lateinit var statMobs: TextView
    private lateinit var statGestures: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MacroConfig.init(this)
        initViews()
        setupListeners()
        startUIUpdater()
    }

    private fun initViews() {
        statusText = findViewById(R.id.statusText)
        runtimeText = findViewById(R.id.runtimeText)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        btnPause = findViewById(R.id.btnPause)
        statPollen = findViewById(R.id.statPollen)
        statQuests = findViewById(R.id.statQuests)
        statMobs = findViewById(R.id.statMobs)
        statGestures = findViewById(R.id.statGestures)
    }

    private fun setupListeners() {
        // Feature switches
        findViewById<SwitchMaterial>(R.id.switchFarm).setOnCheckedChangeListener { _, checked ->
            MacroConfig.autoFarmEnabled = checked
        }
        findViewById<SwitchMaterial>(R.id.switchQuest).setOnCheckedChangeListener { _, checked ->
            MacroConfig.autoQuestEnabled = checked
        }
        findViewById<SwitchMaterial>(R.id.switchMob).setOnCheckedChangeListener { _, checked ->
            MacroConfig.mobDefeatEnabled = checked
        }
        findViewById<SwitchMaterial>(R.id.switchConvert).setOnCheckedChangeListener { _, checked ->
            MacroConfig.autoConvertEnabled = checked
        }

        // Control buttons
        btnStart.setOnClickListener { startMacro() }
        btnStop.setOnClickListener { stopMacro() }
        btnPause.setOnClickListener { togglePause() }

        // Settings button
        findViewById<MaterialButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun startMacro() {
        if (!checkAccessibilityService()) {
            showAccessibilityDialog()
            return
        }
        if (!checkOverlayPermission()) {
            requestOverlayPermission()
            return
        }

        Logger.i("MainActivity: Starting macro")
        StatsTracker.reset()

        val intent = Intent(this, MacroAccessibilityService::class.java)
        intent.action = MacroAccessibilityService.ACTION_START

        // Start overlay
        if (MacroConfig.showOverlay) {
            startService(Intent(this, OverlayService::class.java))
        }

        isRunning = true
        updateStatus("RUNNING", getColor(R.color.status_running))
        Toast.makeText(this, "Macro started! Switch to Roblox now.", Toast.LENGTH_LONG).show()
    }

    private fun stopMacro() {
        Logger.i("MainActivity: Stopping macro")
        MacroAccessibilityService.orchestrator?.stopAll()
        stopService(Intent(this, OverlayService::class.java))
        isRunning = false
        updateStatus("IDLE", getColor(R.color.status_idle))
        Toast.makeText(this, "Macro stopped.", Toast.LENGTH_SHORT).show()
    }

    private fun togglePause() {
        val orch = MacroAccessibilityService.orchestrator ?: return
        if (orch.isAnyRunning()) {
            orch.stopAll()
            btnPause.text = getString(R.string.btn_resume)
            updateStatus("PAUSED", getColor(R.color.status_paused))
        } else {
            orch.startAll()
            btnPause.text = getString(R.string.btn_pause)
            updateStatus("RUNNING", getColor(R.color.status_running))
        }
    }

    private fun updateStatus(text: String, color: Int) {
        statusText.text = text
        statusText.setTextColor(color)
    }

    private fun startUIUpdater() {
        handler.post(object : Runnable {
            override fun run() {
                runtimeText.text = "Runtime: ${StatsTracker.getRuntime()}"
                statPollen.text = StatsTracker.pollenCollected.get().toString()
                statQuests.text = StatsTracker.questsCompleted.get().toString()
                statMobs.text = StatsTracker.mobsDefeated.get().toString()
                statGestures.text = StatsTracker.gesturesPerformed.get().toString()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun checkAccessibilityService(): Boolean {
        return MacroAccessibilityService.instance != null
    }

    private fun showAccessibilityDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.perm_accessibility_title)
            .setMessage(R.string.perm_accessibility_msg)
            .setPositiveButton("Open Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AlertDialog.Builder(this)
                .setTitle(R.string.perm_overlay_title)
                .setMessage(R.string.perm_overlay_msg)
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
