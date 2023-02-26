package com.vicky7230.tasker.ui.home

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class UnderlayButton(
    private val context: Context,
    private val text: String,
    private val textSize: Int,
    private val imageResId: Int,
    private val color: Int,
    private val listener: UnderlayButtonClickListener
) {
    private var pos: Int = 0
    private var clickRegion: RectF? = null

    fun onclick(x: Float, y: Float): Boolean {
        if (clickRegion != null && clickRegion!!.contains(x, y)) {
            listener.onClick(pos)
            return true
        }
        return false
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
        val p = Paint()
        p.color = color
        c.drawRect(rectF, p)

        p.color = Color.WHITE
        p.textSize = textSize.toFloat()

        val r = Rect()
        val cHeight = rectF.height()
        val cWidth = rectF.width()
        p.textAlign = Paint.Align.LEFT
        p.getTextBounds(text, 0, text.length, r)

        val x: Float
        val y: Float
        if (imageResId == 0) {
            x = cWidth / 2f - r.width() / 2f - r.left.toFloat()
            y = cHeight / 2f + r.height() / 2f - r.bottom.toFloat()
            c.drawText(text, rectF.left + x, rectF.top + y, p)
        } else {
            val d = ContextCompat.getDrawable(context, imageResId)!!
            val bitmap = drawableToBitmap(d)
            c.drawBitmap(
                bitmap,
                rectF.left + (rectF.width() / 2) - (bitmap.width / 2),
                rectF.top + (rectF.height() / 2) - (bitmap.height / 2),
                p
            )
        }

        clickRegion = rectF
        this.pos = pos
    }

    private fun drawableToBitmap(d: Drawable): Bitmap {
        if (d is BitmapDrawable) return d.bitmap
        val bitmap =
            Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }
}
