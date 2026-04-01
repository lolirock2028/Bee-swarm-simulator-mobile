package com.beeswarm.macro.macro

import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.utils.GestureEngine
import com.beeswarm.macro.utils.Logger
import com.beeswarm.macro.utils.StatsTracker
import kotlinx.coroutines.*
import kotlin.random.Random

class AutoFarmMacro(private val engine: GestureEngine) {

    private var job: Job? = null
    private val fields = listOf(
        Pair(540f, 800f),   // Sunflower field
        Pair(540f, 1200f),  // Mushroom field
        Pair(300f, 1000f),  // Dandelion field
        Pair(780f, 1000f),  // Blue flower field
        Pair(540f, 600f),   // Clover field
        Pair(540f, 1400f),  // Strawberry field
        Pair(200f, 700f),   // Spider field
        Pair(880f, 700f),   // Bamboo field
    )

    fun start(scope: CoroutineScope) {
        Logger.i("AutoFarm: Starting")
        job = scope.launch {
            var fieldIndex = 0
            while (isActive) {
                try {
                    val field = fields[fieldIndex % fields.size]
                    val pattern = MacroConfig.farmPattern
                    val radius = MacroConfig.farmRadius
                    val speed = MacroConfig.farmSpeed

                    Logger.d("AutoFarm: Farming field ${fieldIndex % fields.size} with $pattern")

                    // Navigate to field
                    engine.swipe(540f, 960f, field.first, field.second, 500L)
                    delay(300)

                    // Farm the field with pattern
                    repeat(5) {
                        engine.executePattern(
                            pattern = pattern,
                            cx = field.first,
                            cy = field.second,
                            radius = radius,
                            duration = speed,
                            randomize = MacroConfig.randomizeGestures
                        )
                        StatsTracker.addPollen(Random.nextLong(50, 200))

                        if (MacroConfig.antiDetectionDelay) {
                            delay(Random.nextLong(200, 800))
                        } else {
                            delay(MacroConfig.farmCycleDelay)
                        }
                    }

                    fieldIndex++
                    delay(MacroConfig.farmCycleDelay)

                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.e("AutoFarm: Error", e)
                    delay(2000)
                }
            }
        }
    }

    fun stop() {
        Logger.i("AutoFarm: Stopping")
        job?.cancel()
        job = null
    }

    fun isRunning(): Boolean = job?.isActive == true
}
