package com.example.stepcount;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private TextView txtStep;
    private Button btnPause;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int steps = 0;
    private boolean isPause = false;

    private float lastX;
    private float lastY;
    private float lastZ;

    private long lastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStep = findViewById(R.id.txtStep);
        btnPause = findViewById(R.id.btnPause);

        sensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);

        accelerometer =
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnPause.setOnClickListener(v -> {

            isPause = !isPause;

            if (isPause)
                btnPause.setText("Продолжить");
            else
                btnPause.setText("Пауза");

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isPause)
            return;

        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastTime) > 100) {

            long diff = currentTime - lastTime;
            lastTime = currentTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float speed =
                    Math.abs(x + y + z - lastX - lastY - lastZ)
                            / diff * 10000;

            if (speed > 300) {

                steps++;

                txtStep.setText(String.valueOf(steps));
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}