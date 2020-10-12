package com.davenotdavid.samplepuzzle;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GestureDetectGridView mGridView;

    private static final int COLUMNS = 3;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;

    private static int mColumnWidth, mColumnHeight;

    public static final String up = "up";
    public static final String down = "down";
    public static final String left = "left";
    public static final String right = "right";

    private String[] tileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        scramble();

        setDimensions();
    }

    private void init() {
        mGridView = (GestureDetectGridView) findViewById(R.id.grid);
        mGridView.setNumColumns(COLUMNS);
        mGridView.setOnSwipeListener(new GestureDetectGridView.OnSwipeListener() {
            @Override public void onSwipe(String direction, int position) {
                moveTiles(direction, position);
            }
        });

        tileList = new String[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            tileList[i] = String.valueOf(i);
        }
    }

    private void scramble() {
        int index;
        String temp;
        Random random = new Random();

        for (int i = tileList.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    private void setDimensions() {
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getMeasuredHeight();

                int statusbarHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusbarHeight;

                mColumnWidth = displayWidth / COLUMNS;
                mColumnHeight = requiredHeight / COLUMNS;

                display();
            }
        });
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    private void display() {
        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        for (int i = 0; i < tileList.length; i++) {
            button = new Button(this);

            if (tileList[i].equals("0"))
                button.setBackgroundResource(R.drawable.pigeon_piece1);
            else if (tileList[i].equals("1"))
                button.setBackgroundResource(R.drawable.pigeon_piece2);
            else if (tileList[i].equals("2"))
                button.setBackgroundResource(R.drawable.pigeon_piece3);
            else if (tileList[i].equals("3"))
                button.setBackgroundResource(R.drawable.pigeon_piece4);
            else if (tileList[i].equals("4"))
                button.setBackgroundResource(R.drawable.pigeon_piece5);
            else if (tileList[i].equals("5"))
                button.setBackgroundResource(R.drawable.pigeon_piece6);
            else if (tileList[i].equals("6"))
                button.setBackgroundResource(R.drawable.pigeon_piece7);
            else if (tileList[i].equals("7"))
                button.setBackgroundResource(R.drawable.pigeon_piece8);
            else if (tileList[i].equals("8"))
                button.setBackgroundResource(R.drawable.pigeon_piece9);

            buttons.add(button);
        }

        mGridView.setAdapter(new CustomAdapter(buttons, mColumnWidth, mColumnHeight));
    }

    private void swap(int currentPosition, int swap) {
        String newPosition = tileList[currentPosition + swap];
        tileList[currentPosition + swap] = tileList[currentPosition];
        tileList[currentPosition] = newPosition;
        display();

        if (isSolved()) Toast.makeText(this, "YOU WIN!", Toast.LENGTH_SHORT).show();
    }

    private void moveTiles(String direction, int position) {

        // Upper-left-corner tile
        if (position == 0) {
            if (direction.equals(right)) swap(position, 1);
            else if (direction.equals(down)) swap(position, COLUMNS);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();

        // Upper-center tiles
        } else if (position > 0 && position < COLUMNS - 1) {
            if (direction.equals(left)) swap(position, -1);
            else if (direction.equals(down)) swap(position, COLUMNS);
            else if (direction.equals(right)) swap(position, 1);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();

        // Upper-right-corner tile
        } else if (position == COLUMNS - 1) {
            if (direction.equals(left)) swap(position, -1);
            else if (direction.equals(down)) swap(position, COLUMNS);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();

        // Left-side tiles
        } else if (position > COLUMNS - 1 && position < DIMENSIONS - COLUMNS &&
                position % COLUMNS == 0) {
            if (direction.equals(up)) swap(position, -COLUMNS);
            else if (direction.equals(right)) swap(position, 1);
            else if (direction.equals(down)) swap(position, COLUMNS);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();

        // Right-side AND bottom-right-corner tiles
        } else if (position == COLUMNS * 2 - 1 || position == COLUMNS * 3 - 1) {
            if (direction.equals(up)) swap(position, -COLUMNS);
            else if (direction.equals(left)) swap(position, -1);
            else if (direction.equals(down)) {

                // Tolerates only the right-side tiles to swap downwards as opposed to the bottom-
                // right-corner tile.
                if (position <= DIMENSIONS - COLUMNS - 1) swap(position,
                        COLUMNS);
                else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();

        // Bottom-left corner tile
        } else if (position == DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(position, -COLUMNS);
            else if (direction.equals(right)) swap(position, 1);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();

        // Bottom-center tiles
        } else if (position < DIMENSIONS - 1 && position > DIMENSIONS - COLUMNS) {
            if (direction.equals(up)) swap(position, -COLUMNS);
            else if (direction.equals(left)) swap(position, -1);
            else if (direction.equals(right)) swap(position, 1);
            else Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();

        // Center tiles
        } else {
            if (direction.equals(up)) swap(position, -COLUMNS);
            else if (direction.equals(left)) swap(position, -1);
            else if (direction.equals(right)) swap(position, 1);
            else swap(position, COLUMNS);
        }
    }

    private boolean isSolved() {
        boolean solved = false;

        for (int i = 0; i < tileList.length; i++) {
            if (tileList[i].equals(String.valueOf(i))) {
                solved = true;
            } else {
                solved = false;
                break;
            }
        }

        return solved;
    }
}
