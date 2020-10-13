package com.davenotdavid.samplepuzzle

import android.content.Context
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.davenotdavid.samplepuzzle.GestureDetectGridView.OnSwipeListener
import kotlinx.android.synthetic.main.activity_sample_puzzle.*
import java.util.Random

enum class SwipeDirections {
    UP, DOWN, LEFT, RIGHT
}

class SamplePuzzleActivity : AppCompatActivity() {

    companion object {
        private const val TOTAL_COLUMNS = 3
        private const val DIMENSIONS = TOTAL_COLUMNS * TOTAL_COLUMNS

        private var boardColumnWidth = 0
        private var boardColumnHeight = 0
    }

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
        setContentView(R.layout.activity_sample_puzzle)

        init()
        scrambleTileBoard()
        setTileBoardDimensions()
    }

    private fun init() {
        gesture_detect_grid_view.apply {
            numColumns = TOTAL_COLUMNS
            setOnSwipeListener(object : OnSwipeListener {
                override fun onSwipe(direction: SwipeDirections, position: Int) {
                    moveTiles(direction, position)
                }
            })
        }

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
        val observer = gesture_detect_grid_view.viewTreeObserver
        observer.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                gesture_detect_grid_view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val displayWidth = gesture_detect_grid_view.measuredWidth
                val displayHeight = gesture_detect_grid_view.measuredHeight
                val statusbarHeight = getStatusBarHeight(applicationContext)
                val requiredHeight = displayHeight - statusbarHeight

                boardColumnWidth = displayWidth / TOTAL_COLUMNS
                boardColumnHeight = requiredHeight / TOTAL_COLUMNS

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

        gesture_detect_grid_view.adapter = TileImageAdapter(tileImages, boardColumnWidth, boardColumnHeight)
    }

    private fun displayToast(@StringRes textResId: Int) {
        Toast.makeText(this, getString(textResId), Toast.LENGTH_SHORT).show()
    }

    private fun moveTiles(direction: SwipeDirections, position: Int) {
        // Upper-left-corner tile
        if (position == 0) {
            when (direction) {
                SwipeDirections.RIGHT -> swapTile(position, 1)
                SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                else -> displayToast(R.string.invalid_move)
            }
        // Upper-center tiles
        } else if (position > 0 && position < TOTAL_COLUMNS - 1) {
            when (direction) {
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> displayToast(R.string.invalid_move)
            }
        // Upper-right-corner tile
        } else if (position == TOTAL_COLUMNS - 1) {
            when (direction) {
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                else -> displayToast(R.string.invalid_move)
            }
        // Left-side tiles
        } else if (position > TOTAL_COLUMNS - 1 && position < DIMENSIONS - TOTAL_COLUMNS && position % TOTAL_COLUMNS == 0) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                SwipeDirections.DOWN -> swapTile(position, TOTAL_COLUMNS)
                else -> displayToast(R.string.invalid_move)
            }
        // Right-side AND bottom-right-corner tiles
        } else if (position == TOTAL_COLUMNS * 2 - 1 || position == TOTAL_COLUMNS * 3 - 1) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.DOWN -> {
                    // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                    // right-corner tile.
                    if (position <= DIMENSIONS - TOTAL_COLUMNS - 1) {
                        swapTile(position, TOTAL_COLUMNS)
                    } else {
                        displayToast(R.string.invalid_move)
                    }
                }
                else -> displayToast(R.string.invalid_move)
            }
        // Bottom-left corner tile
        } else if (position == DIMENSIONS - TOTAL_COLUMNS) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> displayToast(R.string.invalid_move)
            }
        // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - TOTAL_COLUMNS) {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> displayToast(R.string.invalid_move)
            }
        // Center tiles
        } else {
            when (direction) {
                SwipeDirections.UP -> swapTile(position, -TOTAL_COLUMNS)
                SwipeDirections.LEFT -> swapTile(position, -1)
                SwipeDirections.RIGHT -> swapTile(position, 1)
                else -> swapTile(position, TOTAL_COLUMNS)
            }
        }
    }

    private fun swapTile(currentPosition: Int, swap: Int) {
        val newPosition = tileListIndexes[currentPosition + swap]
        tileListIndexes[currentPosition + swap] = tileListIndexes[currentPosition]
        tileListIndexes[currentPosition] = newPosition
        displayTileBoard()

        if (isSolved) {
            displayToast(R.string.winner)
        }
    }
}
