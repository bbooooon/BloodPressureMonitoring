package com.example.bloodpressuremonitoring.classify;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.bloodpressuremonitoring.R;
import com.example.bloodpressuremonitoring.Rss.RssActivity;
import com.example.bloodpressuremonitoring.SessionManager;
import java.io.IOException;
import java.util.HashMap;

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = CameraActivity.class.getSimpleName();
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Button buttonCapture;
    private Button buttonRss;
    private TextureView textureViewCamera;
    private Camera camera;
    SessionManager session;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                break;
            case R.id.action_logout:
                finish();
                session.logoutUser();
//                Intent intent = new Intent(this, MainActivity.class);
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

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        String name = user.get(SessionManager.KEY_NAME);
        String email = user.get(SessionManager.KEY_EMAIL);

        buttonCapture = findViewById(R.id.capture_btn);
        textureViewCamera = findViewById(R.id.textureView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x/3;
        int height = width/3*4;

        int left = size.x*60/100;
        int top = size.y*15/100;
        int right = size.x*5/100;
        int bottom = size.y*38/100;

        ImageView cropArea = findViewById(R.id.crop_imageView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(left, top, right, bottom);
        cropArea.setLayoutParams(params);
//        cropArea.setMaxHeight(height);
//        cropArea.setMaxWidth(width);

//        buttonRss.setOnClickListener(view -> openRss());
        buttonCapture.setOnClickListener(view -> takePicture());
        textureViewCamera.setSurfaceTextureListener(this);
        textureViewCamera.setOnClickListener(view -> refocus());
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
        camera = CameraUtil.openCamera(cameraId);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//        parameters.set("iso", 500);
        Camera.Size bestPictureSize = CameraUtil.getBestPictureSize(parameters.getSupportedPictureSizes());
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

    private void takePicture() {
        camera.takePicture(this::playShutterSound,
            null,
            null,
            (img_data, camera) -> {
                CameraUtil.setData(img_data);
                Intent intent = new Intent(this, PreviewActivity.class);
                startActivity(intent);
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