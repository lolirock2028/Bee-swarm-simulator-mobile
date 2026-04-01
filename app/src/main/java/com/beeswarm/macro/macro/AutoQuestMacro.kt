package com.beeswarm.macro.macro

import com.beeswarm.macro.config.MacroConfig
import com.beeswarm.macro.utils.GestureEngine
import com.beeswarm.macro.utils.Logger
import com.beeswarm.macro.utils.StatsTracker
import kotlinx.coroutines.*
import kotlin.random.Random

class AutoQuestMacro(private val engine: GestureEngine) {

    private var job: Job? = null

    // NPC locations (approximate screen coordinates)
    private val npcLocations = listOf(
        Triple("Black Bear", 540f, 400f),
        Triple("Brown Bear", 200f, 500f),
        Triple("Polar Bear", 880f, 500f),
        Triple("Science Bear", 540f, 300f),
        Triple("Mother Bear", 300f, 300f),
        Triple("Panda Bear", 780f, 300f),
    )

    fun start(scope: CoroutineScope) {
        Logger.i("AutoQuest: Starting")
        job = scope.launch {
            while (isActive) {
                try {
                    for (npc in npcLocations) {
                        if (!isActive) break

                        Logger.d("AutoQuest: Visiting ${npc.first}")

                        // Navigate to NPC
                        engine.swipe(540f, 960f, npc.second, npc.third, 600L)
                        delay(500)

                        // Tap NPC to interact
                        engine.tap(npc.second, npc.third, 100L)
                        delay(800)

                        // Tap dialog/accept button (center-bottom area)
                        engine.tap(540f, 1600f, 100L)
                        delay(500)

                        // Tap again to confirm
                        engine.tap(540f, 1600f, 100L)
                        delay(300)

                        // Close dialog
                        engine.tap(540f, 200f, 100L)
                        delay(300)

                        StatsTracker.addQuest()

                        if (MacroConfig.antiDetectionDelay) {
                            delay(Random.nextLong(500, 1500))
                        }
                    }

                    // Wait before next quest cycle
                    delay(MacroConfig.questCheckInterval)

                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.e("AutoQuest: Error", e)
                    delay(3000)
                }
            }
        }
    }

    fun stop() {
        Logger.i("AutoQuest: Stopping")
        job?.cancel()
        job = null
    }

    fun isRunning(): Boolean = job?.isActive == true
}
