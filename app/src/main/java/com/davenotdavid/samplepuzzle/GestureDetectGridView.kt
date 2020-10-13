package com.davenotdavid.samplepuzzle

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.GridView
import kotlin.math.abs

class GestureDetectGridView : GridView {

    companion object {
        private const val SWIPE_MIN_DISTANCE = 100
        private const val SWIPE_MAX_OFF_PATH = 100
        private const val SWIPE_THRESHOLD_VELOCITY = 100
    }

    private var gestureDetector: GestureDetector? = null
    private var swipeListener: OnSwipeListener? = null
    private var flingConfirmed = false
    private var touchX = 0f
    private var touchY = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) // API 21
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        gestureDetector?.onTouchEvent(ev)
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            flingConfirmed = false
        } else if (action == MotionEvent.ACTION_DOWN) {
            touchX = ev.x
            touchY = ev.y
        } else {
            if (flingConfirmed) {
                return true
            }
            val dX = Math.abs(ev.x - touchX)
            val dY = Math.abs(ev.y - touchY)
            if (dX > SWIPE_MIN_DISTANCE || dY > SWIPE_MIN_DISTANCE) {
                flingConfirmed = true
                return true
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean = gestureDetector?.onTouchEvent(ev) ?: false

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener?) {
        swipeListener = onSwipeListener
    }

    interface OnSwipeListener {
        fun onSwipe(direction: SwipeDirections, position: Int)
    }

    private fun init(context: Context) {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean {
                return true
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val position = pointToPosition(Math.round(e1.x), Math.round(e1.y))
                if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
                    if (abs(e1.x - e2.x) > SWIPE_MAX_OFF_PATH || abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                        return false
                    }
                    if (e1.y - e2.y > SWIPE_MIN_DISTANCE) {
                        swipeListener?.onSwipe(SwipeDirections.UP, position)
                    } else if (e2.y - e1.y > SWIPE_MIN_DISTANCE) {
                        swipeListener?.onSwipe(SwipeDirections.DOWN, position)
                    }
                } else {
                    if (abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                        return false
                    }
                    if (e1.x - e2.x > SWIPE_MIN_DISTANCE) {
                        swipeListener?.onSwipe(SwipeDirections.LEFT, position)
                    } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE) {
                        swipeListener?.onSwipe(SwipeDirections.RIGHT, position)
                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY)
            }
        })
    }
}
