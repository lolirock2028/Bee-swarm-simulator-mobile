package com.beeswarm.macro.macro

import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.utils.GestureEngine
import com.beeswarm.macro.utils.Logger
import com.beeswarm.macro.utils.StatsTracker
import kotlinx.coroutines.*
import kotlin.random.Random

class MobDefeatMacro(private val engine: GestureEngine) {

    private var job: Job? = null

    // Mob spawn zones
    private val mobZones = listOf(
        Pair(300f, 900f),   // Ladybug zone
        Pair(780f, 900f),   // Rhino beetle zone
        Pair(540f, 700f),   // Spider zone
        Pair(200f, 1100f),  // Mantis zone
        Pair(880f, 1100f),  // Scorpion zone
        Pair(540f, 500f),   // Werewolf zone
    )

    fun start(scope: CoroutineScope) {
        Logger.i("MobDefeat: Starting")
        job = scope.launch {
            while (isActive) {
                try {
                    for (zone in mobZones) {
                        if (!isActive) break

                        Logger.d("MobDefeat: Checking zone at ${zone.first}, ${zone.second}")

                        // Navigate to mob zone
                        engine.swipe(540f, 960f, zone.first, zone.second, 400L)
                        delay(300)

                        // Attack pattern - rapid taps and combat gestures
                        repeat(3) {
                            // Rapid tap attack
                            engine.tap(zone.first, zone.second, 50L)
                            delay(100)
                            engine.tap(zone.first + 30f, zone.second - 30f, 50L)
                            delay(100)
                            engine.tap(zone.first - 30f, zone.second + 30f, 50L)
                            delay(100)
                        }

                        // Execute combat pattern
                        engine.executePattern(
                            pattern = MacroConfig.combatPattern,
                            cx = zone.first,
                            cy = zone.second,
                            radius = MacroConfig.combatRadius,
                            duration = MacroConfig.combatSpeed,
                            randomize = MacroConfig.randomizeGestures
                        )

                        StatsTracker.addMob()

                        if (MacroConfig.antiDetectionDelay) {
                            delay(Random.nextLong(300, 1000))
                        } else {
                            delay(500)
                        }
                    }

                    delay(5000) // Wait between mob sweeps

                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.e("MobDefeat: Error", e)
                    delay(2000)
                }
            }
        }
    }

    fun stop() {
        Logger.i("MobDefeat: Stopping")
        job?.cancel()
        job = null
    }

    fun isRunning(): Boolean = job?.isActive == true
}
