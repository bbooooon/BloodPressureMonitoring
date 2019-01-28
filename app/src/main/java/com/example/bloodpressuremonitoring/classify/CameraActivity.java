package com.example.bloodpressuremonitoring.classify;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bloodpressuremonitoring.R;
import com.example.bloodpressuremonitoring.Rss.RssActivity;
import com.example.bloodpressuremonitoring.SessionManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Button buttonCapture;
    private Button buttonFlash;
    private Button buttonRss;
    private TextureView textureViewCamera;
    private Camera camera;
    SessionManager session;
    private boolean isLighOn = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_help:
//                break;
            case R.id.action_logout:
                finish();
                session.logoutUser();
//                Intent intent = new Intent(this, SigninActivity.class);
//                startActivity(intent);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Toolbar camera_toolbar = findViewById(R.id.camera_toolbar);
        setSupportActionBar(camera_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        camera_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManager.KEY_EMAIL);

        final Dialog dialog = new Dialog(CameraActivity.this);
        dialog.setTitle("การเตรียมตัวก่อนการวัดความดันโลหิต");
        dialog.setContentView(R.layout.custom_dialog);

        Button button = (Button) dialog.findViewById(R.id.button_submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        buttonCapture = findViewById(R.id.capture_btn);
        buttonFlash = findViewById(R.id.flash_btn);
        textureViewCamera = findViewById(R.id.textureView);

        int toolbarheight = camera_toolbar.getHeight();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x/3;
        int height = width/3*4;

        int left = size.x/2 - width/2;
        int top = size.y/4 - height/2;
        int right = size.x/2 - width/2;
        int bottom = size.y - height - top;

        ImageView cropArea = findViewById(R.id.crop_imageView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);

        params.setMargins(left, top, right, bottom);
        cropArea.setLayoutParams(params);
//        cropArea.setMaxHeight(height);
//        cropArea.setMaxWidth(width);

        TextureView textureview = findViewById(R.id.textureView);
        ConstraintLayout.LayoutParams parammaters = new ConstraintLayout.LayoutParams(size.x, size.y);
        parammaters.setMargins(0, 55, 0, 0);
        textureview.setLayoutParams(parammaters);

//        buttonRss.setOnClickListener(view -> openRss());
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
        textureViewCamera.setSurfaceTextureListener(this);
        textureViewCamera.setOnClickListener(view -> refocus());

//        buttonFlash.setVisibility(View.GONE);
        buttonFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLighOn) {
                    isLighOn = false;
                    buttonFlash.setBackgroundResource(R.drawable.flashlight_off);
                    setupCamera(textureViewCamera.getWidth(), textureViewCamera.getHeight());
                    startCameraPreview(textureViewCamera.getSurfaceTexture());
                } else {
                    isLighOn = true;
                    buttonFlash.setBackgroundResource(R.drawable.flashlight_on);
                    setupCamera(textureViewCamera.getWidth(), textureViewCamera.getHeight());
                    startCameraPreview(textureViewCamera.getSurfaceTexture());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (textureViewCamera.isAvailable()) {
            setupCamera(textureViewCamera.getWidth(), textureViewCamera.getHeight());
            startCameraPreview(textureViewCamera.getSurfaceTexture());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureViewCamera.setSurfaceTextureListener(null);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        setupCamera(width, height);
        startCameraPreview(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        stopCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    private void openRss(){
        Intent intent_rss = new Intent(this, RssActivity.class);
        startActivity(intent_rss);
    }

    private void setupCamera(int width, int height) {
        camera = CameraUtil.openDefaultCamera();
        Camera.Parameters parameters = camera.getParameters();
        if (isLighOn) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
        else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//        Camera.Size bestPictureSize = CameraUtil.getBestPictureSize(parameters.getSupportedPictureSizes());

        Camera.Size bestPictureSize = CameraUtil.getOptimalPreviewSize(parameters.getSupportedPictureSizes(),width,height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);
        if (CameraUtil.isContinuousFocusModeSupported(parameters.getSupportedFocusModes())) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        camera.setParameters(parameters);
        camera.setDisplayOrientation(CameraUtil.getCameraDisplayOrientation(this, cameraId));
    }

    private void startCameraPreview(SurfaceTexture surfaceTexture) {
        try {
            camera.setPreviewTexture(surfaceTexture);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Error start camera preview: " + e.getMessage());
        }
    }

    private void stopCamera() {
        try {
            camera.stopPreview();
            camera.release();
        } catch (Exception e) {
            Log.e(TAG, "Error stop camera preview: " + e.getMessage());
        }
    }

    public void screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        byte[] bytearr1 = stream2.toByteArray();
        CameraUtil.setData(bytearr1);
//        CameraUtil.bytedata = bytearr1;
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
    }

    private void takePicture() {
        camera.takePicture(this::playShutterSound,
            null,
            null,
            (img_data, camera) -> {
//                CameraUtil.setData(img_data);
                Bitmap bitmap = BitmapFactory.decodeByteArray(img_data, 0, img_data.length);
                BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                ImageView imgview = findViewById(R.id.imageView2);
                imgview.setVisibility(View.VISIBLE);
                imgview.setImageBitmap(bitmap);
                screenShot(imgview);
                imgview.setVisibility(View.GONE);
//                finish();
//                Intent intent = new Intent(this, PreviewActivity.class);
//                startActivity(intent);
            });
    }

    private void refocus() {
        camera.autoFocus((success, camera) -> playFocusSound());
    }

    private void playShutterSound() {
        MediaActionSound sound = new MediaActionSound();
        sound.play(MediaActionSound.SHUTTER_CLICK);
    }

    private void playFocusSound() {
    }
}