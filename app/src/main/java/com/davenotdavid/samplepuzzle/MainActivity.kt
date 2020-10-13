package com.davenotdavid.samplepuzzle

import android.content.Context
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.davenotdavid.samplepuzzle.GestureDetectGridView.OnSwipeListener
import java.util.*

enum class SwipeDirections {
    UP, DOWN, LEFT, RIGHT
}

class MainActivity : AppCompatActivity() {

    companion object {
        private const val COLUMNS = 3
        private const val DIMENSIONS = COLUMNS * COLUMNS

        private var boardColumnWidth = 0
        private var boardColumnHeight = 0
    }

    private lateinit var gestureDetectGridView: GestureDetectGridView
    private val tileListIndexes = mutableListOf<Int>()

    private val isSolved: Boolean
        get() {
            var solved = false
            for (i in tileListIndexes.indices) {
                if (tileListIndexes[i] == i) {
                    solved = true
                } else {
                    solved = false
                    break
                }
            }

            return solved
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        scrambleTileBoard()
        setTileBoardDimensions()
    }

    private fun init() {
        gestureDetectGridView = findViewById(R.id.gesture_detect_grid_view)
        gestureDetectGridView.numColumns = COLUMNS
        gestureDetectGridView.setOnSwipeListener(object : OnSwipeListener {
            override fun onSwipe(direction: SwipeDirections, position: Int) {
                moveTiles(direction, position)
            }
        })

        tileListIndexes += 0 until DIMENSIONS
    }

    private fun scrambleTileBoard() {
        var index: Int
        var tempIndex: Int
        val random = Random()

        for (i in tileListIndexes.size - 1 downTo 1) {
            index = random.nextInt(i + 1)
            tempIndex = tileListIndexes[index]
            tileListIndexes[index] = tileListIndexes[i]
            tileListIndexes[i] = tempIndex
        }
    }

    private fun setTileBoardDimensions() {
        val observer = gestureDetectGridView.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                gestureDetectGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val displayWidth = gestureDetectGridView.measuredWidth
                val displayHeight = gestureDetectGridView.measuredHeight
                val statusbarHeight = getStatusBarHeight(applicationContext)
                val requiredHeight = displayHeight - statusbarHeight

                boardColumnWidth = displayWidth / COLUMNS
                boardColumnHeight = requiredHeight / COLUMNS

                displayTileBoard()
            }
        })
    }

    private fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }

        return result
    }

    /**
     * Used for both init and every time a new swap move is made by the user.
     */
    private fun displayTileBoard() {
        val tileImages = mutableListOf<ImageView>()
        var tileImage: ImageView

        tileListIndexes.forEach { i ->
            tileImage = ImageView(this)

            when (i) {
                0 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece1)
                1 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece2)
                2 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece3)
                3 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece4)
                4 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece5)
                5 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece6)
                6 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece7)
                7 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece8)
                8 -> tileImage.setBackgroundResource(R.drawable.pigeon_piece9)
            }

            tileImages.add(tileImage)
        }

        gestureDetectGridView.adapter = TileImageAdapter(tileImages, boardColumnWidth, boardColumnHeight)
    }

    private fun swap(currentPosition: Int, swap: Int) {
        val newPosition = tileListIndexes[currentPosition + swap]
        tileListIndexes[currentPosition + swap] = tileListIndexes[currentPosition]
        tileListIndexes[currentPosition] = newPosition
        displayTileBoard()

        if (isSolved) Toast.makeText(this, "YOU WIN!", Toast.LENGTH_SHORT).show()
    }

    private fun moveTiles(direction: SwipeDirections, position: Int) {
        // Upper-left-corner tile
        if (position == 0) {
            when (direction) {
                SwipeDirections.RIGHT -> swap(position, 1)
                SwipeDirections.DOWN -> swap(position, COLUMNS)
                else -> Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            }
        // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            when (direction) {
                SwipeDirections.LEFT -> swap(position, -1)
                SwipeDirections.DOWN -> swap(position, COLUMNS)
                SwipeDirections.RIGHT -> swap(position, 1)
                else -> Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            }
        // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            when (direction) {
                SwipeDirections.LEFT -> swap(position, -1)
                SwipeDirections.DOWN -> swap(position, COLUMNS)
                else -> Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            }
        // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS && position % COLUMNS == 0) {
            when (direction) {
                SwipeDirections.UP -> swap(position, -COLUMNS)
                SwipeDirections.RIGHT -> swap(position, 1)
                SwipeDirections.DOWN -> swap(position, COLUMNS)
                else -> Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            }
        // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            when (direction) {
                SwipeDirections.UP -> swap(position, -COLUMNS)
                SwipeDirections.LEFT -> swap(position, -1)
                SwipeDirections.DOWN -> {
                    // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                    // right-corner tile.
                    if (position <= DIMENSIONS - COLUMNS - 1) {
                        swap(position, COLUMNS)
                    } else {
                        Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            }
        // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            when (direction) {
                SwipeDirections.UP -> swap(position, -COLUMNS)
                SwipeDirections.RIGHT -> swap(position, 1)
                else -> Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            }
        // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            when (direction) {
                SwipeDirections.UP -> swap(position, -COLUMNS)
                SwipeDirections.LEFT -> swap(position, -1)
                SwipeDirections.RIGHT -> swap(position, 1)
                else -> Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            }
        // Center tiles
        } else {
            when (direction) {
                SwipeDirections.UP -> swap(position, -COLUMNS)
                SwipeDirections.LEFT -> swap(position, -1)
                SwipeDirections.RIGHT -> swap(position, 1)
                else -> swap(position, COLUMNS)
            }
        }
    }
}
