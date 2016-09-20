package com.example.jeonghun.heathcare.View;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jeonghun.heathcare.Adapter.CustomSpinnerAdapter;
import com.example.jeonghun.heathcare.R;
import com.example.jeonghun.heathcare.RecordData;
import com.example.jeonghun.heathcare.View.MainActivity;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

public class RecordActivity extends AppCompatActivity {

    private static String TAG = "RecordActivity";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private Spinner spinner;

    private Realm realm;
    private GraphView graphView;
    private RealmResults<RecordData> recordDatas;
    private ArrayList<String> days;
    private CustomSpinnerAdapter spinnerAdapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void init() {
        textView = (TextView)findViewById(R.id.nowDate);
        spinner = (Spinner)findViewById(R.id.selectDate);
        days = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        graphView = (GraphView)findViewById(R.id.graph);
        recordDatas = realm.where(RecordData.class).findAll();
        for (RecordData data : recordDatas) {
            days.add(simpleDateFormat.format(data.getToday()));
            MainActivity.println(TAG, String.valueOf(data.getTwoDatas().size()) + " : "  + data.toString());
        }
        spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), Arrays.copyOf(days.toArray(), days.toArray().length, String[].class));
        spinner.setAdapter(spinnerAdapter);
        spinner.invalidate();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                graphView.removeAllSeries();
                RecordData recordData = recordDatas.get(position);
                LineGraphSeries<DataPoint> lefts;
                LineGraphSeries<DataPoint> rights;
                ArrayList<DataPoint> leftDataPoints = new ArrayList<>();
                ArrayList<DataPoint> rightDataPoints = new ArrayList<>();

                for (int i = 0; i < recordData.getTwoDatas().size(); i++) {
                    leftDataPoints.add(new DataPoint(i, recordData.getTwoDatas().get(i).getLeft()));
                    rightDataPoints.add(new DataPoint(i, recordData.getTwoDatas().get(i).getRight()));
                    Log.e(TAG, String.valueOf(i) + " : " + String.valueOf(recordData.getTwoDatas().get(i).getLeft()));
                    Log.e(TAG, String.valueOf(i) + " : " + String.valueOf(recordData.getTwoDatas().get(i).getRight()));

                }
                lefts = new LineGraphSeries<>(Arrays.copyOf(leftDataPoints.toArray(), leftDataPoints.toArray().length, DataPoint[].class));
                rights = new LineGraphSeries<>(Arrays.copyOf(rightDataPoints.toArray(), rightDataPoints.toArray().length, DataPoint[].class));
                textView.setText(simpleDateFormat.format(recordData.getToday()));
                lefts.setColor(Color.RED);
                graphView.addSeries(lefts);
                graphView.addSeries(rights);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                RecordData recordData = recordDatas.get(recordDatas.size()-1);
                BarGraphSeries<DataPoint> lefts;
                BarGraphSeries<DataPoint> rights;
                ArrayList<DataPoint> leftDataPoints = new ArrayList<>();
                ArrayList<DataPoint> rightDataPoints = new ArrayList<>();
                for (int i = 0; i < recordData.getTwoDatas().size(); i++) {
                    leftDataPoints.add(new DataPoint(i, recordData.getTwoDatas().get(recordDatas.size()-1).getLeft()));
                    rightDataPoints.add(new DataPoint(i, recordData.getTwoDatas().get(recordDatas.size()-1).getRight()));
                }
                lefts = new BarGraphSeries<>(Arrays.copyOf(leftDataPoints.toArray(), leftDataPoints.toArray().length, DataPoint[].class));
                rights = new BarGraphSeries<>(Arrays.copyOf(rightDataPoints.toArray(), rightDataPoints.toArray().length, DataPoint[].class));
                textView.setText(simpleDateFormat.format(recordData.getToday()));
                graphView.addSeries(lefts);
                graphView.addSeries(rights);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }



}
