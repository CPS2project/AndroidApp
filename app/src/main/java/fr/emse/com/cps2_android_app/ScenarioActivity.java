package fr.emse.com.cps2_android_app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import fr.emse.com.cps2_android_app.adapter.ScenarioListAdapter;
import fr.emse.com.cps2_android_app.fakeData.FakeScenariosArray;
import fr.emse.com.cps2_android_app.network.AsyncResponse;
import fr.emse.com.cps2_android_app.network.DownloadMongoDocuments;
import fr.emse.com.cps2_android_app.network.MqttConnector;

public class ScenarioActivity extends NavigationHelperActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse, AdapterView.OnItemSelectedListener {

    private DownloadMongoDocuments asyncTask = new DownloadMongoDocuments();
    private Spinner spinner = null;
    private ArrayList<JSONObject> scenarios;
    private JSONObject selectedScenario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_scenario);
        super.onCreate(savedInstanceState);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_scenario);

        // Initialize the spinner with will contain the name of the scenarios
        this.spinner = findViewById(R.id.spinner);
        this.spinner.setOnItemSelectedListener(this);

        // The next two lines fill the listView with real data from MongoDB
        //asyncTask.delegate = this;
        //asyncTask.execute("scenarios");

        // The next bloc fills the listView with fake data (offline test)
        try {
            this.processFinish(FakeScenariosArray.fakeScenariosArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final MqttAndroidClient mqttClient = MqttConnector.createMqttClient(this);

        Button button = findViewById(R.id.scenarioButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = "Scenario";
                String payload = "";
                try {
                    payload = ScenarioActivity.this.selectedScenario.getString("scenario_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                byte[] encodedPayload;
                try {
                    encodedPayload = payload.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    mqttClient.publish(topic, message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ScenarioActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void processFinish(ArrayList<JSONObject> output) throws JSONException {

        // Fill the spinner
        ArrayList<String> scenarioNames = new ArrayList<>();
        for (JSONObject j : output) {
            scenarioNames.add(j.getString("scenario_name"));
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, scenarioNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(spinnerAdapter);

        // Store the scenarios in an attribute to access it later
        this.scenarios = output;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Retrieve the selected scenario in the list and send it to another method to display the details
        JSONObject scenario = this.scenarios.get(i);
        this.selectedScenario = scenario;
        try {
            this.fillListView(scenario);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void fillListView(JSONObject scenario) throws JSONException {
        // Display the name of the scenario in a textView
        TextView textView = findViewById(R.id.scenarioNameTextView);
        textView.setText(scenario.getString("scenario_name"));

        // Fill the listView with the details of the selected scenario
        ListView listView = findViewById(R.id.scenarioListView);
        JSONArray jsonArray = scenario.getJSONArray("configs");
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        int i;
        for (i = 0; i < jsonArray.length(); i++) {
            jsonObjects.add(jsonArray.getJSONObject(i));
        }
        ScenarioListAdapter adapter = new ScenarioListAdapter(ScenarioActivity.this, jsonObjects);
        listView.setAdapter(adapter);
    }
}
