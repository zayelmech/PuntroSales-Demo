package com.imecatro.products.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class BarcodeBoxView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val boxPaint: Paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val scrimPaint: Paint = Paint().apply {
        color = Color.parseColor("#99000000") // Fondo semi-transparente
    }

    private val eraserPaint: Paint = Paint().apply {
        strokeWidth = boxPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var boxRect: RectF? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Define el tamaño y la posición del rectángulo
        val width = w * 0.8f // 80% del ancho de la vista
        val height = h * 0.3f // 30% del alto de la vista
        val left = (w - width) / 2
        val top = (h - height) / 2
        boxRect = RectF(left, top, left + width, top + height)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boxRect?.let {
            // Dibuja el fondo semi-transparente que cubre toda la pantalla
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)
            // "Corta" el área del rectángulo para hacerla transparente
            canvas.drawRoundRect(it, 16f, 16f, eraserPaint)
            // Dibuja el borde blanco alrededor del área transparente
            canvas.drawRoundRect(it, 16f, 16f, boxPaint)
        }
    }
}