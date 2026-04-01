package com.beeswarm.macro.macro

import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.utils.GestureEngine
import com.beeswarm.macro.utils.Logger
import com.beeswarm.macro.utils.StatsTracker
import kotlinx.coroutines.*

class AutoConvertMacro(private val engine: GestureEngine) {

    private var job: Job? = null

    // Hive location (center of screen, upper area)
    private val hiveX = 540f
    private val hiveY = 400f

    fun start(scope: CoroutineScope) {
        Logger.i("AutoConvert: Starting")
        job = scope.launch {
            while (isActive) {
                try {
                    delay(MacroConfig.convertInterval)

                    Logger.d("AutoConvert: Navigating to hive")

                    // Navigate to hive
                    engine.swipe(540f, 960f, hiveX, hiveY, 800L)
                    delay(1000)

                    // Tap hive to start conversion
                    engine.tap(hiveX, hiveY, 200L)
                    delay(500)

                    // Wait for conversion animation
                    delay(5000)

                    StatsTracker.addConversion()
                    Logger.d("AutoConvert: Conversion complete")

                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.e("AutoConvert: Error", e)
                    delay(5000)
                }
            }
        }
    }

    fun stop() {
        Logger.i("AutoConvert: Stopping")
        job?.cancel()
        job = null
    }

    fun isRunning(): Boolean = job?.isActive == true
}
