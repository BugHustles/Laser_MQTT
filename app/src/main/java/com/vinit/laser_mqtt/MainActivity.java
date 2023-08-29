package com.vinit.laser_mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MqttManager.MqttConnectionCallback {
    String host="broker.hivemq.com";
    int port=1883;

    private MqttManager mqttManager;
    private String clientId = "MyAndroidClientId" + System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectToBroker(host,port);
    }
    private void connectToBroker(String host1, int port1) {
        mqttManager = MqttManager.getInstance(this, host1, port1,  this);
    }




        @Override
    public void onConnectionFailed() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                }
            });

    }

    @Override
    public void onConnectionFailed(Throwable cause) {

    }

    @Override
    public void onDisconnected() {

    }
}