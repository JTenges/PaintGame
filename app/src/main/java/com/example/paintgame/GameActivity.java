package com.example.paintgame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.Random;
import java.util.function.Predicate;

public class GameActivity extends AppCompatActivity {
    Button[][] buttonGrid;
    Predicate<Integer> validIndex = i -> i >= 0 && i < buttonGrid.length;
    Random randomGen = new Random();
    int blue;
    int red;

    private int getButtonIndex(String axis, View v) {
        for (int y = 0; y < buttonGrid.length; ++y) {
            for (int x = 0; x < buttonGrid.length; ++x) {
                if (v == buttonGrid[y][x]) {
                    if (axis.equals("y")) {
                        return y;
                    } else if (axis.equals("x")) {
                        return x;
                    }

                }
            }
        }
        return -1;
    }

    private void changeColor(View v) {
        Drawable background = v.getBackground();
        if (background instanceof ColorDrawable) {
            int backgroundColor = ((ColorDrawable) background).getColor();
            if (backgroundColor == blue) {
                v.setBackgroundColor(red);
            } else {
                v.setBackgroundColor(blue);
            }
        }
    }

    private boolean solved() {
        for (int y = 0; y < buttonGrid.length; ++y) {
            for (int x = 0; x < buttonGrid.length; ++x) {
                Drawable background = buttonGrid[y][x].getBackground();
                if (background instanceof ColorDrawable) {
                    int backgroundColor = ((ColorDrawable) background).getColor();
                    if (backgroundColor == red) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    private void clearGridText() {
        for (int y = 0; y < buttonGrid.length; ++y) {
            for (int x = 0; x < buttonGrid.length; ++x) {
                buttonGrid[y][x].setText("");
            }
        }
    }

    private void randomiseGrid() {
        for (int y = 0; y < buttonGrid.length; ++y) {
            for (int x = 0; x < buttonGrid.length; ++x) {
                Button button = buttonGrid[y][x];
                button.setText("");
                if(randomGen.nextBoolean()) {
                    changeColorInCross(button);
                }

            }
        }
    }

    private void changeColorInCross(View v) {
        changeColor(v);
        int yIndex = getButtonIndex("y", v);
        int xIndex = getButtonIndex("x", v);


        if (validIndex.test(yIndex - 1)) {
            changeColor(buttonGrid[yIndex - 1][xIndex]);
        }
        if (validIndex.test(xIndex - 1)) {
            changeColor(buttonGrid[yIndex][xIndex - 1]);
        }
        if (validIndex.test(yIndex + 1)) {
            changeColor(buttonGrid[yIndex + 1][xIndex]);
        }
        if (validIndex.test(xIndex + 1)) {
            changeColor(buttonGrid[yIndex][xIndex + 1]);
        }

        if (solved()) {
            Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_LONG).show();
            randomiseGrid();
        }
    }

    private boolean[][] getGridCopy(boolean[][] grid) {
        boolean[][] copy = new boolean[grid.length][grid.length];
        for(int i=0; i<grid.length; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, grid[i].length);
        }
        return copy;
    }

    private void simulateTap(boolean[][] grid, int x, int y) {
        Predicate<Integer> validGridIndex = i -> i >= 0 && i < grid.length;
        grid[y][x] = !grid[y][x];
        if (validGridIndex.test(y - 1)) {
            grid[y - 1][x] = !grid[y - 1][x];
        }
        if (validGridIndex.test(x - 1)) {
            grid[y][x - 1] = !grid[y][x - 1];
        }
        if (validGridIndex.test(y + 1)) {
            grid[y + 1][x] = !grid[y + 1][x];
        }
        if (validGridIndex.test(x + 1)) {
            grid[y][x + 1] = !grid[y][x + 1];
        }
    }

    private int getBit(int n, int k) {
        return (n >> k) & 1;
    }

    private boolean isSolution(boolean[][] grid) {
        for (int y = 0; y < grid.length; ++y) {
            for (int x = 0; x < grid.length; ++x) {
                if (!grid[y][x]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkRemainingRows(boolean[][] grid, boolean[][] solutionGrid) {
        for (int y = 1; y < grid.length; ++y) {
            for (int x = 0; x < grid.length; ++x) {
                if (!grid[y - 1][x]) {
                    simulateTap(grid, x, y);
                    solutionGrid[y][x] = true;
                }
            }
        }
        return isSolution(grid);
    }

    public void solveGrid(View view) {
        clearGridText();
        // Turn buttonGrid to boolean array
        boolean[][] booleanGrid = new boolean[buttonGrid.length][buttonGrid.length];

        // Sets blue squares to true and red to false
        for (int y = 0; y < buttonGrid.length; ++y) {
            for (int x = 0; x < buttonGrid.length; ++x) {
                Drawable background = buttonGrid[y][x].getBackground();
                if (background instanceof ColorDrawable) {
                    int backgroundColor = ((ColorDrawable) background).getColor();
                    if (backgroundColor == blue) {
                        booleanGrid[y][x] = true;
                    }
                }
            }
        }

        boolean[][] gridCopy;
        boolean[][] solutionGrid = null;
        for (int i = 0; i < Math.pow(2, booleanGrid.length); ++i) {
            gridCopy = getGridCopy(booleanGrid);
            solutionGrid = new boolean[booleanGrid.length][booleanGrid.length];
            // Try top row taps
            for (int x = 0; x < booleanGrid[0].length; ++x) {
                if (getBit(i, x) == 1) {
                    simulateTap(gridCopy, x, 0);
                    solutionGrid[0][x] = true;
                }
            }
            if (checkRemainingRows(gridCopy, solutionGrid)) {
                break;
            }
        }

        for (int y = 0; y < solutionGrid.length; ++y) {
            for (int x = 0; x < solutionGrid.length; ++x) {
                if (solutionGrid[y][x]) {
                    buttonGrid[y][x].setText("tap");
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        blue = getColor(R.color.blue);
        red = getColor(R.color.red);

        int grid_size = getIntent().getIntExtra("grid_size", -1);
        GlobalClass.addToCode(String.valueOf(grid_size));
        buttonGrid = new Button[grid_size][grid_size];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GridLayout gridLayout = findViewById(R.id.buttonGridLayout);
        gridLayout.setColumnCount(grid_size);
        gridLayout.setRowCount(grid_size);
        View.OnClickListener buttonPress = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof Button) {
                    changeColorInCross(view);

                }
            }
        };

        Button button;
        for (int y = 0; y < gridLayout.getRowCount(); ++y) {
            for (int x = 0; x < gridLayout.getColumnCount(); ++x) {
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                        GridLayout.spec(y,1f),
                        GridLayout.spec(x, 1f)
                );
                params.setMargins(8, 8, 8, 8);
                params.height = 0;
                params.width = 0;

                button = new Button(gridLayout.getContext());
                button.setLayoutParams(params);
                button.setBackgroundColor(blue);

                button.setOnClickListener(buttonPress);
                gridLayout.addView(button);
                buttonGrid[y][x] = button;
            }
        }
        randomiseGrid();

        if (GlobalClass.isCheatMode()) {
            Button solveButton = findViewById(R.id.solveButton);
            solveButton.setVisibility(View.VISIBLE);
        }


//        Button button = new Button(gridLayout.getContext());
//        button.setLayoutParams(params);
//        button.setBackgroundColor(getColor(R.color.blue));
//        gridLayout.addView(button);
    }
}
