package com.example.jeonghun.heathcare.View;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jeonghun.heathcare.Adapter.CustomListAdapter;
import com.example.jeonghun.heathcare.R;

public class SettingListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView listView;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView)findViewById(R.id.listView);
        adapter = new CustomListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;

        switch (position){
            case 0: intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;

            case 1: intent = new Intent(this, InformationActivity.class);
                startActivity(intent);
                break;

        }
    }
}
