package com.example.jeonghun.heathcare.View;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeonghun.heathcare.Adapter.CustomSpinnerAdapter;
import com.example.jeonghun.heathcare.Adapter.PagerAdapter;
import com.example.jeonghun.heathcare.Bluetooth.BluetoothReceiveService;
import com.example.jeonghun.heathcare.DB.Dao;
import com.example.jeonghun.heathcare.PushEvent;
import com.example.jeonghun.heathcare.R;
import com.example.jeonghun.heathcare.RecordData;
import com.example.jeonghun.heathcare.TwoData;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity {

    public static String data = "0";

    private static final String TAG = "BluetoothReceiveService";
    private static final int REQUEST_SET = 100;
    private static final int REQUEST_CONNECT_DEVICE = 101;
    private static final int REQUEST_BT_ON = 200;
    private static final boolean D = true;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private ViewPager viewPager;
    private ViewGroup viewGroup;
    private Spinner spinner;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothReceiveService mReceiveService = null;

    private boolean t = true;
    private boolean e = true;
    private boolean touchable = true;
    private MenuItem startItem;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private long backKeyPressedTime;

    private Dao dao;

    private Realm realm;
    private String[] sports = {"Not specified sport", "Running", "Cycling", "Mountain biking", "Walking", "Indoor cycling", "Triathlon", "Crosscountry skiing", "Treadmill"};
    private RealmList<TwoData> twoDatas = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent startIntent = new Intent(this, BluetoothOnActivity.class);
        startActivityForResult(startIntent, REQUEST_BT_ON);
        setupReceive();
        init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        dao = Dao.getInstance(getApplicationContext());
        timerValue = (TextView)findViewById(R.id.timer);
        spinner = (Spinner)findViewById(R.id.spinner);
        viewGroup = (ViewGroup)findViewById(R.id.container);
        spinner.setAdapter(new CustomSpinnerAdapter(getApplicationContext(), sports));
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        try {
            viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mReceiveService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mReceiveService.getState() == mReceiveService.STATE_NONE) {
                // Start the Bluetooth chat services
                mReceiveService.start();
            }
        }
    }

    private void init() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(MainActivity.this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }

    private void setupReceive(){
        Log.d(TAG, "setupReceive");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mReceiveService = new BluetoothReceiveService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

    }

    @Override
    protected synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mReceiveService != null) mReceiveService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        realm.close();
    }

    private final void setStatus(int resId) {
        //final ActionBar actionBar = getActionBar();
        //actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        //final ActionBar actionBar = getActionBar();
        //actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothReceiveService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothReceiveService.STATE_CONNECTED:
                            Log.d(TAG, "BluetoothReceiveService connected");
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothReceiveService.STATE_CONNECTING:
                            Log.d(TAG, "BluetoothReceiveService connecting");
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothReceiveService.STATE_LISTEN:
                        case BluetoothReceiveService.STATE_NONE:
                            Log.d(TAG, "BluetoothReceiveService none");
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    Log.d(TAG, "message write");
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:
                    Log.d(TAG, "message read");
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    final String readMessage = new String(readBuf, 0, msg.arg1);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, readMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                    try {
                        JSONObject jsonObject = new JSONObject(readMessage);
                        int left = Integer.valueOf(jsonObject.getString("left"));
                        int right = Integer.valueOf(jsonObject.getString("right"));

                        if (twoDatas != null) {
                            TwoData twoData = new TwoData();
                            twoData.setLeft(left);
                            twoData.setRight(right);
                            twoDatas.add(twoData);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    data = readMessage;

                    EventBus.getDefault().post(new PushEvent(data));

                    break;
                case MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "message device name");
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Log.d(TAG, "message toast");
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        try {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            // Attempt to connect to the device
            mReceiveService.connect(device, secure);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent serverIntent = null;
        MainActivity.println(TAG, resultCode);

        switch (resultCode){

            case BluetoothOnActivity.BLUETOOTH_NO: finish();
                break;

            case BluetoothOnActivity.BLUETOOTH_OK:
                println(TAG, "Bluetooth_OK");
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                break;

        }
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupReceive();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "back 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        startItem = menu.findItem(R.id.action_start);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent settingIntent;
        switch (item.getItemId()){
            case R.id.action_start:
                if (t) {
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 0);
                    item.setIcon(android.R.drawable.ic_media_pause);

                    if (twoDatas != null)
                        twoDatas = null;

                    twoDatas = new RealmList<>();

                } else {
                    timeSwapBuff += timeInMilliseconds;
                    customHandler.removeCallbacks(updateTimerThread);
                    item.setIcon(android.R.drawable.ic_media_play);
                }
                t = !t;
                e = false;
                break;
            case R.id.action_stop:
                if (!e){
                    registerExercise(timerValue.getText().toString());
                }
                t = e = true;

                try{
                    customHandler.removeCallbacks(updateTimerThread);
                    startItem.setIcon(android.R.drawable.ic_media_play);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.action_lock:
                JSONObject object = Dao.getData();
                try {
                    Toast.makeText(getApplicationContext(), object.getString("name"), Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.records:
                Intent intent = new Intent(this, RecordActivity.class);
                startActivity(intent);
                break;
            case R.id.action_BTscan:
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                break;
            case R.id.action_standard:
                settingIntent = new Intent(this, SettingListActivity.class);
                startActivityForResult(settingIntent, REQUEST_SET);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
/*
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
*/
    private void registerExercise(String data){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stop");
        builder.setMessage("Do you want to register your exercise?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (twoDatas != null) {
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RecordData recordData = realm.createObject(RecordData.class);
                            recordData.setToday(new Date());
                            recordData.setTwoDatas(twoDatas);
                            recordData.setSportTime(timerValue.getText().toString());
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "운동기록을 저장하였습니다.", Toast.LENGTH_SHORT).show();
                            timerValue.setText("" + 0 + ":"
                                    + String.format("%02d", 0) + ":"
                                    + String.format("%03d", 0));
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            Toast.makeText(getApplicationContext(), "저장에 실패하였습니다.\n" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            timerValue.setText("" + 0 + ":"
                                    + String.format("%02d", 0) + ":"
                                    + String.format("%03d", 0));
                        }
                    });

                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timerValue.setText("" + 0 + ":"
                        + String.format("%02d", 0) + ":"
                        + String.format("%03d", 0));
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();

    }

    public static void println(String tag, String str){
        Log.d(tag, str);
    }


    public static void println(String tag, int val){
        Log.d(tag, String.valueOf(val));
    }

}
