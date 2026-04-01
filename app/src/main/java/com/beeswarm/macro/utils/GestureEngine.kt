package com.beeswarm.macro.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.*
import kotlin.random.Random

/**
 * Advanced gesture engine for dispatching complex touch patterns.
 * Supports multiple movement patterns optimized for Bee Swarm Simulator.
 */
class GestureEngine(private val service: AccessibilityService) {

    private var screenWidth = 1080
    private var screenHeight = 1920

    enum class Pattern {
        CIRCLE, SPIRAL, FIGURE_EIGHT, ZIGZAG, GRID, STAR, DIAMOND, RANDOM_WALK, CLOVER, LAWNMOWER
    }

    fun updateScreenSize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
    }

    /**
     * Dispatch a gesture and wait for completion.
     */
    suspend fun dispatchGesture(gesture: GestureDescription): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false
        return suspendCancellableCoroutine { cont ->
            val callback = object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    if (cont.isActive) cont.resume(true)
                }
                override fun onCancelled(gestureDescription: GestureDescription?) {
                    if (cont.isActive) cont.resume(false)
                }
            }
            service.dispatchGesture(gesture, callback, null)
        }
    }

    /**
     * Perform a single tap at coordinates.
     */
    suspend fun tap(x: Float, y: Float, duration: Long = 50L): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(gesture)
    }

    /**
     * Perform a swipe from one point to another.
     */
    suspend fun swipe(startX: Float, startY: Float, endX: Float, endY: Float, duration: Long = 300L): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(gesture)
    }

    /**
     * Execute a movement pattern centered at (cx, cy) with given radius.
     */
    suspend fun executePattern(
        pattern: Pattern,
        cx: Float = screenWidth / 2f,
        cy: Float = screenHeight / 2f,
        radius: Float = 200f,
        duration: Long = 2000L,
        randomize: Boolean = true
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return false

        val jitter = if (randomize) Random.nextFloat() * 20f - 10f else 0f
        val adjustedCx = cx + jitter
        val adjustedCy = cy + jitter

        val path = when (pattern) {
            Pattern.CIRCLE -> createCirclePath(adjustedCx, adjustedCy, radius)
            Pattern.SPIRAL -> createSpiralPath(adjustedCx, adjustedCy, radius)
            Pattern.FIGURE_EIGHT -> createFigureEightPath(adjustedCx, adjustedCy, radius)
            Pattern.ZIGZAG -> createZigzagPath(adjustedCx, adjustedCy, radius)
            Pattern.GRID -> createGridPath(adjustedCx, adjustedCy, radius)
            Pattern.STAR -> createStarPath(adjustedCx, adjustedCy, radius)
            Pattern.DIAMOND -> createDiamondPath(adjustedCx, adjustedCy, radius)
            Pattern.RANDOM_WALK -> createRandomWalkPath(adjustedCx, adjustedCy, radius)
            Pattern.CLOVER -> createCloverPath(adjustedCx, adjustedCy, radius)
            Pattern.LAWNMOWER -> createLawnmowerPath(adjustedCx, adjustedCy, radius)
        }

        val stroke = GestureDescription.StrokeDescription(path, 0, duration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(gesture)
    }

    private fun createCirclePath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        val steps = 36
        for (i in 0..steps) {
            val angle = 2.0 * Math.PI * i / steps
            val x = cx + r * cos(angle).toFloat()
            val y = cy + r * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        return path
    }

    private fun createSpiralPath(cx: Float, cy: Float, maxR: Float): Path {
        val path = Path()
        val steps = 72
        for (i in 0..steps) {
            val angle = 4.0 * Math.PI * i / steps
            val r = maxR * i / steps
            val x = cx + r * cos(angle).toFloat()
            val y = cy + r * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        return path
    }

    private fun createFigureEightPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        val steps = 48
        for (i in 0..steps) {
            val t = 2.0 * Math.PI * i / steps
            val x = cx + r * sin(t).toFloat()
            val y = cy + r * sin(2 * t).toFloat() / 2f
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        return path
    }

    private fun createZigzagPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        val rows = 6
        val amplitude = r
        path.moveTo(cx - amplitude, cy - r)
        for (i in 0 until rows) {
            val y = cy - r + (2 * r * i / rows)
            val nextY = cy - r + (2 * r * (i + 1) / rows)
            if (i % 2 == 0) {
                path.lineTo(cx + amplitude, nextY)
            } else {
                path.lineTo(cx - amplitude, nextY)
            }
        }
        return path
    }

    private fun createGridPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        val gridSize = 4
        val step = 2 * r / gridSize
        path.moveTo(cx - r, cy - r)
        for (row in 0..gridSize) {
            val y = cy - r + row * step
            if (row % 2 == 0) {
                path.lineTo(cx + r, y)
            } else {
                path.lineTo(cx - r, y)
            }
        }
        return path
    }

    private fun createStarPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        val points = 5
        val innerR = r * 0.4f
        for (i in 0 until points * 2) {
            val angle = Math.PI / 2 + Math.PI * i / points
            val currentR = if (i % 2 == 0) r else innerR
            val x = cx + currentR * cos(angle).toFloat()
            val y = cy - currentR * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }

    private fun createDiamondPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        path.moveTo(cx, cy - r)
        path.lineTo(cx + r, cy)
        path.lineTo(cx, cy + r)
        path.lineTo(cx - r, cy)
        path.close()
        return path
    }

    private fun createRandomWalkPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        var x = cx
        var y = cy
        path.moveTo(x, y)
        val steps = 20
        for (i in 0 until steps) {
            x += Random.nextFloat() * r / 3 - r / 6
            y += Random.nextFloat() * r / 3 - r / 6
            x = x.coerceIn(cx - r, cx + r)
            y = y.coerceIn(cy - r, cy + r)
            path.lineTo(x, y)
        }
        return path
    }

    private fun createCloverPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        val steps = 72
        for (i in 0..steps) {
            val t = 2.0 * Math.PI * i / steps
            val cloverR = r * abs(cos(2 * t)).toFloat()
            val x = cx + cloverR * cos(t).toFloat()
            val y = cy + cloverR * sin(t).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        return path
    }

    private fun createLawnmowerPath(cx: Float, cy: Float, r: Float): Path {
        val path = Path()
        val rows = 8
        val step = 2 * r / rows
        path.moveTo(cx - r, cy - r)
        for (row in 0 until rows) {
            val y = cy - r + row * step
            val nextY = y + step
            if (row % 2 == 0) {
                path.lineTo(cx + r, y)
                path.lineTo(cx + r, nextY)
            } else {
                path.lineTo(cx - r, y)
                path.lineTo(cx - r, nextY)
            }
        }
        return path
    }
}
