package com.bluetooth.rutkoski.java_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bluetooth.rutkoski.unitybluetoothadapter.ArduinoBluetoothAdapter;

public class MainActivity extends AppCompatActivity {

    private ArduinoBluetoothAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArduinoBluetoothAdapter("00:21:13:01:CA:9F");
        adapter.Connect();
        adapter.Send("<who>");
    }
}
