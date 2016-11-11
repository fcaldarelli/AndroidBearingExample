package it.negusweb.bearingexample;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Fabrizio on 11/11/16.
 */

public class BearingSensor {

    private Context context;
    private BearingSensorListener listener;

    private SensorManager mSensorManager;
    private int mAzimuth = 0; // degree

    private Sensor mGravity;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    /**
     * Sensor event listener
     */
    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        float[] gData = new float[3]; // gravity or accelerometer
        float[] lastAccelerometerValue = new float[3]; // gravity or accelerometer

        float[] mData = new float[3]; // magnetometer
        float[] rMat = new float[9];
        float[] iMat = new float[9];
        float[] orientation = new float[3];

        public void onAccuracyChanged( Sensor sensor, int accuracy ) {}

        @Override
        public void onSensorChanged( SensorEvent event ) {
            float[] data;
            switch ( event.sensor.getType() ) {
                case Sensor.TYPE_GRAVITY:
                    gData = event.values.clone();
                    lastAccelerometerValue = event.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    gData = event.values.clone();
                    lastAccelerometerValue = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mData = event.values.clone();
                    break;
                default: return;
            }

            if ( SensorManager.getRotationMatrix( rMat, iMat, gData, mData ) ) {

                Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                int screenRotation = display.getRotation();
                int axisX, axisY;
                boolean isUpSideDown = lastAccelerometerValue[2] < 0;

                switch (screenRotation) {
                    case Surface.ROTATION_0:
                        axisX = (isUpSideDown ? SensorManager.AXIS_MINUS_X : SensorManager.AXIS_X);
                        axisY = (Math.abs(lastAccelerometerValue[1]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_MINUS_Z : SensorManager.AXIS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_MINUS_Y : SensorManager.AXIS_Y));
                        break;
                    case Surface.ROTATION_90:
                        axisX = (isUpSideDown ? SensorManager.AXIS_MINUS_Y : SensorManager.AXIS_Y);
                        axisY = (Math.abs(lastAccelerometerValue[0]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_Z : SensorManager.AXIS_MINUS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_X : SensorManager.AXIS_MINUS_X));
                        break;
                    case  Surface.ROTATION_180:
                        axisX = (isUpSideDown ? SensorManager.AXIS_X : SensorManager.AXIS_MINUS_X);
                        axisY = (Math.abs(lastAccelerometerValue[1]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_Z : SensorManager.AXIS_MINUS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_Y : SensorManager.AXIS_MINUS_Y));
                        break;
                    case Surface.ROTATION_270:
                        axisX = (isUpSideDown ? SensorManager.AXIS_Y : SensorManager.AXIS_MINUS_Y);
                        axisY = (Math.abs(lastAccelerometerValue[0]) > 6.0f ?
                                (isUpSideDown ? SensorManager.AXIS_MINUS_Z : SensorManager.AXIS_Z) :
                                (isUpSideDown ? SensorManager.AXIS_MINUS_X : SensorManager.AXIS_X));
                        break;
                    default:
                        axisX = (isUpSideDown ? SensorManager.AXIS_MINUS_X : SensorManager.AXIS_X);
                        axisY = (isUpSideDown ? SensorManager.AXIS_MINUS_Y : SensorManager.AXIS_Y);
                }

                float[] rotationMatrix = new float[9];
                SensorManager.remapCoordinateSystem(rMat, axisX, axisY, rotationMatrix);



                mAzimuth= (int) ( Math.toDegrees( SensorManager.getOrientation( rotationMatrix, orientation )[0] ) + 360 ) % 360;

                if(listener!=null)
                {
                    listener.onBearingSensorAngleChanged(mAzimuth);
                }
                Log.i("Azimuth", "angle = "+mAzimuth);
            }
        }
    };

    public BearingSensor(Context contextInput, BearingSensorListener listenerInput)
    {
        context = contextInput;
        listener = listenerInput;

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    }

    public void start()
    {
        boolean haveGravity;
        boolean haveAccelerometer;
        boolean haveMagnetometer;

        this.mGravity = this.mSensorManager.getDefaultSensor( Sensor.TYPE_GRAVITY );
        haveGravity = this.mSensorManager.registerListener( mSensorEventListener, this.mGravity, SensorManager.SENSOR_DELAY_GAME );

        this.mAccelerometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        haveAccelerometer = this.mSensorManager.registerListener( mSensorEventListener, this.mAccelerometer, SensorManager.SENSOR_DELAY_GAME );

        this.mMagnetometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
        haveMagnetometer = this.mSensorManager.registerListener( mSensorEventListener, this.mMagnetometer, SensorManager.SENSOR_DELAY_GAME );

        // if there is a gravity sensor we do not need the accelerometer
        if( haveGravity )
            this.mSensorManager.unregisterListener( this.mSensorEventListener, this.mAccelerometer );

        /*
        if ( ( haveGravity || haveAccelerometer ) && haveMagnetometer ) {
            // ready to go
        } else {
            // unregister and stop
        }
        */
    }

    public void stop()
    {
        this.mSensorManager.unregisterListener(this.mSensorEventListener);
    }


    /**
     * Bearing Sensor Listener
     */
    public interface BearingSensorListener
    {
        void onBearingSensorAngleChanged(int azimuth);
    }


}
