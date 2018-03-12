package fr.emse.com.cps2_android_app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import fr.emse.com.cps2_android_app.network.DownloadMongoDocuments;
import fr.emse.com.cps2_android_app.network.JsonAsyncResponse;
import fr.emse.com.cps2_android_app.network.MqttConnector;


public class QueryActivity extends NavigationHelperActivity implements JsonAsyncResponse {

    private ArrayList<String> buildingData = new ArrayList<>();
    private ArrayList<String> floorData = new ArrayList<>();
    private ArrayList<String> roomData = new ArrayList<>();
    private ArrayList<String> objectTypeData = new ArrayList<>();
    private ArrayList<String> objectNameData = new ArrayList<>();
    private ArrayList<String> parameterName = new ArrayList<>();

    private ArrayList<JSONObject> objects;
    private ArrayAdapter<String> parameterNameSpinnerAdapter;

    private MqttAndroidClient mqttClient;

    DownloadMongoDocuments asyncTask = new DownloadMongoDocuments();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_query);
        super.onCreate(savedInstanceState);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_query);

        // Try to read the MongoDB host in the hosts file
        try {
            FileInputStream fin = openFileInput("hosts");
            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }

            // If succeed, get the objects from MongoDB using the retrieved IP address.
            String mongo_host = temp.contains(";") ? temp.split(";")[1] : temp;
            asyncTask.delegate = this;
            asyncTask.execute("objects", mongo_host);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // The next line fills the listView with fake data (offline test)
