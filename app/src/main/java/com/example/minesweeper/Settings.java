package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.graphics.Color;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Spinner rows = findViewById(R.id.rows);
        Spinner cols = findViewById(R.id.cols);
        Spinner percentMines = findViewById(R.id.percentMines);
        Spinner coveredColor = findViewById(R.id.coveredCellColor);
        Spinner uncoveredColor = findViewById(R.id.uncoveredCellColor);
        Spinner mineColor = findViewById(R.id.mineColor);
        Spinner suspectColor = findViewById(R.id.suspectColor);
        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowsValue = Integer.parseInt(rows.getSelectedItem().toString());
                int colsValue = Integer.parseInt(cols.getSelectedItem().toString());
                int minePercent = Integer.parseInt(percentMines.getSelectedItem().toString());
                int coveredColorValue= getColorFromSpinner(coveredColor);
                int uncoveredColorValue = getColorFromSpinner(uncoveredColor);
                int mineColorValue = getColorFromSpinner(mineColor);
                int suspectColorValue = getColorFromSpinner(suspectColor);

                Intent intent = new Intent(Settings.this, mineBoard.class);
                intent.putExtra("rows", rowsValue);
                intent.putExtra("cols", colsValue);
                intent.putExtra("minePercent", minePercent);
                intent.putExtra("coveredColor", coveredColorValue);
                intent.putExtra("uncoveredColor", uncoveredColorValue);
                intent.putExtra("mineColor", mineColorValue);
                intent.putExtra("suspectColor", suspectColorValue);
                startActivity(intent); // Start game with settings loaded into intent
            }
        });
    }

    public int getColorFromSpinner(Spinner colorSpinner)
    {
        String color = colorSpinner.getSelectedItem().toString();
        switch(color)
        {
            case "Red":
                return Color.RED;
            case "Blue":
                return Color.BLUE;
            case "Green":
                return Color.GREEN;
            case "Yellow":
                return Color.YELLOW;
            case "Gray":
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }
}