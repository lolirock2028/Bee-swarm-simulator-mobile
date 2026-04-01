package com.beeswarm.macro.utils

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

object StatsTracker {
    val pollenCollected = AtomicLong(0)
    val questsCompleted = AtomicInteger(0)
    val mobsDefeated = AtomicInteger(0)
    val gesturesPerformed = AtomicLong(0)
    val conversions = AtomicInteger(0)
    var startTime: Long = 0L
    var isRunning: Boolean = false

    fun start() {
        startTime = System.currentTimeMillis()
        isRunning = true
    }

    fun stop() { isRunning = false }

    fun reset() {
        pollenCollected.set(0); questsCompleted.set(0)
        mobsDefeated.set(0); gesturesPerformed.set(0)
        conversions.set(0); startTime = 0L; isRunning = false
    }

    fun getRuntime(): String {
        if (startTime == 0L) return "00:00:00"
        val elapsed = System.currentTimeMillis() - startTime
        val h = elapsed / 3600000; val m = (elapsed % 3600000) / 60000; val s = (elapsed % 60000) / 1000
        return String.format("%02d:%02d:%02d", h, m, s)
    }

    fun addPollen(amount: Long) { pollenCollected.addAndGet(amount); gesturesPerformed.incrementAndGet() }
    fun addQuest() { questsCompleted.incrementAndGet() }
    fun addMob() { mobsDefeated.incrementAndGet(); gesturesPerformed.incrementAndGet() }
    fun addConversion() { conversions.incrementAndGet() }
}