//        this.processFinish(FakeObjectsArray.fakeObjectsArray());

        this.mqttClient = MqttConnector.createMqttClient(this);

    }

    public void onRadioButtonClicked(View view) throws JSONException {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        if (checked){
            switch(view.getId()) {
                case R.id.radioButtonField:
                    this.updateParameterSpinner("fields");
                    break;
                case R.id.radioButtonConfig:
                    this.updateParameterSpinner("config");
                    break;
                case R.id.radioButtonRequest:
                    findViewById(R.id.newValue).setEnabled(false);
                    break;
                case R.id.radioButtonChange:
                    findViewById(R.id.newValue).setEnabled(true);
                    break;
            }
        }
    }

    private void updateParameterSpinner(String parameterType) throws JSONException {
        // Empty the ArrayList of the spinner fields
        this.parameterName.clear();

        // Get either the different fields of configs from the objects array
        for (JSONObject obj: this.objects) {
            JSONObject values = obj.getJSONObject(parameterType).getJSONObject("values");
            JSONArray valuesNames = values.names();

            for (int i = 0; i < valuesNames.length() ; i++){
                String label = values.getJSONObject(valuesNames.get(i).toString()).getString("label");
                if (!this.parameterName.contains(label)){
                    this.parameterName.add(label);
                }
            }
        }
        // Update the adapter
        this.parameterNameSpinnerAdapter.notifyDataSetChanged();
    }

    public void onSendingButtonClicked(View view) throws JSONException {
        // Retrieve the targets and request type to make the topic
        String building = ((Spinner) findViewById(R.id.buildingSpinner)).getSelectedItem().toString();
        String floor = ((Spinner) findViewById(R.id.floorSpinner)).getSelectedItem().toString();
        String room = ((Spinner) findViewById(R.id.roomSpinner)).getSelectedItem().toString();
        String objectType = ((Spinner) findViewById(R.id.objectTypeSpinner)).getSelectedItem().toString();
        String objectName = ((Spinner) findViewById(R.id.objectNameSpinner)).getSelectedItem().toString();
        String requestType = ((RadioButton) findViewById(R.id.radioButtonRequest)).isChecked() ? "request" : "change";

        String topic = String.format("%s/%s/%s/%s/%s/%s", building, floor, room, objectType, objectName, requestType);

        System.out.println("### topic: " + topic);

        // Retrieve the information to make the message
        String parameterType = ((RadioButton) findViewById(R.id.radioButtonConfig)).isChecked() ? "config" : "field";
        String parameterLabel = ((Spinner) findViewById(R.id.parameterNameSpinner)).getSelectedItem().toString();

        // We must get the name of the selected parameter and not its displayed label
        String parameterName = this.getParameterNameFromLabel(parameterLabel);
        String clientId = this.mqttClient.getClientId();
        String payload = String.format("%s,%s", parameterType, parameterName);
        if (((RadioButton) findViewById(R.id.radioButtonRequest)).isChecked()){
            // This is a request. The third element of the message is the client ID
            payload += "," + clientId;
        } else {
            // This is a change command. The third element of the message is the new value
            payload += "," + ((TextView) findViewById(R.id.newValue)).getText();
        }

        System.out.println("### message: " + payload);

        byte[] encodedPayload;
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            this.mqttClient.publish(topic, message);
            this.mqttClient.subscribe("+/+/+/+/+/answer/" + clientId, 1);
            this.mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println(new String(message.getPayload()));
                    displayResponses(topic, new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
        Toast.makeText(QueryActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
        // Empty the textView of the responses
        ((TextView) findViewById(R.id.responses)).setText("");
    }

    private void displayResponses(String topic, String message) {
        TextView textView = findViewById(R.id.responses);
        textView.setText(textView.getText() + topic.split("/")[4] + ": " + message + "\n");
    }

    private String getParameterNameFromLabel(String label) throws JSONException {
        for (JSONObject obj: this.objects) {
            JSONObject values = obj.getJSONObject(((RadioButton) findViewById(R.id.radioButtonConfig)).isChecked() ? "config" : "fields").getJSONObject("values");
            JSONArray valuesNames = values.names();
            for (int i = 0; i < valuesNames.length() ; i++){
                String parameterName = valuesNames.get(i).toString();
                if (values.getJSONObject(parameterName).getString("label").equals(label)){
                    return parameterName;
                }
            }
        }
        return "";
    }

    @Override
    public void processFinish(ArrayList<JSONObject> output) throws JSONException {
        this.objects = output;

        this.buildingData.add("All");
        this.floorData.add("All");
        this.roomData.add("All");
        this.objectTypeData.add("All");
        this.objectNameData.add("All");

        for (JSONObject object : output){

            String first = object.getJSONObject("building").getString("value");
            String second = object.getJSONObject("floor").getString("value");
            String third = object.getJSONObject("room").getString("value");
            String fourth = object.getJSONObject("object_type").getString("value");
            String fifth = object.getJSONObject("object_name").getString("value");

            if (!this.buildingData.contains(first)) {
                this.buildingData.add(first);
            }
            if (!this.floorData.contains(second)) {
                this.floorData.add(second);
            }
            if (!this.roomData.contains(third)) {
                this.roomData.add(third);
            }
            if (!this.objectTypeData.contains(fourth)) {
                this.objectTypeData.add(fourth);
            }
            if (!this.objectNameData.contains(fifth)) {
                this.objectNameData.add(fifth);
            }

            // Fill the last spinner with config by default
            JSONObject values = object.getJSONObject("config").getJSONObject("values");
            JSONArray valuesNames = values.names();

            for (int i = 0; i < valuesNames.length() ; i++){
                String label = values.getJSONObject(valuesNames.get(i).toString()).getString("label");
                if (!this.parameterName.contains(label)){
                    this.parameterName.add(label);
                }
            }
        }

        ArrayAdapter<String> firstLevelSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, buildingData);
        firstLevelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> secondLevelSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, floorData);
        secondLevelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> thirdLevelSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, roomData);
        thirdLevelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> fourthLevelSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, objectTypeData);
        fourthLevelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> fifthLevelSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, objectNameData);
        fifthLevelSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.parameterNameSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, parameterName);
        parameterNameSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        ((Spinner) findViewById(R.id.buildingSpinner)).setAdapter(firstLevelSpinnerAdapter);
        ((Spinner) findViewById(R.id.floorSpinner)).setAdapter(secondLevelSpinnerAdapter);
        ((Spinner) findViewById(R.id.roomSpinner)).setAdapter(thirdLevelSpinnerAdapter);
        ((Spinner) findViewById(R.id.objectTypeSpinner)).setAdapter(fourthLevelSpinnerAdapter);
        ((Spinner) findViewById(R.id.objectNameSpinner)).setAdapter(fifthLevelSpinnerAdapter);
        ((Spinner) findViewById(R.id.parameterNameSpinner)).setAdapter(this.parameterNameSpinnerAdapter);
    }
}
