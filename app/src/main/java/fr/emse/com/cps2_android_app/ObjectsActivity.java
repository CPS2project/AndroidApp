package fr.emse.com.cps2_android_app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.widget.ListView;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import fr.emse.com.cps2_android_app.adapter.ObjectsListAdapter;
import fr.emse.com.cps2_android_app.network.DownloadMongoDocuments;
import fr.emse.com.cps2_android_app.network.JsonAsyncResponse;

public class ObjectsActivity extends NavigationHelperActivity
        implements NavigationView.OnNavigationItemSelectedListener, JsonAsyncResponse {

    DownloadMongoDocuments asyncTask = new DownloadMongoDocuments();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_objects);
        super.onCreate(savedInstanceState);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_objects);

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
    }

    @Override
    public void processFinish(ArrayList<JSONObject> jsonArray) {
        ListView listView = findViewById(R.id.listView);
        ObjectsListAdapter adapter = new ObjectsListAdapter(ObjectsActivity.this, jsonArray);
        listView.setAdapter(adapter);
    }
}