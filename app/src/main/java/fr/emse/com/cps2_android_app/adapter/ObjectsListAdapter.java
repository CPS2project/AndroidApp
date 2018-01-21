package fr.emse.com.cps2_android_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mongodb.util.JSONSerializers;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.List;

import fr.emse.com.cps2_android_app.HelpActivity;
import fr.emse.com.cps2_android_app.ObjectInfoActivity;
import fr.emse.com.cps2_android_app.ObjectsActivity;
import fr.emse.com.cps2_android_app.R;

/**
 * Created by Valentin on 16/01/2018.
 */

public class ObjectsListAdapter extends ArrayAdapter<JSONObject> {

    private Context context;

    public ObjectsListAdapter(Context context, List list){
        super(context,0,list);
        this.context = context;
        Log.i("ObjectsListAdapter","Construc OK");
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

        final JSONObject json = getItem(position);

        try {
            viewHolder.rowTitle.setText( json.getJSONObject("object_name").getString("value"));
            viewHolder.rowLeftTitle.setText(json.getJSONObject("object_type").getString("value"));
            viewHolder.rowHeader.setText(String.format("%s - %s - %s",
                    json.getJSONObject("building").getString("value"),
                    json.getJSONObject("floor").getString("value"),
                    json.getJSONObject("room").getString("value")));
        } catch (JSONException e) {
            e.printStackTrace();
        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent objectInfoActivity = new Intent(context, ObjectInfoActivity.class);
                objectInfoActivity.putExtra("JSONOBJECT_SELECTED",json.toString());
                context.startActivity(objectInfoActivity);
            }
        });
        return convertView;
    }

    public class RowViewHolder {
        public TextView rowTitle;
        public TextView rowLeftTitle;
        public TextView rowHeader;
    }
}
