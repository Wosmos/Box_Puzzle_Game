package com.example.boxpuzzler;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GridLayout puzzleGrid;
    private TextView movesText;
    private TextView winText;
    private  Button restartButton;
    private List<ImageView> tiles;
    private int emptyPosition = 8; // Last position is empty
    private int moves = 0;
    private static final int GRID_SIZE = 3;
    private final int[] imageResources = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3,
            R.drawable.image_4, R.drawable.image_5, R.drawable.image_6,
            R.drawable.image_7, R.drawable.image_8, R.drawable.image_9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        puzzleGrid = findViewById(R.id.puzzle_grid);
        movesText = findViewById(R.id.moves_text);
        winText = findViewById(R.id.win_text);
        restartButton = findViewById(R.id.restart_button);

        // Setup game
        initializeGame();

        // Set restart button click listener
        restartButton.setOnClickListener(v -> initializeGame());
    }

    private void initializeGame() {
        // Reset game state
        moves = 0;
        movesText.setText("Moves: 0");
        winText.setVisibility(View.GONE);
        puzzleGrid.removeAllViews();
        tiles = new ArrayList<>();

        // Create shuffled indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            indices.add(i);
        }
        do {
            Collections.shuffle(indices);
        } while (!isSolvable(indices));

        // Create and add tiles to grid
        int tileSize = getResources().getDisplayMetrics().widthPixels / 4; // Use 1/4 of screen width

        for (int i = 0; i < 9; i++) {
            ImageView tile = new ImageView(this);
            tile.setLayoutParams(new GridLayout.LayoutParams());
            tile.getLayoutParams().width = tileSize;
            tile.getLayoutParams().height = tileSize;
            tile.setScaleType(ImageView.ScaleType.FIT_XY);
            tile.setPadding(2, 2, 2, 2);

            final int position = i;
            tile.setOnClickListener(v -> handleTileClick(position));

            if (indices.get(i) < 8) {
                tile.setImageResource(imageResources[indices.get(i)]);
                tile.setTag(indices.get(i));
            } else {
                tile.setBackgroundColor(0xFFE0E0E0);
                tile.setTag(-1);
                emptyPosition = position;
            }

            tiles.add(tile);
            puzzleGrid.addView(tile);
        }
    }

    private void handleTileClick(int position) {
        // Check if clicked tile is adjacent to empty space
        if (isAdjacent(position, emptyPosition)) {
            // Swap tiles
            swapTiles(position, emptyPosition);
            moves++;
            movesText.setText("Moves: " + moves);
            emptyPosition = position;

            // Check if puzzle is solved
            if (isPuzzleSolved()) {
                winText.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isAdjacent(int pos1, int pos2) {
        int row1 = pos1 / GRID_SIZE;
        int col1 = pos1 % GRID_SIZE;
        int row2 = pos2 / GRID_SIZE;
        int col2 = pos2 % GRID_SIZE;

        return Math.abs(row1 - row2) + Math.abs(col1 - col2) == 1;
    }

    private void swapTiles(int pos1, int pos2) {
        ImageView tile1 = tiles.get(pos1);
        ImageView tile2 = tiles.get(pos2);

        // Swap images and tags
        Object tempTag = tile1.getTag();
        Integer tempBackground = null;
        if (tile1.getDrawable() != null) {
            tempBackground = imageResources[(Integer) tile1.getTag()];
        }

        if (tile2.getTag().equals(-1)) {
            tile1.setImageDrawable(null);
            tile1.setBackgroundColor(0xFFE0E0E0);
            tile1.setTag(-1);

            if (tempBackground != null) {
                tile2.setBackgroundColor(0x00000000);
                tile2.setImageResource(tempBackground);
                tile2.setTag(tempTag);
            }
        } else {
            tile1.setBackgroundColor(0x00000000);
            tile1.setImageResource(imageResources[(Integer) tile2.getTag()]);
            tile1.setTag(tile2.getTag());

            tile2.setImageDrawable(null);
            tile2.setBackgroundColor(0xFFE0E0E0);
            tile2.setTag(-1);
        }
    }

    private boolean isPuzzleSolved() {
        for (int i = 0; i < tiles.size() - 1; i++) {
            if (!tiles.get(i).getTag().equals(i)) {
                return false;
            }
        }
        return tiles.get(8).getTag().equals(-1);
    }

    private boolean isSolvable(List<Integer> puzzle) {
        int inversions = 0;
        for (int i = 0; i < puzzle.size() - 1; i++) {
            for (int j = i + 1; j < puzzle.size(); j++) {
                if (puzzle.get(i) != 8 && puzzle.get(j) != 8 && puzzle.get(i) > puzzle.get(j)) {
                    inversions++;
                }
            }
        }
        // For 3x3 puzzle with blank at the end, puzzle is solvable if inversions is even
        return inversions % 2 == 0;
    }
}



