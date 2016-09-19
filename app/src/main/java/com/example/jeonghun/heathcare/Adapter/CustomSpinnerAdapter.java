package com.example.jeonghun.heathcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jeonghun.heathcare.R;

/**
 * Created by JeongHun on 16. 5. 23..
 */
public class CustomSpinnerAdapter extends BaseAdapter{

    Context context;
    String[] values;

    public CustomSpinnerAdapter(Context context, String[] values) {
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return values[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if(row == null){

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.spinner_text, parent, false);
        }

        TextView textView = (TextView)row.findViewById(R.id.spinner_text);

        textView.setText(values[position]);

        return row;
    }

}
