package com.example.ismael.genericapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ismael.genericapp.R;
import com.example.ismael.genericapp.entity.Recipe;

import java.util.List;


public class SpinnerAdapter extends ArrayAdapter<Recipe> {
    private Context context;
    private List<Recipe> values;

    public SpinnerAdapter(Context context, int textViewResourceId, List<Recipe> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;

    }

    public int getCount(){
        return values.size();
    }

    public Recipe getItem(int position){
        return values.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.spin_item, parent, false);

        TextView tv = (TextView) rootView.findViewById(R.id.spinner_item_text);

        tv.setText(values.get(position).getDescription());

        return rootView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.spin_item, parent, false);
        TextView tv = (TextView) rootView.findViewById(R.id.spinner_item_text);
        tv.setText(values.get(position).getDescription());

        return rootView;
    }

}
