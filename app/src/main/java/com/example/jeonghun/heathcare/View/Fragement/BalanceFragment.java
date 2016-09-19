package com.example.jeonghun.heathcare.View.Fragement;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jeonghun.heathcare.PushEvent;
import com.example.jeonghun.heathcare.R;
import com.example.jeonghun.heathcare.View.MainActivity;

import org.greenrobot.eventbus.Subscribe;

import org.greenrobot.eventbus.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class BalanceFragment extends Fragment {

    ProgressBar progressBar;
    TextView textView;
    Handler handler = new Handler();

    public BalanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this);
        return inflater.inflate(R.layout.fragment_balance, container, false);
    }

    @Subscribe
    public void onPushEvent(PushEvent mPushEvent){
        mPushEvent.getData();
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(Integer.valueOf(MainActivity.data));
                textView.setText(MainActivity.data);
                progressBar.invalidate();
                textView.invalidate();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        textView = (TextView)view.findViewById(R.id.textViewBalance);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
