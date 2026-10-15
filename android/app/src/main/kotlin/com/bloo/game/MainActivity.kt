package com.bloo.game

import android.app.Activity
import android.os.Bundle
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import kotlin.math.hypot

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(18, 32, 65)
        window.navigationBarColor = Color.rgb(18, 32, 65)
        setContentView(GameView(this))
    }
}

class GameView(context: Context) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private var started = false
    private var won = false
    private var lastTime = 0L
    private var bx = 0f
    private var by = 0f
    private var vx = 0f
    private var vy = 0f
    private var downX = 0f
    private var downY = 0f

    override fun onDraw(c: Canvas) {
        super.onDraw(c)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        if (bx == 0f) {
            bx = w * .30f
            by = h * .38f
        }

        c.drawColor(Color.rgb(18, 32, 65))

        p.color = Color.rgb(37, 59, 101)
        c.drawCircle(w * .12f, h * .22f, w * .17f, p)
        c.drawCircle(w * .88f, h * .28f, w * .20f, p)

        p.color = Color.WHITE
        p.textAlign = Paint.Align.CENTER
        p.typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        p.textSize = w * .085f
        c.drawText("ROPE BLOO", w / 2f, h * .09f, p)
        p.typeface = Typeface.DEFAULT
        p.textSize = w * .038f
        c.drawText(
            if (won) "LEVEL COMPLETE!" else "SWIPE TO CUT THE ROPE",
            w / 2f, h * .14f, p
        )

        val ax = w * .30f
        val ay = h * .20f
        if (!started) {
            p.color = Color.rgb(240, 190, 95)
            p.strokeWidth = w * .018f
            c.drawLine(ax, ay, bx, by, p)
        }

        val dx = w * .68f
        val dy = h * .70f
        val diamond = Path().apply {
            moveTo(dx, dy - w * .07f)
            lineTo(dx + w * .055f, dy)
            lineTo(dx, dy + w * .075f)
            lineTo(dx - w * .055f, dy)
            close()
        }
        p.color = if (won) Color.GREEN else Color.rgb(65, 235, 255)
        c.drawPath(diamond, p)

        p.color = Color.rgb(40, 155, 255)
        c.drawCircle(bx, by, w * .075f, p)
        p.color = Color.WHITE
        c.drawCircle(bx - w * .02f, by - w * .02f, w * .015f, p)
        c.drawCircle(bx + w * .02f, by - w * .02f, w * .015f, p)
        p.color = Color.rgb(15, 45, 90)
        c.drawCircle(bx - w * .018f, by - w * .02f, w * .006f, p)
        c.drawCircle(bx + w * .022f, by - w * .02f, w * .006f, p)

        p.color = Color.WHITE
        p.textSize = w * .04f
        c.drawText(
            if (won) "Tap to replay"
            else if (started) "BLOO is moving!"
            else "Swipe anywhere to begin",
            w / 2f, h * .88f, p
        )

        if (started && !won) {
            val now = System.nanoTime()
            val dt = if (lastTime == 0L) 0f
                else ((now - lastTime) / 1e9f).coerceAtMost(.04f)
            lastTime = now
            bx += vx * dt
            by += vy * dt
            vy += h * 1.15f * dt

            if (hypot(bx - dx, by - dy) < w * .12f) won = true

            if (by > h * .96f || bx > w * 1.1f) {
                if (!won) {
                    started = false
                    lastTime = 0L
                    bx = w * .30f
                    by = h * .38f
                    vx = 0f
                    vy = 0f
                }
            }
            invalidate()
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.x
                downY = e.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (won) {
                    won = false
                    started = false
                    bx = width * .30f
                    by = height * .38f
                    vx = 0f
                    vy = 0f
                    lastTime = 0L
                    invalidate()
                    return true
                }
                if (!started && hypot(e.x - downX, e.y - downY) > 20f) {
                    started = true
                    vx = width * .50f
                    vy = 0f
                    lastTime = System.nanoTime()
                    invalidate()
                }
                return true
            }
        }
        return true
    }
}
