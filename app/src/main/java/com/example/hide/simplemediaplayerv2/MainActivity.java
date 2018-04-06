package com.example.hide.simplemediaplayerv2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity  implements SurfaceHolder.Callback {
    private static final String TAG = "SimpleMediaPlayerV2";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=999;

    private static final String MP4_FILE = "a1.mp4";
    private static final String VIDEO_PATH = System.getenv("EXTERNAL_STORAGE") + "/video/";
    String mediaPath =  VIDEO_PATH + MP4_FILE;

    private static boolean mpstarted=false;
    private static boolean haveafile=false;
    private static boolean havepermission=false;
    private SurfaceHolder holder1;
    private void appEnd(){
        Log.i(TAG, "appEnd");
        this.finish();
    }
    private void checkFile(){
        if (new File(mediaPath).exists()) {
            Log.i(TAG, "A video file is exist");
            haveafile=true;
        } else {
            Log.i(TAG, "A video file is not exist");
            new AlertDialog.Builder(this)
                    .setMessage("Put \"" + MP4_FILE + "\" to " + VIDEO_PATH + ", and Execute again.")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) { appEnd(); }
                    }).show();
        }
    }


    MediaPlayer mp = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* No window title */
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* Set Full screen flag */
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        //| View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        //| View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

                        //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        ); /* OK */
        /* add FLAG_KEEP_SCREEN_ON */
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /* Support Transparent color */
        //getWindow().setFormat(PixelFormat.TRANSPARENT);
        //getWindow().setFormat(PixelFormat.RGBX_8888);

        SurfaceView  mPreview = findViewById(R.id.surfaceView);
        //mPreview.setSecure(true);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        holder1 = mPreview.getHolder();
        holder1.addCallback(this);

        // Assume thisActivity is the current activity
        if ( ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setMessage("To use this app, need to get permission to read external storage")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) { requestPermission();}
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                requestPermission();
            }
        } else {
            havepermission=true;
            checkFile();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    havepermission=true;
                    checkFile();
                    mediaPlay(mp, mediaPath);
                } else {
                    Log.i(TAG, "not allowed");
                    appEnd();
                }
            }
        }
    }
    private void requestPermission(){
        Log.i(TAG, "requestPermission");
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }
    private void mediaPlay(MediaPlayer mp,String mediaPath) {
        if (!mpstarted && havepermission && haveafile) {
            mpstarted=true;

            try {
                mp.setVolume((float) 0.5, (float) 0.5);
                mp.setLooping(true);
                mp.setDisplay(holder1);
                mp.setDataSource(mediaPath);
                mp.prepare();
                mp.start();
            } catch (IllegalArgumentException|IllegalStateException|IOException e) {
                e.printStackTrace();
                appEnd();
            }
        } else if (!mpstarted) {
            Log.i(TAG, "Not confirmed permission");
        } else {
            Log.i(TAG, "MP is already started");
            try {
                mp.setVolume((float) 0.5, (float) 0.5);
                mp.setLooping(true);
                mp.setDisplay(holder1);
                mp.setDataSource(mediaPath);
                mp.prepare();
                mp.start();
            } catch (IllegalArgumentException|IllegalStateException|IOException e) {
                e.printStackTrace();
                appEnd();
            }
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        Log.i(TAG, "surfaceCreated()");
        if(paramSurfaceHolder==holder1) {
            Log.i(TAG, "holder1");
        }
//        mediaPlay(mp, mediaPath);
    }

    @Override
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
                               int paramInt2, int paramInt3) {
        Log.i(TAG, "surfaceChanged()");
        mediaPlay(mp, mediaPath);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        Log.i(TAG, "surfaceDestroyed()");
    }

}
