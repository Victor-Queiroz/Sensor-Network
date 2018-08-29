package br.com.renanxavier.sd01;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLuz;
    private Sensor mProximidade;
    private TextView txValue;
    private TextView txIpDevice;
    private EditText txIp;

    private boolean run = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txValue = (TextView) findViewById(R.id.textViewValues);
        txIpDevice = findViewById(R.id.textIp);
        txIp = findViewById(R.id.editTextIp);

        txIp.setText("10.180.23.138");

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLuz = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip2 = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        txIpDevice.setText(ip2);

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                run = true;

                new Thread(new Runnable() {
                    public void run() {
                        while (run){
                            List<String> list = new ArrayList<>();
                            list.add(txValue.getText().toString());
                            list.add(txIpDevice.getText().toString());

                            try {
                                SendData send = new SendData();
                                send.setIp(txIp.getText().toString());
                                send.execute(list);
                                //Toast.makeText(this, "Dados enviados com sucesso!", Toast.LENGTH_SHORT).show();
                            } catch (Exception e){
                                Toast.makeText(MainActivity.this, "Erro:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            try {
                                Thread.sleep(3000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            }
        });

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float millibarsOfPressure = event.values[0];
        // Do something with this sensor data.
        txValue.setText(""+ millibarsOfPressure);
        Log.i("LOG", ""+ millibarsOfPressure);
    }


    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mLuz, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    public void send(View v){


        run = false;


    }
}
