package fr.emse.com.cps2_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.emse.com.cps2_android_app.adapter.ResultAdapter;
import fr.emse.com.cps2_android_app.fakeData.FakeObjectsArray;
import fr.emse.com.cps2_android_app.network.AsyncResponse;
import fr.emse.com.cps2_android_app.network.DownloadMongoDocuments;

public class ObjectsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncResponse{

    DownloadMongoDocuments asyncTask = new DownloadMongoDocuments();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objects);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // The next two lines fill the listView with real data from MongoDB
        asyncTask.delegate = this;
        asyncTask.execute("objects");

        // The next line fills the listView with fake data (offline test)
//        this.processFinish(FakeObjectsArray.fakeObjectsArray());
        // End of bloc
    }

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
            Intent helpActivity = new Intent(ObjectsActivity.this, HelpActivity.class);
            startActivity(helpActivity);
        } else if (id == R.id.nav_query) {
            Intent queryActivity = new Intent(ObjectsActivity.this, QueryActivity.class);
            startActivity(queryActivity);
        } else if (id == R.id.nav_scenario) {
            Intent scenarioActivity = new Intent(ObjectsActivity.this, ScenarioActivity.class);
            startActivity(scenarioActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void processFinish(ArrayList<JSONObject> jsonArray) {
        ListView listView = findViewById(R.id.listView);
        ResultAdapter adapter = new ResultAdapter(ObjectsActivity.this, jsonArray);
        listView.setAdapter(adapter);
    }
}
