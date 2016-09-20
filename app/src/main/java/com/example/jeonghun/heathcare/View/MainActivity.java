package com.example.jeonghun.heathcare.View;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IntegerRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jeonghun.heathcare.Adapter.CustomSpinnerAdapter;
import com.example.jeonghun.heathcare.Adapter.PagerAdapter;
import com.example.jeonghun.heathcare.Bluetooth.BluetoothReceiveService;
import com.example.jeonghun.heathcare.Bluetooth.BluetoothSerialClient;
import com.example.jeonghun.heathcare.DB.Dao;
import com.example.jeonghun.heathcare.PushEvent;
import com.example.jeonghun.heathcare.R;
import com.example.jeonghun.heathcare.RecordData;
import com.example.jeonghun.heathcare.TwoData;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity {

    public static String data = "0";

    private static final int REQUEST_CONNECT_DEVICE = 101;
    private static final boolean D = true;

    public static Integer LEFT_AVERAGE = 0;
    public static Integer RIGHT_AVERAGE = 0;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_SET = 100;
    private static final int REQUEST_BT_ON = 200;

    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private ViewPager viewPager;
    private ViewGroup viewGroup;
    private Spinner spinner;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSerialClient mSerialClient = null;

    private boolean t = true;
    private boolean e = true;
    private Boolean running = false;
    private MenuItem startItem;
    private TextView timerValue;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private long backKeyPressedTime;

    private Realm realm;
    private String[] sports = {"Not specified sport", "Running", "Cycling", "Mountain biking", "Walking", "Indoor cycling", "Triathlon", "Crosscountry skiing", "Treadmill"};
    private ArrayList<TwoData> twoDatas = null;

    private Dao dao;

    private LinkedList<BluetoothDevice> mBluetoothDevices = new LinkedList<>();
    private ArrayAdapter<String> mDeviceArrayAdapter;
    private AlertDialog mDeviceListDialog;
    private ProgressDialog mLoadingDialog;
    private Menu mMenu;

    private Integer num = 0;

    ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent startIntent = new Intent(this, BluetoothOnActivity.class);
        startActivityForResult(startIntent, REQUEST_BT_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSerialClient = BluetoothSerialClient.getInstance();
        if (mBluetoothAdapter == null || mSerialClient == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        init();

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
        enableBluetooth();
    }

    @Override
    protected void onPause() {
        mSerialClient.cancelScan(getApplicationContext());
        super.onPause();
    }

    private void init() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(MainActivity.this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();

        mSerialClient = BluetoothSerialClient.getInstance();
        if (mSerialClient.isEnabled()) {
            mSerialClient.enableBluetooth(getApplicationContext(), new BluetoothSerialClient.OnBluetoothEnabledListener() {
                @Override
                public void onBluetoothEnabled(boolean success) {

                }
            });
        }
        initDeviceListDialog();
        initProgressDialog();
    }

    private void initProgressDialog() {
        mLoadingDialog = new ProgressDialog(this);
        mLoadingDialog.setCancelable(false);
    }

    private void initDeviceListDialog() {
        mDeviceArrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        ListView listView = new ListView(getApplicationContext());
        listView.setBackgroundColor(Color.BLACK);
        listView.setAdapter(mDeviceArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item =  (String) parent.getItemAtPosition(position);
                for(BluetoothDevice device : mBluetoothDevices) {
                    if(item.contains(device.getAddress())) {
                        connect(device);
                        mDeviceListDialog.cancel();
                    }
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select bluetooth device");
        builder.setView(listView);
        builder.setPositiveButton("Scan",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        scanDevices();
                    }
                });
        mDeviceListDialog = builder.create();
        mDeviceListDialog.setCanceledOnTouchOutside(false);
    }

    private void addDeviceToArrayAdapter(BluetoothDevice device) {
        if(mBluetoothDevices.contains(device)) {
            mBluetoothDevices.remove(device);
            mDeviceArrayAdapter.remove(device.getName() + "\n" + device.getAddress());
        }
        mBluetoothDevices.add(device);
        mDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress() );
        mDeviceArrayAdapter.notifyDataSetChanged();

    }

    private void enableBluetooth() {
        BluetoothSerialClient btSet =  mSerialClient;
        btSet.enableBluetooth(this, new BluetoothSerialClient.OnBluetoothEnabledListener() {
            @Override
            public void onBluetoothEnabled(boolean success) {
                if(success) {
                    getPairedDevices();
                } else {
                    finish();
                }
            }
        });
    }

    private void getPairedDevices() {
        Set<BluetoothDevice> devices =  mSerialClient.getPairedDevices();
        for(BluetoothDevice device: devices) {
            addDeviceToArrayAdapter(device);
        }
    }

    private void scanDevices() {
        BluetoothSerialClient btSet = mSerialClient;
        btSet.scanDevices(getApplicationContext(), new BluetoothSerialClient.OnScanListener() {
            String message ="";
            @Override
            public void onStart() {
                Log.d("Test", "Scan Start.");
                mLoadingDialog.show();
                message = "Scanning....";
                mLoadingDialog.setMessage("Scanning....");
                mLoadingDialog.setCancelable(true);
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        BluetoothSerialClient btSet = mSerialClient;
                        btSet.cancelScan(getApplicationContext());
                    }
                });
            }

            @Override
            public void onFoundDevice(BluetoothDevice bluetoothDevice) {
                addDeviceToArrayAdapter(bluetoothDevice);
                message += "\n" + bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress();
                mLoadingDialog.setMessage(message);
            }

            @Override
            public void onFinish() {
                Log.d("Test", "Scan finish.");
                message = "";
                mLoadingDialog.cancel();
                mLoadingDialog.setCancelable(false);
                mLoadingDialog.setOnCancelListener(null);
                mDeviceListDialog.show();
            }
        });
    }


    private void connect(BluetoothDevice device) {
        mLoadingDialog.setMessage("Connecting....");
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.show();
        BluetoothSerialClient btSet =  mSerialClient;
        btSet.connect(getApplicationContext(), device, mBTHandler);
    }

    // The Handler that gets information back from the BluetoothReceiveService

    private BluetoothSerialClient.BluetoothStreamingHandler mBTHandler = new BluetoothSerialClient.BluetoothStreamingHandler() {
        ByteBuffer mmByteBuffer = ByteBuffer.allocate(1024);

        @Override
        public void onError(Exception e) {
            mLoadingDialog.cancel();
            mMenu.getItem(0).setTitle(R.string.action_connect);
            Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected() {
            mMenu.getItem(0).setTitle(R.string.action_connect);
            mLoadingDialog.cancel();
            Toast.makeText(getApplicationContext(), "onDisconnected", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onData(byte[] buffer, int length) {
            Log.e("jsotest", new String(buffer, 0, length));

            int left = 0, right = 0;

            if(length == 0) return;

            if(mmByteBuffer.position() + length >= mmByteBuffer.capacity()) {
                ByteBuffer newBuffer = ByteBuffer.allocate(mmByteBuffer.capacity() * 2);
                newBuffer.put(mmByteBuffer.array(), 0,  mmByteBuffer.position());
                mmByteBuffer = newBuffer;
            }
            mmByteBuffer.put(buffer, 0, length);
            if(buffer[length - 1] == '}') {
                String json = new String(mmByteBuffer.array(), 0, mmByteBuffer.position());
                data = json;
                EventBus.getDefault().post(new PushEvent(json));
                Log.e("json", json);
                mmByteBuffer.clear();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    left = Integer.valueOf(jsonObject.getString("left"));
                    right = Integer.valueOf(jsonObject.getString("right"));
                    if (progressDialog != null) {
                        synchronized (num) {
                            synchronized (LEFT_AVERAGE) {
                                synchronized (RIGHT_AVERAGE) {
                                    Log.e("num", num.toString());
                                    Log.e("num", String.valueOf(LEFT_AVERAGE) + " : " + String.valueOf(RIGHT_AVERAGE));

                                    if (num >= 8) {
                                        LEFT_AVERAGE /= 10;
                                        RIGHT_AVERAGE /= 10;
                                        LEFT_AVERAGE -= 150;
                                        RIGHT_AVERAGE -= 150;
                                        LEFT_AVERAGE = (LEFT_AVERAGE > 0) ? LEFT_AVERAGE : 0;
                                        RIGHT_AVERAGE = (RIGHT_AVERAGE > 0) ? RIGHT_AVERAGE : 0;
                                        num = 0;
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                        Log.e("num", String.valueOf(LEFT_AVERAGE) + " : " + String.valueOf(RIGHT_AVERAGE));

                                    }
                                    LEFT_AVERAGE += left;
                                    RIGHT_AVERAGE += right;
                                    num++;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (running) {
                    try {
                        Log.e("json", json);

                        if (twoDatas != null) {
                            TwoData twoData = new TwoData();
                            twoData.setLeft(left*2-LEFT_AVERAGE);
                            twoData.setRight(right*2-RIGHT_AVERAGE);
                            twoDatas.add(twoData);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            //Log.e("json", new String(mmByteBuffer.array(), 0, mmByteBuffer.position()));
            //Log.e("json", String.valueOf(length) + " : " + Arrays.toString(buffer));

        }

        @Override
        public void onConnected() {
            mLoadingDialog.cancel();
            mMenu.getItem(0).setTitle(R.string.action_disconnect);
            Toast.makeText(MainActivity.this, "onConnected", Toast.LENGTH_SHORT).show();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("잠시만 기다려주십시오");
            progressDialog.show();

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSerialClient.claer();
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
                break;

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
        mMenu = menu;
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

                    twoDatas = new ArrayList<>();
                    synchronized (running) {
                        running = true;
                    }

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
                    synchronized (running) {
                        running = false;
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
                boolean connect = mSerialClient.isConnection();
                if (!connect) {
                    mDeviceListDialog.show();
                } else {
                    mBTHandler.close();
                }
                break;
            case R.id.action_standard:
                settingIntent = new Intent(this, SettingListActivity.class);
                startActivityForResult(settingIntent, REQUEST_SET);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCodeDlg() {
        TextView codeView = new TextView(this);
        codeView.setText(Html.fromHtml(readCode()));
        codeView.setMovementMethod(new ScrollingMovementMethod());
        codeView.setBackgroundColor(Color.parseColor("#202020"));
        new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_DialogWhenLarge)
                .setView(codeView)
                .setPositiveButton("OK", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private String readCode() {
        try {
            InputStream is = getAssets().open("HC_06_Echo.txt"); //getAssets().open("HC_06_Echo.txt");
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            is.close();
            String code = new String(buffer);
            buffer = null;
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

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
                            for (TwoData tmp : twoDatas) {
                                recordData.getTwoDatas().add(tmp);
                                Log.e("test", tmp.toString());
                            }
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
