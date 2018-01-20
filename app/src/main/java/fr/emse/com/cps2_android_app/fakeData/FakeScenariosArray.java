package fr.emse.com.cps2_android_app.fakeData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by julien on 20/01/18.
 */

public class FakeScenariosArray {

    public static ArrayList<JSONObject> fakeScenariosArray() {
        String obj1 = "{\"_id\":{\"$oid\":\"5a60ecaf47146f41752956da\"},\"scenario_id\":1,\"scenario_name\":\"blackout\",\"configs\":[{\"targets\":{\"building\":\"All\",\"floor\":\"All\",\"room\":\"All\",\"type\":\"All\",\"name\":\"All\"},\"config\":{\"publishing_mode\":\"on-demand\",\"response_latency\":0}},{\"targets\":{\"building\":\"All\",\"floor\":\"All\",\"room\":\"All\",\"type\":\"Lamp\",\"name\":\"All\"},\"fields\":{\"status\":\"OFF\"}},{\"targets\":{\"building\":\"All\",\"floor\":\"All\",\"room\":\"All\",\"type\":\"SystemData Publisher\",\"name\":\"All\"},\"fields\":{\"status\":\"OFF\"}}]}";
        JSONObject json1 = null;
        String obj2 = "{\"_id\":{\"$oid\":\"5a635cdc47146f6c2252b8f1\"},\"scenario_id\":2,\"scenario_name\":\"christmas\",\"configs\":[{\"targets\":{\"building\":\"All\",\"floor\":\"All\",\"room\":\"All\",\"type\":\"Lamp\",\"name\":\"All\"},\"fields\":{\"status\":\"ON\"}}]}";
        JSONObject json2 = null;
        String obj3 = "{\"_id\":{\"$oid\":\"5a63709247146f0d2bb9dd4a\"},\"scenario_id\":3,\"scenario_name\":\"evacuation\",\"configs\":[{\"targets\":{\"building\":\"EF\",\"floor\":\"All\",\"room\":\"All\",\"type\":\"Door\",\"name\":\"All\"},\"fields\":{\"status\":\"Open\"}},{\"targets\":{\"building\":\"EF\",\"floor\":\"All\",\"room\":\"All\",\"type\":\"Ringer\",\"name\":\"All\"},\"fields\":{\"status\":\"ON\"}}]}";
        JSONObject json3 = null;

        try {
            json1 = new JSONObject(obj1);
            json2 = new JSONObject(obj2);
            json3 = new JSONObject(obj3);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<JSONObject> fakeArray = new ArrayList<JSONObject>(
                Arrays.asList(json1, json2, json3)
        );
        return fakeArray;
    }
}
