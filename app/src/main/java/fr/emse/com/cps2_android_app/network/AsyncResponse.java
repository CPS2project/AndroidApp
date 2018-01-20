package fr.emse.com.cps2_android_app.network;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by julien on 19/01/18.
 */

public interface AsyncResponse {
    void processFinish(ArrayList<JSONObject> output);
}
