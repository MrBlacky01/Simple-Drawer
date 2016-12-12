package com.blacky.mr.simpledrawer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class DrawerActivity extends AppCompatActivity {

    Button colorButton;
    Button sizeButton;

    Integer lineSize;
    Integer lineColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        colorButton = (Button) findViewById(R.id.color_button);
        sizeButton = (Button)findViewById(R.id.size_button);

        lineSize = 1;
        lineColor = Color.BLACK;

        sizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSizeDialog();
            }
        });
        sizeButton.setText(String.valueOf(lineSize));

        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowColorDialog();
            }
        });

    }

    public void ShowSizeDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final SeekBar seekBar = new SeekBar(this);

        seekBar.setMax(9);
        seekBar.setProgress(lineSize-1);
        popDialog.setTitle("Select line size:");
        popDialog.setView(seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lineSize = progress+1;
                sizeButton.setText(String.valueOf(lineSize));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        popDialog.create();
        popDialog.show();
    }

    private void ShowColorDialog()
    {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, lineColor, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                lineColor = color;
                colorButton.setBackgroundColor(color);
            }

        });
        colorPickerDialog.show();
    }


}
