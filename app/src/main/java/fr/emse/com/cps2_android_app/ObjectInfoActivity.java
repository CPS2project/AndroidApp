package fr.emse.com.cps2_android_app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
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

import fr.emse.com.cps2_android_app.adapter.ObjectInfoListAdapter;
import fr.emse.com.cps2_android_app.adapter.ScenarioListAdapter;

public class ObjectInfoActivity extends NavigationHelperActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener{


    private Spinner spinner = null;
    private JSONObject selected_object;
    private static String[] object_info_titles = {"General Information","Configs", "Fields"};
    private ListView listView;
    private String selected_section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_object_info);
        super.onCreate(savedInstanceState);

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setCheckedItem(R.id.nav_objects);

        listView = findViewById(R.id.objectInfoListView);

        // Initialize the spinner with will contain the name of the scenarios
        this.spinner = findViewById(R.id.spinner_obj_info);
        this.spinner.setOnItemSelectedListener(this);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, object_info_titles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(spinnerAdapter);



        //Get JSON from the intent

        try {
            selected_object = new JSONObject(getIntent().getStringExtra("JSONOBJECT_SELECTED"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        /*ObjectsListAdapter adapter = new ObjectsListAdapter(ObjectInfoActivity.this, output);
        listView.setAdapter(adapter);*/

    }

    private void fillListView(JSONObject object) throws JSONException {
        // Display the name of the scenario in a textView
        TextView textView = findViewById(R.id.objectInfoNameTextView);
        textView.setText(selected_section);

        ArrayList<JSONObject> jsonObjects = new ArrayList<>();

        int i;

        // Fill the listView with the details of the selected scenario
        if(("General Information").equals(selected_section)){
            JSONArray jsonArray = selected_object.names();
            for (i = 0; i < jsonArray.length(); i++) {

                if (!jsonArray.get(i).equals("config") && !jsonArray.get(i).toString().equals("fields") && !jsonArray.get(i).toString().equals("_id")) {
                    jsonObjects.add(selected_object.getJSONObject(jsonArray.get(i).toString()));
                }

            }

            System.out.println("jdkjdkeje" + jsonObjects.toString());

        } else if (("Configs").equals(selected_section)){

            JSONObject values = selected_object.getJSONObject("config").getJSONObject("values");
            JSONArray jsonArray = values.names();

            for (i = 0; i < jsonArray.length(); i++) {
                jsonObjects.add(values.getJSONObject(jsonArray.get(i).toString()));
            }

        } else if (("Fields").equals(selected_section)){

            JSONObject values = selected_object.getJSONObject("fields").getJSONObject("values");
            JSONArray jsonArray = values.names();

            for (i = 0; i < jsonArray.length(); i++) {
                jsonObjects.add(values.getJSONObject(jsonArray.get(i).toString()));
            }

            System.out.println(jsonObjects.toString() + jsonObjects.get(0).getString("description"));

        }

        ObjectInfoListAdapter adapter = new ObjectInfoListAdapter(ObjectInfoActivity.this, jsonObjects);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Retrieve the selected scenario in the list and send it to another method to display the details

        this.selected_section = this.object_info_titles[i];;
        try {
            this.fillListView(selected_object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
