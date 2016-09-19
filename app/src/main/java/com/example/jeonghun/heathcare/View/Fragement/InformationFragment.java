package com.example.jeonghun.heathcare.View.Fragement;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeonghun.heathcare.PushEvent;
import com.example.jeonghun.heathcare.R;
import com.example.jeonghun.heathcare.View.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment {

    private TextView muscleLoad;
    private TextView heartRate;
    private TextView cadence;
    private TextView speed;
    private TextView ratio;
    private TextView balance;

    private Handler handler;

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
        cadence = (TextView)view.findViewById(R.id.cadenceGagueTextView);
        speed = (TextView)view.findViewById(R.id.speedGaugeTextView);
        ratio = (TextView)view.findViewById(R.id.ratioGagueTextView);
        balance = (TextView)view.findViewById(R.id.balanceGaugeTextView);
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
                muscleLoad.setText(MainActivity.data);
                heartRate.setText(MainActivity.data);
                cadence.setText(MainActivity.data);
                speed.setText(MainActivity.data);
                ratio.setText(MainActivity.data);
                balance.setText(MainActivity.data);
            }
        });
    }

}
