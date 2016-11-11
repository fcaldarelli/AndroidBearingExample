package it.negusweb.bearingexample;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements BearingSensor.BearingSensorListener {

    private TextView txtAngle;
    private BearingSensor bearingSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAngle = (TextView)findViewById(R.id.txtAngle);

        bearingSensor = new BearingSensor(this, this);
    }

    @Override
    public void onBearingSensorAngleChanged(int azimuth) {
        txtAngle.setText(String.format("%d degrees", azimuth));
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
