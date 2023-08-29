package com.vinit.laser_mqtt;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class MqttManager {
    private static MqttManager instance;
    private MqttAndroidClient mqttAndroidClient;

    public interface MqttConnectionCallback {


        void onConnectionFailed();

        void onConnectionFailed(Throwable cause);

        void onDisconnected();
    }

    public interface MqttMessageListener {
        void onMessageArrived(String topic, MqttMessage message);

        void onFailure(String topic, Exception e);

        void onSuccess(String topic, MqttMessage message);

        void onFailure();
    }

    public MqttManager(Context context, String host, int port,  MqttConnectionCallback callback) {
        String brokerUri = "tcp://" + host + ":" + port;
        String clientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(context, brokerUri, clientId);

        MqttConnectOptions options = new MqttConnectOptions();


        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                callback.onConnectionFailed();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Notifying the listener about the incoming message
                for (IMqttMessageListener listener : mqttMessageListeners) {
                    listener.messageArrived(topic, message);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Message delivery complete
            }
        });

        try {
            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    callback.onConnectionFailed();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            callback.onConnectionFailed();
        }
    }

    private List<IMqttMessageListener> mqttMessageListeners = new ArrayList<>();

    public static MqttManager getInstance(Context context, String host, int port, MqttConnectionCallback callback) {
        if (instance == null) {
            instance = new MqttManager(context, host, port,  callback);
        }
        return instance;
    }

    public void subscribe(String topic, int qos, MqttMessageListener listener) throws MqttException {
        mqttAndroidClient.subscribe(topic, qos, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                listener.onSuccess(topic, new MqttMessage());
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                listener.onFailure();
            }
        });

        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                // Handle connection loss
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                listener.onMessageArrived(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Message delivery complete
            }
        });
    }
}
