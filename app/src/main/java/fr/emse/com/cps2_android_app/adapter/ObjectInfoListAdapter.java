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
 * Created by Valentin on 21/01/2018.
 */

public class ObjectInfoListAdapter extends ArrayAdapter<JSONObject> {


    public ObjectInfoListAdapter(Context context, List list){
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
        viewHolder.rowTitle.setText("");
        viewHolder.rowLeftTitle.setText("");
        viewHolder.rowHeader.setText("");


        try {
            viewHolder.rowTitle.setText(json.getString("label"));
            viewHolder.rowLeftTitle.setText(json.getString("value"));


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
