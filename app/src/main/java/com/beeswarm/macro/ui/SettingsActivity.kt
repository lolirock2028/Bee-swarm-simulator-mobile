package com.beeswarm.macro.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beeswarm.macro.R
import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.utils.GestureEngine
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = "Settings"
        toolbar.setNavigationOnClickListener { finish() }
    }
}
