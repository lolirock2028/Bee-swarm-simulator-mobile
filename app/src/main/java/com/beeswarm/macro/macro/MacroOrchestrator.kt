package com.beeswarm.macro.macro

import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.utils.GestureEngine
import com.beeswarm.macro.utils.Logger
import com.beeswarm.macro.utils.StatsTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MacroOrchestrator(engine: GestureEngine) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val farmMacro = AutoFarmMacro(engine)
    private val questMacro = AutoQuestMacro(engine)
    private val mobMacro = MobDefeatMacro(engine)
    private val convertMacro = AutoConvertMacro(engine)

    fun startAll() {
        Logger.i("Orchestrator: Starting all enabled macros")
        StatsTracker.start()

        if (MacroConfig.autoFarmEnabled) farmMacro.start(scope)
        if (MacroConfig.autoQuestEnabled) questMacro.start(scope)
        if (MacroConfig.mobDefeatEnabled) mobMacro.start(scope)
        if (MacroConfig.autoConvertEnabled) convertMacro.start(scope)
    }

    fun stopAll() {
        Logger.i("Orchestrator: Stopping all macros")
        farmMacro.stop()
        questMacro.stop()
        mobMacro.stop()
        convertMacro.stop()
        StatsTracker.stop()
    }

    fun isAnyRunning(): Boolean {
        return farmMacro.isRunning() || questMacro.isRunning() ||
               mobMacro.isRunning() || convertMacro.isRunning()
    }
}
