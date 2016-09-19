package com.example.jeonghun.heathcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jeonghun.heathcare.R;

/**
 * Created by JeongHun on 16. 5. 4..
 */
public class CustomListAdapter extends BaseAdapter {

    private Context context;
    private String[] titles = {"내 설정", "어플리케이션 정보"};
    private String[] contents = {"나의 정보를 설정합니다.", "어플리케이션 정보를 확인합니다."};

    public CustomListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null){
            LayoutInflater inflater  = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_row, parent, false);
        }
        TextView textView1 = (TextView)row.findViewById(R.id.row_title);
        TextView textView2 = (TextView)row.findViewById(R.id.row_content);

        textView1.setText(titles[position]);
        textView2.setText(contents[position]);

        return row;
    }
}
