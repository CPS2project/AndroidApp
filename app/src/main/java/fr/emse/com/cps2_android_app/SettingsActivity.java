package fr.emse.com.cps2_android_app;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SettingsActivity extends NavigationHelperActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_settings);
        super.onCreate(savedInstanceState);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_settings);

        // Try to read existing hosts to display them in the textViews
        try {
            FileInputStream fin = openFileInput("hosts");
            int c;
            String temp="";
            while( (c = fin.read()) != -1){
                temp = temp + Character.toString((char)c);
            }
            String mqtt_host;
            String mongo_host;
            String influx_host;
            boolean unique_server;
            if (temp.contains(";")){
                // there are multiple hosts
                mqtt_host = temp.split(";")[0];
                mongo_host = temp.split(";")[1];
                influx_host = temp.split(";")[2];
                unique_server = false;
            }
            else {
                mqtt_host = temp;
                mongo_host = temp;
                influx_host = temp;
                unique_server = true;
            }
            ((EditText) findViewById(R.id.mqtt_host)).setText(mqtt_host);
            ((EditText) findViewById(R.id.mongo_host)).setText(mongo_host);
            ((EditText) findViewById(R.id.influx_host)).setText(influx_host);
            ((CheckBox) findViewById(R.id.unique_server_check)).setChecked(unique_server);
            toggleHosts(!unique_server);
        }
        catch(Exception e){
        }
    }

    public void toggleHosts(boolean status){
        EditText mongo_host = findViewById(R.id.mongo_host);
        EditText influx_host = findViewById(R.id.influx_host);

        mongo_host.setEnabled(status);
        influx_host.setEnabled(status);
    }

    public void onCheckboxClicked(View view){
        CheckBox check = findViewById(R.id.unique_server_check);
        Boolean status = !check.isChecked();
        toggleHosts(status);
    }

    public void onApplyButtonClicked(View view) {

        String hosts =  ((TextView) findViewById(R.id.mqtt_host)).getText().toString();

        CheckBox check = findViewById(R.id.unique_server_check);
        if (check.isChecked()){
            ((EditText) findViewById(R.id.mongo_host)).setText(hosts);
            ((EditText) findViewById(R.id.influx_host)).setText(hosts);
        }
        else {
            hosts += ";" + ((TextView) findViewById(R.id.mongo_host)).getText().toString();
            hosts += ";" + ((TextView) findViewById(R.id.influx_host)).getText().toString();
        }
        String filename = "hosts";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(hosts.getBytes());
            outputStream.close();
            Toast.makeText(getBaseContext(),"hosts saved",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
