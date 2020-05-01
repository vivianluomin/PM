package com.example.asus1.pm;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus1.pm.CallUser.CallUserActivity;
import com.example.asus1.pm.PMValue.PMValueActivity;
import com.github.clans.fab.FloatingActionButton;

public class ConnectAcitivy extends BaseActivity implements Handler.Callback,View.OnClickListener{

    private BluetoothDevice mDevice;
    private TextView mDeviceName;
    private ImageView mBack;
    private BluetoothService mService;
    private Handler mHanlder;
    private TextView mScan;
    private BluetoothAdapter mAdapter;
    private CardView mCard;
    private TextView mPMValue;
    private TextView mUpdtePM;
    private FloatingActionButton mSettingFloat;
    private FloatingActionButton mCallUserFloat;
    private ImageView mPMBackground;

    private Float mDefaultPM;

    private static final String TAG = "ConnectAcitivy";
    public static final int SCAN_DEVICES = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_acitivy);
        mHanlder = new Handler(Looper.getMainLooper(),this);
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mService = new BluetoothService(this,mHanlder);
        init();
    }

    private void init(){

        mDeviceName = findViewById(R.id.tv_device_name);

        mScan = findViewById(R.id.tv_scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectAcitivy.this,MainActivity.class);
                startActivityForResult(intent,SCAN_DEVICES);
            }
        });

        mSettingFloat = findViewById(R.id.float_btn_setting);
        mSettingFloat.setOnClickListener(this);
        mCallUserFloat = findViewById(R.id.float_btn_call);
        mCallUserFloat.setOnClickListener(this);
        mCard = findViewById(R.id.card_pm);
        mPMValue = findViewById(R.id.tv_pm_value);
        mUpdtePM = findViewById(R.id.tv_update_pm);
        mUpdtePM.setOnClickListener(this);
        mPMBackground = findViewById(R.id.iv_pm);

        String value = (String) AppSharePreferenceMrg.get(AppSharePreferenceMrg.PM_VALUE,
                this,"value","0");
        mDefaultPM = Float.valueOf(value);

    }

    private void connect(){
        mService.connect(mDevice,false);
    }

    private void ensureDiscoverable() {
        if (mAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        if (mService != null) {
            if (mService.getState() == BluetoothService.STATE_NONE) {
                Log.d(TAG, "onResume: ");
                mService.start();
            }
        }
        //ensureDiscoverable();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case Constants.MESSAGE_READ:
                int size = msg.arg1;
                byte[] buffer = (byte[]) msg.obj;
                String readMessage = new String(buffer,0,size);
                Log.d(TAG, "handleMessage: read "+readMessage);
                mPMValue.setText("PM = "+readMessage);
                if(Float.valueOf(readMessage) > mDefaultPM){
                    mPMBackground.setBackgroundResource(R.mipmap.bg_pm);
                }else {
                    mPMBackground.setBackgroundResource(R.mipmap.bg_sky);
                }
                break;
            case Constants.MESSAGE_STATE_CHANGE:
            switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    Log.d(TAG, "handleMessage: STATE_CONNECTED");

                    if(mDevice != null){
                        mDeviceName.setText(mDevice.getName());
                    }
                    sendMessage("1");
                    break;
                case BluetoothService.STATE_CONNECTING:
                    Log.d(TAG, "handleMessage: STATE_CONNECTING");
                    mDeviceName.setText("连接中...");
                    break;
                case BluetoothService.STATE_LISTEN:
                    Log.d(TAG, "handleMessage: STATE_LISTEN");
                    break;
                case BluetoothService.STATE_NONE:
                    Log.d(TAG, "handleMessage: STATE_NONE");
                    break;
                case BluetoothService.STATE_SERVER_CONNECTED:
                    Log.d(TAG, "handleMessage: STATE_SERVER_CONNECTED");
                    mDeviceName.setText((String)msg.obj);
            }
            break;

            case Constants.MESSAGE_WRITE:
                String messge = new String((byte[])msg.obj);
                Log.d(TAG, "handleMessage: "+messge);

        }
        return true;
    }

    private void sendMessage(String message) {
        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mService.write(send);

        }
        mService.setNeedRead(true);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.float_btn_call:
                startActivity(new Intent(this, CallUserActivity.class));
                break;

            case R.id.float_btn_setting:
                startActivity(new Intent(this, PMValueActivity.class));
                break;
            case R.id.tv_update_pm:
                sendMessage("1");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_DEVICES && resultCode == RESULT_OK){
            String address = data.getStringExtra("address");
            Log.d(TAG, "onActivityResult: address: "+address);
            mDevice = mAdapter.getRemoteDevice(address);
            Log.d(TAG, "onActivityResult: device name: "+mDevice.getName());
            mDeviceName.setText(mDevice.getName());
            mCard.setVisibility(View.VISIBLE);
            mUpdtePM.setVisibility(View.VISIBLE);
            connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
        }
    }
}
