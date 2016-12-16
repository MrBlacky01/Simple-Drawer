package com.blacky.mr.simpledrawer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.IOException;
import java.util.ArrayList;

public class DrawerActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    ShareDialog shareDialog;

    Button colorButton;
    Button sizeButton;
    LinearLayout paintLayout;
    DrawingView drawingView;
    ImageView backgroundImageView;
    Toolbar toolbar;

    Integer lineSize;
    Integer lineColor;
    Integer paintWidth;
    Integer paintHeigth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        shareDialog = new ShareDialog(this);
        colorButton = (Button) findViewById(R.id.color_button);
        sizeButton = (Button)findViewById(R.id.size_button);
        paintLayout = (LinearLayout) findViewById(R.id.paint_linear_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar_drawer);

        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

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

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(DrawerActivity.this,"",Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancel() {
                Toast.makeText(DrawerActivity.this,"",Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(DrawerActivity.this,"",Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_item_drawer:
                finish();
                return true;
            case R.id.share_item_drawer:
                CapturePhotoUtils.insertImage(getContentResolver(),mark(drawingView.getBitmap()),"");
                //ShareImage();
                return true;
            case R.id.backgroung_item_drawer:
                ShowBackGroundDialog();
                return true;
            case R.id.save_item_drawer:
                ShowSaveDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    callbackManager.onActivityResult(requestCode, resultCode, imageReturnedIntent);
                }
                break;
            case 2:
            case 1:
                if(resultCode == RESULT_OK){

                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                        drawingView.AddBackground(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void selectImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    private void takePicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 2);
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

    private void ShareImage(){

        Bitmap image = drawingView.getBitmap();
        SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
        SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result)
            {
                //Toast.makeText(getApplicationContext(), getString(R.string.facebookSuccessful), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel()
            {
                //Log.v("FACEBOOK_TEST", "share api cancel");
            }

            @Override
            public void onError(FacebookException e)
            {
                //Log.v("FACEBOOK_TEST", "share api error " + e);
            }
        });
    }

    private void ShowBackGroundDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_action_dialog)
                .setItems(R.array.background_actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                selectImage();
                                break;
                            case 1:
                                takePicture();
                                break;
                            case 2:
                                drawingView.ClearBackground();
                                break;
                        }
                    }
                });
        builder.create();
        builder.show();
    }

   public void ShowSaveDialog()
   {
       final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);

       View dialoglayout = getLayoutInflater().inflate(R.layout.save_dialog, null);
       final EditText title = (EditText)  dialoglayout.findViewById(R.id.save_title);

       popDialog.setTitle("Save picture");
       popDialog.setView(dialoglayout);

       popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
               CapturePhotoUtils.insertImage(getContentResolver(),drawingView.getBitmap(),title.getText().toString());
           }
       });

       popDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               dialog.dismiss();
           }
       });
       popDialog.create();
       popDialog.show();
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
                drawingView.ChangePaint();
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
                drawingView.ChangePaint();

            }

        });
        colorPickerDialog.show();
    }

    public static Bitmap mark(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        int min = Math.min(w,h);

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setAlpha(140);
        paint.setTextSize(min/10);
        paint.setAntiAlias(true);
        canvas.drawText("Test Android", 0, h-min/10, paint);

        return result;
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

        public void ChangePaint() {
            path = new Path();
            graphics.add(new PathWithPaint(path,lineColor,lineSize));
            pathesCount++;
        }

        public void AddBackground(Bitmap image){
            this.setBackground((Drawable)new BitmapDrawable(image));
            mCanvas.drawBitmap(image,0,0,new Paint());
        }

        public void ClearBackground()
        {
            this.setBackgroundColor(Color.WHITE);
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

        public Bitmap getBitmap() {
            this.setDrawingCacheEnabled(true);
            this.buildDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
            this.setDrawingCacheEnabled(false);
            return bmp;
        }

        public void setBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
        }
    }

}
