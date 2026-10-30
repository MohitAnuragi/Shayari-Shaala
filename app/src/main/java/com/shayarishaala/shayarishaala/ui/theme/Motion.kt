package com.shayarishaala.shayarishaala.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

/** Shared motion tokens so every screen moves the same way. */
object Motion {
    const val SHORT = 180
    const val MEDIUM = 320
    const val LONG = 500

    /** Things arriving: fast start, gentle landing. */
    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    /** Things leaving: gentle start, quick exit. */
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    fun <T> pressSpring() = spring<T>(
        dampingRatio = 0.6f,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> bouncySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
}
