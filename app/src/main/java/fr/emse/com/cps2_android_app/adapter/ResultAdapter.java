package fr.emse.com.cps2_android_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fr.emse.com.cps2_android_app.HelpActivity;
import fr.emse.com.cps2_android_app.ObjectsActivity;
import fr.emse.com.cps2_android_app.R;

/**
 * Created by Valentin on 16/01/2018.
 */

public class ResultAdapter extends ArrayAdapter<JSONObject> {


    public ResultAdapter(Context context, List list){
        super(context,0,list);
        Log.i("ResultAdapter","Construc OK");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_result, parent, false);
        }

        RowViewHolder viewHolder = (RowViewHolder) convertView.getTag();
        if (viewHolder == null){
            viewHolder = new RowViewHolder();
            viewHolder.rowTitle = (TextView) convertView.findViewById(R.id.rowTitle);
            viewHolder.rowLeftTitle = (TextView) convertView.findViewById(R.id.rowRightTitle);
            viewHolder.rowHeader = (TextView) convertView.findViewById(R.id.rowHeader);
            viewHolder.rowDescription = (TextView) convertView.findViewById(R.id.rowDescription);

            convertView.setTag(viewHolder);
        }


        JSONObject json = getItem(position);



        try {
            viewHolder.rowTitle.setText( json.getString("title") );
            viewHolder.rowLeftTitle.setText(json.getString("title2"));
            viewHolder.rowHeader.setText( json.getString("header") );
            viewHolder.rowDescription.setText(json.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent objectInfoActivity = new Intent(ObjectInfoActivity.this, HelpActivity.class);
                startActivity(objectInfoActivity);
            }
        });
        return convertView;
    }


    public class RowViewHolder {
        public TextView rowTitle;
        public TextView rowLeftTitle;
        public TextView rowHeader;
        public TextView rowDescription;
    }



}
