package com.beeswarm.macro.config

import android.content.Context
import android.content.SharedPreferences
import com.beeswarm.macro.utils.GestureEngine

object MacroConfig {
    private const val PREFS_NAME = "bee_swarm_macro_prefs"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var autoFarmEnabled: Boolean
        get() = prefs.getBoolean("auto_farm_enabled", true)
        set(value) = prefs.edit().putBoolean("auto_farm_enabled", value).apply()

    var autoQuestEnabled: Boolean
        get() = prefs.getBoolean("auto_quest_enabled", true)
        set(value) = prefs.edit().putBoolean("auto_quest_enabled", value).apply()

    var mobDefeatEnabled: Boolean
        get() = prefs.getBoolean("mob_defeat_enabled", true)
        set(value) = prefs.edit().putBoolean("mob_defeat_enabled", value).apply()

    var autoConvertEnabled: Boolean
        get() = prefs.getBoolean("auto_convert_enabled", true)
        set(value) = prefs.edit().putBoolean("auto_convert_enabled", value).apply()

    var farmPattern: GestureEngine.Pattern
        get() = GestureEngine.Pattern.valueOf(prefs.getString("farm_pattern", "SPIRAL") ?: "SPIRAL")
        set(value) = prefs.edit().putString("farm_pattern", value.name).apply()

    var farmRadius: Float
        get() = prefs.getFloat("farm_radius", 200f)
        set(value) = prefs.edit().putFloat("farm_radius", value).apply()

    var farmSpeed: Long
        get() = prefs.getLong("farm_speed", 2000L)
        set(value) = prefs.edit().putLong("farm_speed", value).apply()

    var farmCycleDelay: Long
        get() = prefs.getLong("farm_cycle_delay", 500L)
        set(value) = prefs.edit().putLong("farm_cycle_delay", value).apply()

    var questCheckInterval: Long
        get() = prefs.getLong("quest_check_interval", 30000L)
        set(value) = prefs.edit().putLong("quest_check_interval", value).apply()

    var combatPattern: GestureEngine.Pattern
        get() = GestureEngine.Pattern.valueOf(prefs.getString("combat_pattern", "STAR") ?: "STAR")
        set(value) = prefs.edit().putString("combat_pattern", value.name).apply()

    var combatRadius: Float
        get() = prefs.getFloat("combat_radius", 150f)
        set(value) = prefs.edit().putFloat("combat_radius", value).apply()

    var combatSpeed: Long
        get() = prefs.getLong("combat_speed", 1500L)
        set(value) = prefs.edit().putLong("combat_speed", value).apply()

    var convertInterval: Long
        get() = prefs.getLong("convert_interval", 120000L)
        set(value) = prefs.edit().putLong("convert_interval", value).apply()

    var randomizeGestures: Boolean
        get() = prefs.getBoolean("randomize_gestures", true)
        set(value) = prefs.edit().putBoolean("randomize_gestures", value).apply()

    var showOverlay: Boolean
        get() = prefs.getBoolean("show_overlay", true)
        set(value) = prefs.edit().putBoolean("show_overlay", value).apply()

    var antiDetectionDelay: Boolean
        get() = prefs.getBoolean("anti_detection_delay", true)
        set(value) = prefs.edit().putBoolean("anti_detection_delay", value).apply()
}
