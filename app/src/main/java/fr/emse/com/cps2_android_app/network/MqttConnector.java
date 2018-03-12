package fr.emse.com.cps2_android_app.network;

import android.app.Activity;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.FileInputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by julien on 20/01/18.
 */

public class MqttConnector {

    public static MqttAndroidClient createMqttClient(Activity activity){

        MqttAndroidClient client = null;
        String mqtt_host;

        // Try to read the MQTT host in the file "hosts"
        try {
            FileInputStream fin = activity.getApplicationContext().openFileInput("hosts");
            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            mqtt_host = temp.contains(";") ? temp.split(";")[0] : temp;
        } catch (IOException e) {
            e.printStackTrace();
            return client;
        }

        final String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(activity.getApplicationContext(),
                "tcp://" + mqtt_host + ":1883",
                clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "Mqtt client successfully connected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "Failed to connect the mqtt client");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return client;
    }
}
