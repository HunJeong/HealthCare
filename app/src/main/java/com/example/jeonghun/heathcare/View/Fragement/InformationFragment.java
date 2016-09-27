package com.example.jeonghun.heathcare.View.Fragement;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeonghun.heathcare.PushEvent;
import com.example.jeonghun.heathcare.R;
import com.example.jeonghun.heathcare.View.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment {

    private static final String TAG = "Fragment";

    private TextView muscleLoad;
    private TextView heartRate;
    private TextView power;
    private TextView ratio;
    private TextView balance;

    private Handler handler;

    private int diff = 0;

    public InformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);

        handler = new Handler();

        muscleLoad = (TextView)view.findViewById(R.id.muscleLoadGaugeTextView);
        heartRate = (TextView)view.findViewById(R.id.heartRateGauge);
        ratio = (TextView)view.findViewById(R.id.ratioGagueTextView);
        balance = (TextView)view.findViewById(R.id.balanceGaugeTextView);
        power = (TextView)view.findViewById(R.id.power);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onPushEvent(PushEvent pushEvent){
        pushEvent.getData();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (MainActivity.LEFT_AVERAGE) {
                        synchronized (MainActivity.RIGHT_AVERAGE) {
                            Log.e(TAG, String.valueOf(MainActivity.LEFT_AVERAGE) + " : " + String.valueOf(MainActivity.RIGHT_AVERAGE));
                            JSONObject jsonObject = new JSONObject(MainActivity.data);
                            diff = (int)(MainActivity.LEFT_AVERAGE*0.5) - (int)(MainActivity.RIGHT_AVERAGE*0.5);
                            int left = Integer.valueOf(jsonObject.getString("left")) - (int)(MainActivity.LEFT_AVERAGE*0.5);
                            int right = Integer.valueOf(jsonObject.getString("right")) - (int)(MainActivity.RIGHT_AVERAGE*0.5);
                            float balanc = (float) Math.abs(left - right) / left * 100;
                            Log.d(TAG, String.valueOf(MainActivity.LEFT_AVERAGE) + " : " + String.valueOf(left) + " : " + String.valueOf(MainActivity.RIGHT_AVERAGE) + " : " + String.valueOf(right));
                            if (Math.abs(right - left) <= 25) {
                                power.setText("=");
                            } else if (right > left) {
                                power.setText("<");
                            } else {
                                power.setText(">");
                            }
                            balanc = (balanc > 100) ? 0 : 100 - balanc;
                            muscleLoad.setText(String.valueOf(left));
                            heartRate.setText(String.valueOf(right));
                            ratio.setText("0");
                            balance.setText(String.valueOf(balanc));
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

}
