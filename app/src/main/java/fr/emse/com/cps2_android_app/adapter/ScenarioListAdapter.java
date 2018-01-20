package fr.emse.com.cps2_android_app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import fr.emse.com.cps2_android_app.R;

/**
 * Created by julien on 20/01/2018.
 */

public class ScenarioListAdapter extends ArrayAdapter<JSONObject> {


    public ScenarioListAdapter(Context context, List list){
        super(context,0,list);
        Log.i("ScenarioListAdapter","Construc OK");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_result, parent, false);
        }

        RowViewHolder viewHolder = (RowViewHolder) convertView.getTag();
        if (viewHolder == null){
            viewHolder = new RowViewHolder();
            viewHolder.rowTitle = convertView.findViewById(R.id.rowTitle);
            viewHolder.rowLeftTitle = convertView.findViewById(R.id.rowRightTitle);
            viewHolder.rowHeader = convertView.findViewById(R.id.rowHeader);

            convertView.setTag(viewHolder);
        }

        JSONObject json = getItem(position);

        try {
            viewHolder.rowTitle.setText( String.format("Targets: %s/%s/%s/%s/%s",
                    json.getJSONObject("targets").getString("building"),
                    json.getJSONObject("targets").getString("floor"),
                    json.getJSONObject("targets").getString("room"),
                    json.getJSONObject("targets").getString("type"),
                    json.getJSONObject("targets").getString("name")));
            viewHolder.rowLeftTitle.setText("");
            String header="";
            // Browse the keys of the json object
            for (Iterator it = json.keys(); it.hasNext();) {
                // for each key, retrieve the associated json object
                // and append its own keys and values in the header string
                String key = (String) it.next();
                if ("targets".equals(key))
                    continue;
                header += key + ":\n";
                JSONObject j = json.getJSONObject(key);
                for (Iterator it2 = j.keys(); it2.hasNext();){
                    String param = (String) it2.next();
                    header += param + ": " + j.getString(param) + "\n";
                }
            }
            viewHolder.rowHeader.setText(header);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        convertView.setOnClickListener(null);
        return convertView;
    }

    public class RowViewHolder {
        public TextView rowTitle;
        public TextView rowLeftTitle;
        public TextView rowHeader;
    }
}
