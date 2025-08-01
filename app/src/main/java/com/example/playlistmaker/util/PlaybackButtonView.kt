package com.example.playlistmaker.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.playlistmaker.R


class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val playImageBitmap: Bitmap?
    private val pauseImageBitmap: Bitmap?
    private var imageBitmap: Bitmap?

    private var imageRect = RectF(0f, 0f, 0f, 0f)

    private var isPlayingState: Boolean = false

    private val paint: Paint

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                playImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_playImageResId)?.toBitmap()
                pauseImageBitmap =
                    getDrawable(R.styleable.PlaybackButtonView_pauseImageResId)?.toBitmap()
                imageBitmap = playImageBitmap

                val tintColor =
                    getColor(
                        R.styleable.PlaybackButtonView_tintColorId,
                        context.getColor(R.color.black)
                    )

                val filter = PorterDuffColorFilter(
                    tintColor,
                    PorterDuff.Mode.SRC_IN
                )
                paint = Paint().apply { setColorFilter(filter) }
            } finally {
                recycle()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                changeCurState()
                performClick()
                return true
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        imageBitmap?.let {
            canvas.drawBitmap(imageBitmap!!, null, imageRect, paint)
        }
    }

    private fun changeCurState() {
        isPlayingState = !isPlayingState
        imageBitmap = if (isPlayingState) pauseImageBitmap else playImageBitmap
        invalidate()
    }

    fun setPlayingState(isPlaying: Boolean) {
        if (isPlayingState != isPlaying) changeCurState()
    }
}