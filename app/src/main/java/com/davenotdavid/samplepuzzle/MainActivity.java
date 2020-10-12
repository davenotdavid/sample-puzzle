package com.davenotdavid.samplepuzzle;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    enum SwipeDirections {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private GestureDetectGridView mGridView;

    private static final int COLUMNS = 3;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;

    private static int mColumnWidth, mColumnHeight;

    private int[] tileListIndexes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        scrambleTileBoard();
        setTileBoardDimensions();
    }

    private void init() {
        mGridView = (GestureDetectGridView) findViewById(R.id.gesture_detect_grid_view);
        mGridView.setNumColumns(COLUMNS);
        mGridView.setOnSwipeListener(new GestureDetectGridView.OnSwipeListener() {
            @Override public void onSwipe(SwipeDirections direction, int position) {
                moveTiles(direction, position);
            }
        });

        tileListIndexes = new int[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileListIndexes[i] = i;
        }
    }

    private void scrambleTileBoard() {
        int index;
        int tempIndex;
        Random random = new Random();

        for (int i = tileListIndexes.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            tempIndex = tileListIndexes[index];
            tileListIndexes[index] = tileListIndexes[i];
            tileListIndexes[i] = tempIndex;
        }
    }

    private void setTileBoardDimensions() {
        ViewTreeObserver observer = mGridView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getMeasuredHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = requiredHeight / COLUMNS;

                displayTileBoard();
            }
        });
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    /**
     * Used for both init and every time a new swap move is made by the user.
     */
    private void displayTileBoard() {
        ArrayList<ImageView> tileImages = new ArrayList<>();
        ImageView tileImage;

        for (int i : tileListIndexes) {
            tileImage = new ImageView(this);

            if (i == 0) tileImage.setBackgroundResource(R.drawable.pigeon_piece1);
            else if (i == 1) tileImage.setBackgroundResource(R.drawable.pigeon_piece2);
            else if (i == 2) tileImage.setBackgroundResource(R.drawable.pigeon_piece3);
            else if (i == 3) tileImage.setBackgroundResource(R.drawable.pigeon_piece4);
            else if (i == 4) tileImage.setBackgroundResource(R.drawable.pigeon_piece5);
            else if (i == 5) tileImage.setBackgroundResource(R.drawable.pigeon_piece6);
            else if (i == 6) tileImage.setBackgroundResource(R.drawable.pigeon_piece7);
            else if (i == 7) tileImage.setBackgroundResource(R.drawable.pigeon_piece8);
            else if (i == 8) tileImage.setBackgroundResource(R.drawable.pigeon_piece9);

            tileImages.add(tileImage);
        }

        mGridView.setAdapter(new TileImageAdapter(tileImages, mColumnWidth, mColumnHeight));
    }

    private void swap(int currentPosition, int swap) {
        int newPosition = tileListIndexes[currentPosition + swap];
        tileListIndexes[currentPosition + swap] = tileListIndexes[currentPosition];
        tileListIndexes[currentPosition] = newPosition;
        displayTileBoard();

        if (isSolved()) Toast.makeText(this, "YOU WIN!", Toast.LENGTH_SHORT).show();
    }

    private void moveTiles(SwipeDirections direction, int position) {
        // Upper-left-corner tile
        if (position == 0) {
            if (direction == SwipeDirections.RIGHT) swap(position, 1);
            else if (direction == SwipeDirections.DOWN) swap(position, COLUMNS);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction == SwipeDirections.LEFT) swap(position, -1);
            else if (direction == SwipeDirections.DOWN) swap(position, COLUMNS);
            else if (direction == SwipeDirections.RIGHT) swap(position, 1);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction == SwipeDirections.LEFT) swap(position, -1);
            else if (direction == SwipeDirections.DOWN) swap(position, COLUMNS);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction == SwipeDirections.UP) swap(position, -COLUMNS);
            else if (direction == SwipeDirections.RIGHT) swap(position, 1);
            else if (direction == SwipeDirections.DOWN) swap(position, COLUMNS);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction == SwipeDirections.UP) swap(position, -COLUMNS);
            else if (direction == SwipeDirections.LEFT) swap(position, -1);
            else if (direction == SwipeDirections.DOWN) {
                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(position, COLUMNS);
                else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction == SwipeDirections.UP) swap(position, -COLUMNS);
            else if (direction == SwipeDirections.RIGHT) swap(position, 1);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction == SwipeDirections.UP) swap(position, -COLUMNS);
            else if (direction == SwipeDirections.LEFT) swap(position, -1);
            else if (direction == SwipeDirections.RIGHT) swap(position, 1);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
        // Center tiles
        } else {
            if (direction == SwipeDirections.UP) swap(position, -COLUMNS);
            else if (direction == SwipeDirections.LEFT) swap(position, -1);
            else if (direction == SwipeDirections.RIGHT) swap(position, 1);
            else swap(position, COLUMNS);
        }
    }

    private boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileListIndexes.length; i++) {
            if (tileListIndexes[i] == i) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }
}
