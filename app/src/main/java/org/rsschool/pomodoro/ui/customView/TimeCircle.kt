package org.rsschool.pomodoro.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import org.rsschool.pomodoro.R

class TimeCircle @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var periodMs = 0L
    private var currentMs = 0L
    private var color = 0
    private val paint = Paint()

    init {
        if (attrs != null) {
            val styledAttrs = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                defStyleAttr,
                0
            )

            color = styledAttrs.getColor(R.styleable.CustomView_color, Color.RED)
            styledAttrs.recycle()
        }
        paint.color = color
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 5F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (periodMs == 0L || currentMs == 0L) return

        val startAngel = (((currentMs % periodMs).toFloat() / periodMs) * 360)

        canvas.drawArc(
            0F,
            0F,
            width.toFloat(),
            height.toFloat(),
            -90F,
            startAngel,
            true,
            paint
        )

    }

    fun setCurrent(current: Long) {
        currentMs = current
        invalidate()
    }

    fun setPeriod(period: Long) {
        periodMs = period
    }
}
