package it.mirea.aivazyan_ikbo0620;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CameraCaptureSession myCameraCaptureSession;
    private String myCameraID;
    private CameraManager myCameraManager;
    private CameraDevice myCameraDevice;
    private TextureView myTextureView;
    private CaptureRequest.Builder myCaptureRequestBuilder;

    Button notifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        myTextureView = findViewById(R.id.textureView);
        myCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        openCamera();


        notifyBtn = findViewById(R.id.notify_btn);

        notifyBtn.setOnClickListener(view -> {
            //notification code goes here
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "My Notification");
            builder.setContentTitle("Student notification");
            builder.setContentText("Hello from Mikhail Aivazyan IKBO-06-20");
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
            managerCompat.notify(1, builder.build());

        });
    }

    private CameraDevice.StateCallback myStateCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            myCameraDevice = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            myCameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            myCameraDevice.close();
            myCameraDevice = null;
        }
    };

    private void openCamera() {
        try {
            myCameraID = myCameraManager.getCameraIdList()[0];

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myCameraManager.openCamera(myCameraID, myStateCallBack, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cameraPreview(View view) {
        SurfaceTexture mySurfaceTexture = myTextureView.getSurfaceTexture();
        Surface mySurface = new Surface(mySurfaceTexture);

        try {
            myCaptureRequestBuilder = myCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            myCaptureRequestBuilder.addTarget(mySurface);

            myCameraDevice.createCaptureSession(Arrays.asList(mySurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    myCameraCaptureSession = session;
                    myCaptureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    try {
                        myCameraCaptureSession.setRepeatingRequest(myCaptureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
}