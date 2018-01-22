package fr.emse.com.cps2_android_app.network;

import android.os.AsyncTask;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by julien on 17/01/18.
 */

public class DownloadMongoDocuments extends AsyncTask<String, Integer, ArrayList<JSONObject>> {

    public JsonAsyncResponse delegate = null;

    @Override
    protected ArrayList<JSONObject> doInBackground(String... strings) {
        // Get the JSON documents from MongoDB
        MongoClient mongoClient = new MongoClient(new ServerAddress("192.168.43.48", 27017));
        MongoDatabase database = mongoClient.getDatabase("cps2_project");
        MongoCollection<Document> collection = database.getCollection(strings[0]);

        // Insert the documents in a list to send to the adapter
        ArrayList<JSONObject> jsonArray = new ArrayList<>();
        JSONObject json;
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()) {
                json = new JSONObject(cursor.next().toJson());
//                System.out.println(json);
                jsonArray.add(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return jsonArray;
    }

    @Override
    protected void onPostExecute(ArrayList<JSONObject> output) {
        try {
            delegate.processFinish(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
