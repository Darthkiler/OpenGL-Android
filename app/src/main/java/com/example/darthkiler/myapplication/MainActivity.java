package com.example.darthkiler.myapplication;


import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    SensorManager sensorManager;
    Sensor sensorAccel;
    Sensor sensorLinAccel;

    StringBuilder sb = new StringBuilder();
    OpenGLRenderer openGLRenderer;

    Timer timer;
    Sensor sensorGravity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLinAccel = sensorManager
                .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        if (!supportES2()) {
            Toast.makeText(this, "OpenGl ES 2.0 is not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);
        //glSurfaceView.setRenderer(new OpenGLRenderer());
        openGLRenderer=new OpenGLRenderer(this);

        glSurfaceView.setRenderer(openGLRenderer );
        setContentView(glSurfaceView);
        final Context context=this;
        glSurfaceView.setOnTouchListener(new OnSwipeTouchListener(this)
        {
            @Override
            public void onSwipeLeft(float diffX,float diffY, float x, float y) {
                super.onSwipeLeft(diffX,diffY, x, y);
                openGLRenderer.centerX+=diffX/100;
                openGLRenderer.centerY-=diffY/50;

            }

            @Override
            public void onSwipeRight(float diffX,float diffY, float x, float y) {
                super.onSwipeRight(diffX,diffY, x, y);
                openGLRenderer.centerX+=diffX/100;
                openGLRenderer.centerY-=diffY/50;
                //Log.d("My",diffX+"qwe"+diffY);


            }


        });
        //Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();


        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        sensorManager.registerListener(listener, sensorAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorLinAccel,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(listener, sensorGravity,
                SensorManager.SENSOR_DELAY_NORMAL);



        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showInfo();
                    }
                });
            }
        };
        timer.schedule(task, 0, 50);
    }

    void showInfo() {
        if(Math.abs(openGLRenderer.eyeX-valuesAccel[0]/2)>0.3f)
        openGLRenderer.eyeX=valuesAccel[0]/2;
        if(Math.abs(openGLRenderer.eyeZ-valuesAccel[2]/2)>0.3f)
        openGLRenderer.eyeZ=valuesAccel[2]/2;
        if(Math.abs(openGLRenderer.eyeY-valuesAccel[1]/2)>0.3f)
        openGLRenderer.eyeY=valuesAccel[1]/2;

        /*sb.setLength(0);
        sb.append("Accelerometer: " + format(valuesAccel))
                .append("\n\nAccel motion: " + format(valuesAccelMotion))
                .append("\nAccel gravity : " + format(valuesAccelGravity))
                .append("\n\nLin accel : " + format(valuesLinAccel))
                .append("\nGravity : " + format(valuesGravity));
        Log.d("My",sb.toString());*/
    }

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }

    private boolean supportES2() {

        try {
            ActivityManager activityManager =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            return (configurationInfo.reqGlEsVersion >= 0x20000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }



    float[] valuesAccel = new float[3];
    float[] valuesAccelMotion = new float[3];
    float[] valuesAccelGravity = new float[3];
    float[] valuesLinAccel = new float[3];
    float[] valuesGravity = new float[3];

    SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        valuesAccel[i] = event.values[i];
                        valuesAccelGravity[i] = (float) (0.1 * event.values[i] + 0.9 * valuesAccelGravity[i]);
                        valuesAccelMotion[i] = event.values[i]
                                - valuesAccelGravity[i];
                    }
                    break;
                case Sensor.TYPE_LINEAR_ACCELERATION:
                    for (int i = 0; i < 3; i++) {
                        valuesLinAccel[i] = event.values[i];
                    }
                    break;
                case Sensor.TYPE_GRAVITY:
                    for (int i = 0; i < 3; i++) {
                        valuesGravity[i] = event.values[i];
                    }
                    break;

                    //case Sensor.
            }

        }

    };


}


