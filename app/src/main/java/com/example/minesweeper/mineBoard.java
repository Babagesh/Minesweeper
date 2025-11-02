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
        Intent intent = getIntent(); // Load intent
        // Load settings from intent
        rows = intent.getIntExtra("rows", 5);
        cols = intent.getIntExtra("cols", 5);
        minePercent = intent.getIntExtra("minePercent", 10);
        coveredColor = intent.getIntExtra("coveredColor", Color.GRAY);
        uncoveredColor = intent.getIntExtra("uncoveredColor", Color.LTGRAY);
        mineColor = intent.getIntExtra("mineColor", Color.RED);
        suspectColor = intent.getIntExtra("suspectColor", Color.YELLOW);
        cellsLeft = rows * cols - (rows * cols * minePercent / 100);
        setupBoard(); // Set up minesweeper board
        placeMines(); // Place mines randomly
        calculateNearbyMines(); // Give each cell a value of nearby mines
        Button home = findViewById(R.id.home); // Home button to return
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
        // Set up gridlayout dimensions
        GridLayout mineGrid = findViewById(R.id.mineGrid);
        mineGrid.removeAllViews();
        mineGrid.setRowCount(rows);
        mineGrid.setColumnCount(cols);

        // Setup cell size
        cellGrid = new Cell[rows][cols];
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int buttonSize = screenWidth / cols;
        for(int row = 0; row < rows; row++)
        {
            for(int col = 0; col < cols; col++)
            {
                // Create a button and cell
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
                // Set up click listener
                cellButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v)
                    {
                        int row = mineGrid.indexOfChild(v) / cols;
                        int col = mineGrid.indexOfChild(v) % cols;
                        Cell cell = cellGrid[row][col]; // Get cell which was clicked
                        // Check if cell has been revealed already
                        if(!cell.beenRevealed() && !cell.flagged)
                        {
                            boolean mine = cell.isMine();
                            if (mine) // Logic for if cell is mine
                            {
                                for(int i = 0; i < rows; i++)
                                {
                                    for(int j = 0; j < cols; j++)
                                    {
                                        Cell myCell = cellGrid[i][j];
                                        myCell.getButton().setEnabled(false);
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
                                revealCells(row, col); // Reveal cell and neighbors as needed
                            }
                        }
                    }
                });
                // Logic to flag cell
                cellButton.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v)
                    {
                        int row = mineGrid.indexOfChild(v) / cols;
                        int col = mineGrid.indexOfChild(v) % cols;
                        Cell cell = cellGrid[row][col];
                        if(!cell.beenRevealed())
                        {
                            if(cell.isFlagged()) // Unflag cell
                            {
                                cell.setFlagged(false);
                                cell.getButton().setBackgroundColor(coveredColor);
                                cell.getButton().setForeground(null);
                            }
                            else // Flag cell
                            {
                                cell.setFlagged(true);
                                cell.getButton().setBackgroundColor(suspectColor);
                                cell.getButton().setForeground(getDrawable(R.mipmap.flag_foreground));
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    // Function which reveals cells and neighbors
    private void revealCells(int row, int col)
    {
        // To implement
        if(cellGrid[row][col].beenRevealed() || cellGrid[row][col].isFlagged() || cellGrid[row][col].isMine())
        {
            return;
        }
        cellGrid[row][col].revealCell();
        cellGrid[row][col].getButton().setBackgroundColor(uncoveredColor);
        cellsLeft--;
        if(cellsLeft == 0)
        {
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0,0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText("You won!");
            toast.show();
            endGame();
            return;
        }
        if(cellGrid[row][col].nearbyMines != 0)
        {
            cellGrid[row][col].getButton().setText(String.valueOf(cellGrid[row][col].nearbyMines));
        }
        else
        {
            for(int tb = row - 1; tb <= row + 1; tb++)
            {
                for(int lr = col - 1; lr <= col + 1; lr++)
                {
                    if(lr >= 0 && lr < cols && tb >= 0 && tb < rows)
                    {
                        revealCells(tb, lr);
                    }
                }
            }
        }
    }

    // Function to place mines on grid
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

    // End game and reveal mines
    private void endGame()
    {
        for(int r = 0; r < rows; r++)
        {
            for(int c = 0; c < cols; c++)
            {
                Cell cell = cellGrid[r][c];
                cell.getButton().setEnabled(false);
                if(cell.isMine())
                {
                    cell.getButton().setBackgroundColor(mineColor);
                }
            }
        }
    }

    // Set each mine to have a value of nearby mines
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

    // Cell class
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

        public void revealCell()
        {
            this.revealed = true;
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