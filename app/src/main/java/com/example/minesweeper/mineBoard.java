package com.example.minesweeper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.gridlayout.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class mineBoard extends AppCompatActivity {

    private int rows;
    private int cols;
    private int minePercent;
    private int coveredColor;
    private int uncoveredColor;
    private int mineColor;
    private int suspectColor;
    private int cellsLeft;
    private Cell[][] cellGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mine_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        rows = intent.getIntExtra("rows", 5);
        cols = intent.getIntExtra("cols", 5);
        minePercent = intent.getIntExtra("minePercent", 10);
        coveredColor = intent.getIntExtra("coveredColor", Color.GRAY);
        uncoveredColor = intent.getIntExtra("uncoveredColor", Color.LTGRAY);
        mineColor = intent.getIntExtra("mineColor", Color.RED);
        suspectColor = intent.getIntExtra("suspectColor", Color.YELLOW);
        cellsLeft = rows * cols - (rows * cols * minePercent / 100);
        setupBoard();
        placeMines();
        calculateNearbyMines();
        Button home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mineBoard.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setupBoard()
    {
        GridLayout mineGrid = findViewById(R.id.mineGrid);
        mineGrid.removeAllViews();
        mineGrid.setRowCount(rows);
        mineGrid.setColumnCount(cols);

        cellGrid = new Cell[rows][cols];
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int buttonSize = screenWidth / cols;
        for(int row = 0; row < rows; row++)
        {
            for(int col = 0; col < cols; col++)
            {
                Button cellButton = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = buttonSize;
                params.height = buttonSize;
                params.setMargins(2,2,2,2);
                cellButton.setLayoutParams(params);
                cellButton.setBackgroundColor(coveredColor);
                mineGrid.addView(cellButton);
                Cell cell = new Cell(cellButton);
                cellGrid[row][col] = cell;
                cellButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        int row = mineGrid.indexOfChild(v) / cols;
                        int col = mineGrid.indexOfChild(v) % cols;
                        Cell cell = cellGrid[row][col];
                        // Check if cell has been revealed already
                        // If revealed already, do nothing
                        if(!cell.beenRevealed() && !cell.flagged)
                        {
                            boolean mine = cell.revealCell();
                            if (mine)
                            {
                                for(int i = 0; i < rows; i++)
                                {
                                    for(int j = 0; j < cols; j++)
                                    {
                                        Cell myCell = cellGrid[i][j];
                                        if(myCell.isMine())
                                        {
                                            myCell.getButton().setBackgroundColor(mineColor);
                                        }
                                    }
                                }
                                // Reveal all other mines.
                                Toast toast = new Toast(getApplicationContext());
                                toast.setGravity(Gravity.BOTTOM, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setText("You lost!");
                                toast.show();
                            }
                            else
                            {
                                cellsLeft--;
                                if(cellsLeft == 0)
                                {
                                    // Game won, end the game and return to home screen
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                                    toast.setDuration(Toast.LENGTH_LONG);
                                    toast.setText("You won!");
                                    toast.show();
                                }
                                int nearbyMines = cell.getNearbyMines();
                                cell.getButton().setBackgroundColor(uncoveredColor);
                                if(nearbyMines > 0)
                                    cell.getButton().setText(String.valueOf(nearbyMines));
                                else
                                {
                                    // Implement flood fill function here
                                    revealCells();
                                }

                            }
                        }


                    }
                });
                cellButton.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v)
                    {
                        int row = mineGrid.indexOfChild(v) / cols;
                        int col = mineGrid.indexOfChild(v) % cols;
                        Cell cell = cellGrid[row][col];
                        if(!cell.beenRevealed())
                        {
                            if(cell.isFlagged())
                            {
                                cell.setFlagged(false);
                                v.setBackgroundColor(coveredColor);
                            }
                            else
                            {
                                cell.setFlagged(true);
                                v.setBackgroundColor(suspectColor);
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void revealCells()
    {
        // To implement
    }
    private void placeMines()
    {
        int minesToPlace = (minePercent * rows * cols) / 100;
        while(minesToPlace > 0)
        {
            int row = (int)(Math.random() * rows);
            int col = (int)(Math.random() * cols);
            Cell cell = cellGrid[row][col];
            if(!cell.mine)
            {
                cell.setAsMine();
                minesToPlace--;
            }
        }
    }

    private void calculateNearbyMines()
    {
        for(int row = 0; row < rows; row++)
        {
            for(int col = 0; col < cols; col++)
            {
                Cell cell = cellGrid[row][col];
                if(!cell.mine)
                {
                    int nearbyMines = 0;
                    for(int lr = col - 1; lr <= col + 1; lr++)
                    {
                        for(int tb = row - 1; tb <= row + 1; tb++)
                        {
                            if(lr == col && tb == row)
                            {
                                continue;
                            }
                            if(lr >=0 && lr < cols && tb >= 0 && tb < rows)
                            {
                                Cell neighbor = cellGrid[tb][lr];
                                if(neighbor.isMine()) {
                                    nearbyMines++;
                                }
                            }
                        }
                    }
                    cell.setNearbyMines(nearbyMines);
                }
            }
        }
    }
    private class Cell
    {
        private Button button;
        private boolean mine;
        private boolean revealed;
        private boolean flagged;
        private int nearbyMines;

        public Cell(Button button)
        {
            this.button = button;
            this.mine = false;
            this.revealed = false;
            this.flagged = false;
            this.nearbyMines = 0;
        }

        public Button getButton()
        {
            return this.button;
        }
        public boolean isMine()
        {
            return mine;
        }
        public void setAsMine()
        {
            this.mine = true;
        }
        public boolean beenRevealed()
        {
            return revealed;
        }

        public boolean revealCell()
        {
            this.revealed = true;
            // Calculate number of nearby mines and set it
            return this.mine;

        }

        public boolean isFlagged()
        {
            return flagged;
        }

        public void setFlagged(boolean flag)
        {
            this.flagged = flag;
        }

        public int getNearbyMines()
        {
            return nearbyMines;
        }

        public void setNearbyMines(int numMines)
        {
            this.nearbyMines = numMines;
        }
    }
}