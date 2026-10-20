package com.practicum.playlistmaker3.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.practicum.playlistmaker3.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "PlaybackDebug"
        private const val STATE_PLAY = 0
        private const val STATE_PAUSE = 1
        private const val DEBOUNCE_DELAY = 300L
    }

    private var currentState = STATE_PLAY

    private var playBitmap: Bitmap? = null
    private var pauseBitmap: Bitmap? = null

    private val imageRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val handler = Handler(Looper.getMainLooper())
    private var isClickEnabled = true

    var onPlaybackClickListener: (() -> Unit)? = null

    init {
        Log.d(TAG, "PlaybackButtonView init")
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlaybackButtonView)
            try {
                val playResId = typedArray.getResourceId(R.styleable.PlaybackButtonView_playIcon, 0)
                val pauseResId = typedArray.getResourceId(R.styleable.PlaybackButtonView_pauseIcon, 0)

                if (playResId != 0) {
                    playBitmap = getBitmapFromDrawable(playResId)
                    Log.d(TAG, "playBitmap загружен")
                }
                if (pauseResId != 0) {
                    pauseBitmap = getBitmapFromDrawable(pauseResId)
                    Log.d(TAG, "pauseBitmap загружен")
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    private fun getBitmapFromDrawable(resId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, resId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect.set(0f, 0f, w.toFloat(), h.toFloat())
        Log.d(TAG, "onSizeChanged: $w x $h, imageRect = $imageRect")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmap = if (currentState == STATE_PLAY) playBitmap else pauseBitmap
        bitmap?.let {
            canvas.drawBitmap(it, null, imageRect, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent: action = ${event.action}, currentState = $currentState, isClickEnabled = $isClickEnabled")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "ACTION_DOWN")
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (!isClickEnabled) {
                    Log.d(TAG, "ACTION_UP: клик заблокирован (debounce)")
                    return true
                }
                isClickEnabled = false
                handler.postDelayed({ isClickEnabled = true }, DEBOUNCE_DELAY)

                currentState = if (currentState == STATE_PLAY) STATE_PAUSE else STATE_PLAY
                Log.d(TAG, "ACTION_UP: переключаем состояние на $currentState")
                invalidate()

                Log.d(TAG, "ACTION_UP: вызываем onPlaybackClickListener")
                onPlaybackClickListener?.invoke()
                Log.d(TAG, "ACTION_UP: onPlaybackClickListener вызван")
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setPlaying(isPlaying: Boolean) {
        val newState = if (isPlaying) STATE_PAUSE else STATE_PLAY
        Log.d(TAG, "setPlaying($isPlaying) -> newState = $newState, currentState = $currentState")
        if (currentState != newState) {
            currentState = newState
            invalidate()
            Log.d(TAG, "setPlaying: состояние изменено на $currentState")
        } else {
            Log.d(TAG, "setPlaying: состояние не изменилось (уже $currentState)")
        }
    }
}