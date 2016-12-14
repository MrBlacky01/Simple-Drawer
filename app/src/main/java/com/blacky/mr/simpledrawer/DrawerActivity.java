package com.blacky.mr.simpledrawer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity {

    Button colorButton;
    Button sizeButton;
    LinearLayout paintLayout;
    DrawingView drawingView;

    Integer lineSize;
    Integer lineColor;
    Integer paintWidth;
    Integer paintHeigth;

    //Paint paint;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        colorButton = (Button) findViewById(R.id.color_button);
        sizeButton = (Button)findViewById(R.id.size_button);
        paintLayout = (LinearLayout) findViewById(R.id.paint_linear_layout);

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
        colorButton.setBackgroundColor(lineColor);

       ViewTreeObserver viewTreeObserver = paintLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    paintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    paintWidth = paintLayout.getWidth();
                    paintHeigth = paintLayout.getHeight();

                    drawingView = new DrawingView(getApplicationContext());
                    paintLayout.addView(drawingView, new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));

                }
            });
        }
    }

   /* @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lineSize", lineSize);
        outState.putInt("lineColor",lineColor);
        outState.putInt("pathCount",drawingView.    getPathesCount());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        lineColor = savedInstanceState.getInt("lineColor");
        lineSize = savedInstanceState.getInt("lineSize");
        drawingView.setPathesCount(savedInstanceState.getInt("pathCount"));
        drawingView.setGraphics((ArrayList<PathWithPaint>) getLastNonConfigurationInstance());

    }


    @Override
    public Object onRetainCustomNonConfigurationInstance(){
        super.onRetainNonConfigurationInstance();
        return drawingView.getGraphics();
    }*/

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
                //paint.setStrokeWidth(lineSize);
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
                ((DrawingView)drawingView).ChangePaint();
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
                ((DrawingView)drawingView).ChangePaint();
                //paint.setColor(color);
            }

        });
        colorPickerDialog.show();
    }

    class DrawingView extends View {

        private Path path;
        private Bitmap mBitmap;
        private Canvas mCanvas;

        public DrawingView(Context context) {
            super(context);
            path = new Path();
            mBitmap = Bitmap.createBitmap(paintWidth, paintHeigth, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            this.setBackgroundColor(Color.WHITE);
            pathesCount = 0;
            graphics.add(new PathWithPaint(path,lineColor,lineSize));
        }

        private ArrayList<PathWithPaint> graphics = new ArrayList<>();
        private Integer pathesCount;

        public ArrayList<PathWithPaint> getGraphics()
        {
            return  graphics;
        }

        public Integer getPathesCount()
        {
            return pathesCount;
        }

        public void setGraphics(ArrayList<PathWithPaint> graphics)
        {
            this.graphics = graphics;
        }

        public void  setPathesCount(Integer count)
        {
            pathesCount = count;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            mCanvas.drawPath(path,new Paint());
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                path.moveTo(event.getX(), event.getY());
                path.lineTo(event.getX(), event.getY());
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                path.lineTo(event.getX(), event.getY());
                graphics.get(pathesCount).setPath(path);;
            }
            invalidate();
            return true;
        }

        public void ChangePaint()
        {
            path = new Path();
            graphics.add(new PathWithPaint(path,lineColor,lineSize));
            pathesCount++;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for(int i = 0; i < graphics.size(); i++){
                canvas.drawPath(
                        graphics.get(i).getPath(),
                        graphics.get(i).getPaint());
            }
        }
    }

}
