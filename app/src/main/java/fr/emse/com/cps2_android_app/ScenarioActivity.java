package fr.emse.com.cps2_android_app;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.emse.com.cps2_android_app.adapter.ResultAdapter;
import fr.emse.com.cps2_android_app.adapter.ScenarioListAdapter;
import fr.emse.com.cps2_android_app.fakeData.FakeScenariosArray;
import fr.emse.com.cps2_android_app.network.AsyncResponse;
import fr.emse.com.cps2_android_app.network.DownloadMongoDocuments;

public class ScenarioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse, AdapterView.OnItemSelectedListener {

    DownloadMongoDocuments asyncTask = new DownloadMongoDocuments();
    Spinner spinner = null;
    ArrayList<JSONObject> scenarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_scenario);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize the spinner with will contain the name of the scenarios
        this.spinner = findViewById(R.id.spinner);
        this.spinner.setOnItemSelectedListener(this);

        // The next two lines fill the listView with real data from MongoDB
//        asyncTask.delegate = this;
//        asyncTask.execute("scenarios");

        // The next bloc fills the listView with fake data (offline test)
        try {
            this.processFinish(FakeScenariosArray.fakeScenariosArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    /*
    * The following functions handle the navigation panel on the left
    */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_help) {
            Intent helpActivity = new Intent(ScenarioActivity.this, HelpActivity.class);
            startActivity(helpActivity);
        } else if (id == R.id.nav_objects) {
            Intent objectsActivity = new Intent(ScenarioActivity.this, ObjectsActivity.class);
            startActivity(objectsActivity);
        } else if (id == R.id.nav_query) {
            Intent queryActivity = new Intent(ScenarioActivity.this, QueryActivity.class);
            startActivity(queryActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
