package fr.emse.com.cps2_android_app.network;

import android.os.AsyncTask;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by julien on 22/01/18.
 */

public class QueryInfluxDB extends AsyncTask<String, Integer, String> {

    public StringAsyncResponse delegate = null;

    @Override
    protected String doInBackground(String... strings) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://192.168.43.48:8086", "", "");
        influxDB.query(new Query("SELECT \"numericValue\" FROM \"cps2_project\".\"autogen\".\"Lamp\" WHERE \"field_name\"='brightness' LIMIT 1",
                "cps2_project"), queryResult -> {
            // Do something with the result...
            System.out.println(queryResult.toString());
        }, throwable -> {
            // Do something with the error...
        });
        return null;
    }

    @Override
    protected void onPostExecute(String output) {
            delegate.processFinish(output);
    }
}
