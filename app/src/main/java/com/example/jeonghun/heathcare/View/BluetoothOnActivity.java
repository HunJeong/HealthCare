package com.example.jeonghun.heathcare.View;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.jeonghun.heathcare.R;

public class BluetoothOnActivity extends AppCompatActivity {

    public static final int BLUETOOTH_OK = 1000;
    public static final int BLUETOOTH_NO = 1001;

    public static final int REQUEST_ENABLE_BT = 0;

    private static final int RESULT_OK = -1;
    private static final int RESULT_NO = 0;
    private static final String TAG = "BluetoothOnActivity";

    private BluetoothAdapter btAdapter;

    Handler handler;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setup();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_on);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        mActivity = this;
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (btAdapter.getState() == BluetoothAdapter.STATE_OFF){
                    requestBluetoothEnable(mActivity);
                }
                else {
                    setResult(BLUETOOTH_OK);
                    finish();
                }
            }
        }, 2000);

    }

    public void requestBluetoothEnable(Activity activity) {
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){

            case RESULT_OK: setResult(BLUETOOTH_OK);
                MainActivity.println(TAG, "OK");
                finish();
                break;

            case RESULT_NO: setResult(BLUETOOTH_NO);
                finish();
                break;

            default: setResult(BLUETOOTH_NO);
                break;

        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setup(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }


    public static void println(String tag, String str){
        Log.d(tag, str);
    }


    public static void println(String tag, int val){
        Log.d(tag, String.valueOf(val));
    }

}
