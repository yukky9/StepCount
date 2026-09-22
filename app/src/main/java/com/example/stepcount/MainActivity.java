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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private TextView txtStep;
    private TextView txtCalories;
    private Button btnPause;
    private LineChart stepChart;
    private ArrayList<Entry> entries = new ArrayList<>();
    private int timePoint = 0;

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
        txtCalories = findViewById(R.id.txtCalories);
        btnPause = findViewById(R.id.btnPause);
        stepChart = findViewById(R.id.stepChart);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        updateCalories();
        updateChart();
        btnPause.setOnClickListener(v -> {
            isPause = !isPause;

            if (isPause) {
                btnPause.setText("Продолжить");
            } else {
                btnPause.setText("Пауза");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (accelerometer != null) {
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (isPause) return;

        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastTime) > 100) {

            long diff = currentTime - lastTime;
            lastTime = currentTime;

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float speed = Math.abs(x + y + z - lastX - lastY - lastZ)
                    / diff * 10000;

            if (speed > 300) {
                steps++;
                txtStep.setText(String.valueOf(steps));
                updateCalories();
                updateChart();
            }

            lastX = x;
            lastY = y;
            lastZ = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void updateCalories() {
        double calories = steps * 0.04;
        txtCalories.setText(String.format(
                Locale.US,
                "🔥 Калории: %.2f ккал",
                calories
        ));
    }

    private void updateChart() {

        entries.add(new Entry(timePoint++, steps));

        LineDataSet dataSet = new LineDataSet(entries, "Шаги");

        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(3f);

        LineData lineData = new LineData(dataSet);

        stepChart.setData(lineData);
        stepChart.invalidate();
    }
}