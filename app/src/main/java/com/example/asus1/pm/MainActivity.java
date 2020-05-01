package com.example.asus1.pm;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus1.pm.CallUser.CallUserActivity;
import com.example.asus1.pm.PMValue.PMValueActivity;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends BaseActivity implements Handler.Callback{


    private static final String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;

    private TextView mScan;
    private RecyclerView mRecyclerView;
    private ArrayList<BluetoothDevice> mMatchedDevices = new ArrayList<>();
    private ArrayList<BluetoothDevice> mFindedDeviced = new ArrayList<>();
    private BluetoothListAdapter mListAdapter;

    private static int REQUEST_ENABLE_BT = 100;
    private static int REQUEST_ACCESS_COARSE_LOCATION = 200;
    private Handler mHanlder;
    public static final int MSG_DEVICE = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver,filter);
        mHanlder = new Handler(Looper.getMainLooper(),this);
        init();
    }

    private void init(){
        mScan = findViewById(R.id.tv_scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
            }
        });

        setBluetooth();
        mRecyclerView = findViewById(R.id.recycler_view);
        mListAdapter = new BluetoothListAdapter(this,mMatchedDevices,mFindedDeviced,mHanlder);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mListAdapter);
    }

    private boolean setBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.d(TAG, "setBluetooth: dont support bluetooth");
            return false;
        }

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }

        getPairedDevices();
        doDiscovery();
        return true;
    }


    private void getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mMatchedDevices.clear();
        mMatchedDevices.addAll(pairedDevices);
    }

    private void notifyData(){
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
            getPairedDevices();
            doDiscovery();
            notifyData();
        }else {
            Log.d(TAG, "onActivityResult: dont support bluetooth");
        }
    }

    private void doDiscovery(){
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);
            }else {
                mScan.setText("扫描中...");
                mScan.setClickable(false);
                mFindedDeviced.clear();
                mBluetoothAdapter.startDiscovery();

            }
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_DEVICE:
                String address = (String) msg.obj;
                Intent intent = new Intent();
                intent.putExtra("address",address);
                setResult(RESULT_OK,intent);
                finish();

        }
        return true;
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "onReceive: "+device.getName());
                if(device.getName() != null && !mFindedDeviced.contains(device)){
                    mFindedDeviced.add(device);
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mScan.setText("扫描");
                mScan.setClickable(true);
                notifyData();
                if (mFindedDeviced.size() == 0) {
                    Toast.makeText(MainActivity.this,"没有发现设备",Toast.LENGTH_SHORT).show();
                }
            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                Log.d(TAG, "onReceive: action start");
            }
        }
    };



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mScan.setText("扫描中...");
                mScan.setClickable(false);
                mFindedDeviced.clear();
                mBluetoothAdapter.startDiscovery();
            } else {
                Toast.makeText(this,"请打开相应权限",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
    }
}
