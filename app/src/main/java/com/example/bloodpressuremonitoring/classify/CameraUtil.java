package com.example.bloodpressuremonitoring.classify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CameraUtil {
    private static final String TAG = CameraActivity.class.getSimpleName();
    public static Bitmap bitmap;
    public static byte[] bytedata;
    public static String filename;
    public static String takentime;
    public static boolean isCameraSupport(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera openDefaultCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            Log.e(TAG, "Error open camera: " + e.getMessage());
        }
        return camera;
    }

    public static Camera openCamera(int cameraId) {
        Camera camera = null;
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            Log.e(TAG, "Error open camera: " + e.getMessage());
        }
        return camera;
    }

    public static int getNumberOfCamera() {
        return Camera.getNumberOfCameras();
    }

    public static Camera.Size getBestPictureSize(@NonNull List<Camera.Size> pictureSizeList) {
        Camera.Size bestPictureSize = null;
        for (Camera.Size pictureSize : pictureSizeList) {
            if (bestPictureSize == null ||
                    (pictureSize.height >= bestPictureSize.height &&
                            pictureSize.width >= bestPictureSize.width)) {
                bestPictureSize = pictureSize;
            }
        }
        return bestPictureSize;
    }

    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public static boolean isContinuousFocusModeSupported(List<String> supportedFocusModes) {
        if (supportedFocusModes != null && !supportedFocusModes.isEmpty()) {
            for (String focusMode : supportedFocusModes) {
                if (focusMode != null && focusMode.equalsIgnoreCase(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, cameraInfo);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
        }
        int orientation = 0;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (cameraInfo.orientation + degree) % 360;
            orientation = (360 - orientation) % 360;
        } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            orientation = (cameraInfo.orientation - degree + 360) % 360;
        }
        return orientation;
    }

    public static void setImageOrientation(File file, int orientation) {
        if (file != null) {
            try {
                ExifInterface exifInterface = new ExifInterface(file.getPath());
                String orientationValue = String.valueOf(getOrientationExifValue(orientation));
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orientationValue);
                exifInterface.saveAttributes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getOrientationExifValue(int orientation) {
        switch (orientation) {
            case 90:
                return ExifInterface.ORIENTATION_ROTATE_90;
            case 180:
                return ExifInterface.ORIENTATION_ROTATE_180;
            case 270:
                return ExifInterface.ORIENTATION_ROTATE_270;
            default:
                return ExifInterface.ORIENTATION_NORMAL;
        }
    }

    public static File savePicture(String title) {
        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        filename = title+dateformat.format(date)+".jpg";
        takentime = dateformat.format(date);
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(filePath + "/" + filename);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytedata);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setData(byte[] imagedata) {
        bytedata = imagedata;
    }

    public static void updateMediaScanner(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }
}