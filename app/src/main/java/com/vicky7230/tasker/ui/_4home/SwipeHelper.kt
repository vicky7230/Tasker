package com.vicky7230.tasker.ui._4home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class SwipeHelper(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private var buttonWidth: Int,
    private val rightSwipeListener: RightSwipeListener
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private var buttonList: MutableList<UnderlayButton>? = null
    private lateinit var gestureDetector: GestureDetector
    var swipePosition = -1
    var swipeThreshold = 0.5f
    private val buttonBuffer: MutableMap<Int, MutableList<UnderlayButton>>
    lateinit var removerQueue: LinkedList<Int>
    private var swipedBack = true

    abstract fun instantiateMyButton(
        viewHolder: RecyclerView.ViewHolder,
        buffer: MutableList<UnderlayButton>
    )

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            for (button in buttonList!!) {
                if (!swipedBack)
                    if (button.onclick(e.x, e.y))
                        break
            }
            return true
        }
    }

    private val onTouchListener = View.OnTouchListener { view: View, motionEvent: MotionEvent ->
        if (swipePosition < 0) return@OnTouchListener false

        val point = Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
        val swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition)
        val swipedItem = swipeViewHolder?.itemView
        val rect = Rect()
        swipedItem?.getGlobalVisibleRect(rect)

        if (motionEvent.action == MotionEvent.ACTION_DOWN ||
            motionEvent.action == MotionEvent.ACTION_MOVE ||
            motionEvent.action == MotionEvent.ACTION_UP
        ) {
            if (rect.top < point.y && rect.bottom > point.y)
                gestureDetector.onTouchEvent(motionEvent)
            else {
                removerQueue.add(swipePosition)
                swipePosition = -1
                recoverSwipeItem()
            }
        }
        return@OnTouchListener false
    }

    @Synchronized
    private fun recoverSwipeItem() {
        while (!removerQueue.isEmpty()) {
            val pos = removerQueue.poll()!!.toInt()
            if (pos > -1)
                recyclerView.adapter?.notifyItemChanged(pos)
        }
    }

    init {
        this.buttonList = ArrayList()
        this.gestureDetector = GestureDetector(context, gestureListener)
        this.recyclerView.setOnTouchListener(onTouchListener)
        this.buttonBuffer = HashMap()
        this.removerQueue = IntLinkedList()

        attachSwipe()
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    class IntLinkedList : LinkedList<Int>() {
        override fun contains(element: Int): Boolean {
            return false
        }

        override fun lastIndexOf(element: Int): Int {
            return element
        }

        override fun remove(element: Int): Boolean {
            return false
        }

        override fun indexOf(element: Int): Int {
            return element
        }

        override fun add(element: Int): Boolean {
            return if (contains(element))
                false
            else
                super.add(element)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            val pos = viewHolder.adapterPosition
            if (swipePosition != pos) {
                removerQueue.add(swipePosition)
            }
            swipePosition = pos
            if (buttonBuffer.containsKey(swipePosition))
                buttonList = buttonBuffer[swipePosition]
            else
                buttonList?.clear()
            buttonBuffer.clear()
            swipeThreshold = 0.5f * buttonList!!.size.toFloat() * buttonWidth.toFloat()
            recoverSwipeItem()
        } else {
            rightSwipeListener.onRightSwiped(viewHolder)
            //recyclerView.adapter?.notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        var itemView = viewHolder.itemView
        if (pos < 0) {
            swipePosition = pos
            return
        }

        swipedBack = dX == 0f

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {

                var buffer: MutableList<UnderlayButton> = ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX = dX * buffer.size.toFloat() * buttonWidth.toFloat() / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            } else {
                if (dX >= context.resources.getDimension(R.dimen.underlay_button_width)) {
                    translationX = context.resources.getDimension(R.dimen.underlay_button_width)
                }
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawButton(
        c: Canvas,
        itemView: View,
        buffer: MutableList<UnderlayButton>,
        pos: Int,
        translationX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * translationX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(left, itemView.top.toFloat(), right, itemView.bottom.toFloat()),
                pos
            )
            right = left
        }
    }
}