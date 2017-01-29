package info.benallen.jeeves.jeevescontroller;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by maartie0 on 28/01/2017.
 */

public class Compass implements SensorEventListener {

    private float direction;

    SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;

    Compass(MainActivity mainActivity){
        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];

        sensorManager = (SensorManager) mainActivity.getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sensorManager.registerListener(this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorMagneticField,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public float getDirection() {
        return direction;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        switch(event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                for(int i =0; i < 3; i++){
                    valuesAccelerometer[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for(int i =0; i < 3; i++){
                    valuesMagneticField[i] = event.values[i];
                }
                break;
        }

        boolean success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                valuesAccelerometer,
                valuesMagneticField);

        if(success){
            SensorManager.getOrientation(matrixR, matrixValues);
            float normal_dir = matrixValues[0] % (float) Math.PI * 2;
            if(matrixValues[0] < 0){
                matrixValues[0] = ((float) Math.PI * 2)  + matrixValues[0];
            }

            direction = matrixValues[0] * (180 /(float) Math.PI);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
