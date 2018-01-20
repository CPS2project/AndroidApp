package fr.emse.com.cps2_android_app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;

import fr.emse.com.cps2_android_app.adapter.ObjectsListAdapter;
import fr.emse.com.cps2_android_app.network.AsyncResponse;
import fr.emse.com.cps2_android_app.network.DownloadMongoDocuments;

public class ObjectsActivity extends NavigationHelperActivity
        implements AsyncResponse{

    private DownloadMongoDocuments asyncTask = new DownloadMongoDocuments();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_objects);
        super.onCreate(savedInstanceState);

        NavigationView nav = findViewById(R.id.nav_view);
        nav.setCheckedItem(R.id.nav_objects);

        // The next two lines fill the listView with real data from MongoDB
        asyncTask.delegate = this;
        asyncTask.execute("objects");

        // The next line fills the listView with fake data (offline test)
//        this.processFinish(FakeObjectsArray.fakeObjectsArray());
    }

    @Override
    public void processFinish(ArrayList<JSONObject> output) {
        ListView listView = findViewById(R.id.listView);
        ObjectsListAdapter adapter = new ObjectsListAdapter(ObjectsActivity.this, output);
        listView.setAdapter(adapter);
    }
}
