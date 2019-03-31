package com.iitr.mdg.claimit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements SensorEventListener {

    float x,y,z;
    private float[] mGravity = { 0.0f, 0.0f, 0.0f };
    private float[] mLinearAcceleration = { 0.0f, 0.0f, 0.0f };
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    long startTime = 0;
    private float Current = 0.0f;
    int moveCount = 0;
    // Minimum acceleration needed to count as a shake movement
    private static final int MIN_SHAKE_ACCELERATION = 10;

    // Minimum number of movements to register a shake
    private static final int MIN_MOVEMENTS = 1;

    // Maximum time (in milliseconds) for the whole shake to occur
    private static final int MAX_SHAKE_DURATION = 500;

    int quality = 0;
    int rate = 100;
    String timeStampFile;
    Timer timer;
    int VideoFrameRate = 24;
    LocationListener locationListener;
    LocationManager LM;
    boolean recording = false;
    Location location;
    LocationManager lm;
    double latitude = 0;
    double longitude = 0;
    double latitude_original = 0;
    double longitude_original = 0;
    float speed = 0;
    float dist[] = {0, 0, 0};
    PrintWriter writer = null;

    String[] options = {"1080p", "720p", "480p"};
    String[] options1 = {"15 Hz", "10 Hz"};
    String[] options2 = {"10 fps", "20 fps", "30 fps"};

    /* --------------------- Data Section ----------------------------*/
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private ImageButton capture, vid;
    private Context myContext;
    private FrameLayout cameraPreview;
    private Chronometer chrono;
    private TextView tv;
    private TextView txt;
    OnClickListener captureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Timer timer = new Timer ();

            if (recording) {
                Log.e("rec,", "end");
                // stop recording and release camera
                timer.cancel();
                timer.purge();
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();
                recording = false;
                chrono.stop();
                chrono.setBase(SystemClock.elapsedRealtime());

                chrono.start();
                chrono.stop();
                txt.setTextColor(-16711936);
                enddata();

            } else {
                Log.e("rec", "start");
                Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_LONG).show();


                TimerTask hourlyTask = new TimerTask () {
                    @Override
                    public void run() {
                        // your code here...
                        timeStampFile = String.valueOf((new Date()).getTime());
                        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/claimit/");
                        wallpaperDirectory.mkdirs();

                        File wallpaperDirectory1 = new File(Environment.getExternalStorageDirectory().getPath() + "/claimit/" + "data");
                        wallpaperDirectory1.mkdirs();
                        if (!prepareMediaRecorder()) {
                            Toast.makeText(MainActivity.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        try {
                            mediaRecorder.start();
                        } catch (final Exception ex) {}

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI

                                Camera.Parameters params = mCamera.getParameters();
                                params.setPreviewFpsRange(30000, 30000); // 30 fps
                                if (params.isAutoExposureLockSupported())
                                    params.setAutoExposureLock(true);

                                //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                //mCamera.setParameters(params);
                                //d.beginData();
                                storeData();
                                chrono.setBase(SystemClock.elapsedRealtime());

                                chrono.start();
                                //chrono.setBackgroundColor(-65536);
                                txt.setTextColor(-65536);
                                recording = true;

                            }
                        });


                        }
                    };
                timer.schedule(hourlyTask,01,30000);
                }


                }

        };

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyro;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        capture = (ImageButton) findViewById(R.id.button_capture);
        capture.setOnClickListener(captureListener);

        chrono = (Chronometer) findViewById(R.id.chronometer);
        txt = (TextView) findViewById(R.id.txt1);
        txt.setTextColor(-16711936);

        vid = (ImageButton) findViewById(R.id.imageButton);
        vid.setVisibility(View.GONE);
    }


    /* ---------------------- Sensor data ------------------- */

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!checkCameraHardware(myContext)) {
            Toast toast = Toast.makeText(myContext, "Phone doesn't have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            mCamera = Camera.open(findBackFacingCamera());
            mPreview.refreshCamera(mCamera);
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                if (location.hasSpeed()) {
                    speed = location.getSpeed();
                }
                location.distanceBetween(latitude_original, longitude_original, latitude, longitude, dist);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Acquire a reference to the system Location Manager
        LM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    float gyro_x = 0;
    float gyro_y = 0;
    float gyro_z = 0;


    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
        sensorManager.unregisterListener(this);

    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (quality == 0)
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
        else if (quality == 1)
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
        else if (quality == 2)
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));

        mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/claimit/" + "data" + "/" + "video" + ".mp4");
        mediaRecorder.setVideoFrameRate(VideoFrameRate);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void storeData() {

        String filePath = Environment.getExternalStorageDirectory().getPath() + "/claimit/" + "data" + "/" + "sensor_data" + ".csv";
        try {
            writer = new PrintWriter(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writer.println("Timestamp" + "," +
                "Longitude" + "," + "Latitude" + "," +
                "Acc x" + "," + "Acc Y" + "," + "Acc Z" + "," + "gyro_x" + "," + "gyro_y" + "," + "gyro_z");
        LocationManager original = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location original_location = original.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (original.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            latitude_original = original_location.getLatitude();
            longitude_original = original_location.getLongitude();
        }

        timer = new Timer();
        timer.schedule(new SayHello(), 0, rate);
    }

    public void enddata() {
        writer.close();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // This method will be called when the accelerometer detects a change.

        // Call a helper method that wraps code from the Android developer site
        setCurrentAcceleration(event);
        // Get the max linear acceleration in any direction
        float maxLinearAcceleration = getMaxCurrentLinearAcceleration();

        // Check if the acceleration is greater than our minimum threshold
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            long now = System.currentTimeMillis();

            // Set the startTime if it was reset to zero
            if (startTime == 0) {
                startTime = now;
            }

            long elapsedTime = now - startTime;

            // Check if we're still in the shake window we defined
            if (elapsedTime > MAX_SHAKE_DURATION) {
                // Too much time has passed. Start over!
                resetShakeDetection();
            } else {
                // Keep track of all the movements
                moveCount++;

                // Check if enough movements have been made to qualify as a shake
                if (moveCount > MIN_MOVEMENTS) {
                    // It's a shake! Notify the listener.
                   // mShakeListener.onShake();

                    // Reset for the next one!
                    resetShakeDetection();
                }
            }
        }
    }

        public void addQuality (View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String setting = new String();
            if (quality == 0) {
                setting = "1080p";
            } else if (quality == 1) {
                setting = "720p";
            } else if (quality == 2) {
                setting = "480p";
            }
            builder.setTitle("Pick Quality, Current setting: " + setting)
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if (which == 0) {
                                quality = 0;
                            } else if (which == 1) {
                                quality = 1;
                            } else if (which == 2) {
                                quality = 2;
                            }
                        }
                    });
            builder.show();
        }

        public void addRate (View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String setting = new String();
            if (rate == 100) {
                setting = "10 Hz";
            } else if (rate == 67) {
                setting = "15 Hz";
            }
            builder.setTitle("Pick Data Save Rate, Current setting: " + setting)
                    .setItems(options1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if (which == 0) {
                                rate = 67;
                            } else if (which == 1) {
                                rate = 100;
                            }
                        }
                    });
            builder.show();
        }

        public void addFrameRate (View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String setting = new String();
            if (VideoFrameRate == 10) {
                setting = "10 fps";
            } else if (VideoFrameRate == 20) {
                setting = "20 fps";
            } else if (VideoFrameRate == 30) {
                setting = "30 fps";
            }
            builder.setTitle("Pick Video fps, Current setting: " + setting)
                    .setItems(options2, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if (which == 0) {
                                VideoFrameRate = 10;
                            } else if (which == 1) {
                                VideoFrameRate = 20;
                            } else if (which == 2) {
                                VideoFrameRate = 30;
                            }
                        }
                    });
            builder.show();
        }

    private void setCurrentAcceleration(SensorEvent event) {
        final float alpha = 0.8f;

        // Gravity components of x, y, and z acceleration
        mGravity[X] = alpha * mGravity[X] + (1 - alpha) * event.values[X];
        mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * event.values[Y];
        mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * event.values[Z];

        // Linear acceleration along the x, y, and z axes (gravity effects removed)
        mLinearAcceleration[X] = event.values[X] - mGravity[X];
        mLinearAcceleration[Y] = event.values[Y] - mGravity[Y];
        mLinearAcceleration[Z] = event.values[Z] - mGravity[Z];

        Current = Math.round(Math.sqrt(mLinearAcceleration[X] * mLinearAcceleration[X] + mLinearAcceleration[Y] * mLinearAcceleration[Y] + mLinearAcceleration[Z] * mLinearAcceleration[Z]));
    }

    private float getMaxCurrentLinearAcceleration() {
        // Start by setting the value to the x value
        float maxLinearAcceleration = Current;

        // Return the greatest value
        return maxLinearAcceleration;
    }

    private void resetShakeDetection() {
        startTime = 0;
        moveCount = 0;
    }


    class SayHello extends TimerTask {
            public void run() {
                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                String timeStamp = String.valueOf((new Date()).getTime());
                writer.println(timeStamp + "," +
                        longitude_original + "," + latitude_original + "," +
                        x + "," + y + "," + z + "," + gyro_x + "," + gyro_y + "." + gyro_z);
            }
        }
}

