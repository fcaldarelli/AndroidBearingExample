# Android Bearing Example
Full example to calculate bearing in Android

I wrote this example to thank you to all users that have posted in interner some kind of solution to solve bearing calculation.

The "engine" is BearingSensor class that initializes sensors, get data from them and apply some formulas to received data:

## Sensors initialization
BearingSensor class initializes and uses these sensors: **gravity, accelerometer and magnetometer sensors.**

    this.mGravity = this.mSensorManager.getDefaultSensor( Sensor.TYPE_GRAVITY );
    haveGravity = this.mSensorManager.registerListener( mSensorEventListener, this.mGravity, SensorManager.SENSOR_DELAY_GAME );
    
    this.mAccelerometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
    haveAccelerometer = this.mSensorManager.registerListener( mSensorEventListener, this.mAccelerometer, SensorManager.SENSOR_DELAY_GAME );
    
    this.mMagnetometer = this.mSensorManager.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
    haveMagnetometer = this.mSensorManager.registerListener( mSensorEventListener, this.mMagnetometer, SensorManager.SENSOR_DELAY_GAME );
    
## Get orientation
BearingSensor class retrieves data using SensorManager listener:

    @Override
    public void onAccuracyChanged( Sensor sensor, int accuracy );

    @Override
    public void onSensorChanged( SensorEvent event );

onAccuracyChanged is not important, so its body is empty.

In **onSensorChanged** we get data from sensors and apply some formulas on them, using 

    SensorManager.getRotationMatrix
    
to obtain rotation matrix from sensor data, then

    SensorManager.remapCoordinateSystem
    
to apply a remapping of coordinate system due to device orientation, finally

    SensorManager.getOrientation
    
to compute the device's orientation based on the rotation matrix. 

## How to use BearingSensor class

To use BearingSensor you have to instantiate it passing a context instance and the listener that will receive bearing changes.

    public class MainActivity extends AppCompatActivity implements BearingSensor.BearingSensorListener {

      private BearingSensor bearingSensor;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          bearingSensor = new BearingSensor(this, this);
      }

      @Override
      public void onBearingSensorAngleChanged(int azimuth) {
          Log.i("BearingSensor", String.format("%d degrees", azimuth));
      }

      @Override
      public void onResume()
      {
          super.onResume();
          bearingSensor.start();
      }

      @Override
      public void onPause()
      {
          super.onPause();
          bearingSensor.stop();
      }
    }

The callback

    public void onBearingSensorAngleChanged(int azimuth);
    
contains *azimuth* parameter that indicates current angle from north pole in degrees.
